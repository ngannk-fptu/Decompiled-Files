/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import org.hibernate.QueryException;
import org.hibernate.hql.internal.classic.ParserHelper;
import org.hibernate.internal.util.StringHelper;

public class ParameterParser {
    private ParameterParser() {
    }

    public static void parse(String sqlString, Recognizer recognizer) throws QueryException {
        boolean hasMainOutputParameter = ParameterParser.startsWithEscapeCallTemplate(sqlString);
        boolean foundMainOutputParam = false;
        int stringLength = sqlString.length();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean inLineComment = false;
        boolean inDelimitedComment = false;
        for (int indx = 0; indx < stringLength; ++indx) {
            String param;
            int chopLocation;
            int right;
            boolean lastCharacter;
            char c = sqlString.charAt(indx);
            boolean bl = lastCharacter = indx == stringLength - 1;
            if (inSingleQuotes) {
                recognizer.other(c);
                if ('\'' != c) continue;
                inSingleQuotes = false;
                continue;
            }
            if (inDoubleQuotes) {
                recognizer.other(c);
                if ('\"' != c) continue;
                inDoubleQuotes = false;
                continue;
            }
            if (inDelimitedComment) {
                recognizer.other(c);
                if (lastCharacter || '*' != c || '/' != sqlString.charAt(indx + 1)) continue;
                inDelimitedComment = false;
                recognizer.other(sqlString.charAt(indx + 1));
                ++indx;
                continue;
            }
            if (inLineComment) {
                recognizer.other(c);
                if ('\n' == c) {
                    inLineComment = false;
                    continue;
                }
                if ('\r' != c) continue;
                inLineComment = false;
                if (lastCharacter || '\n' != sqlString.charAt(indx + 1)) continue;
                recognizer.other(sqlString.charAt(indx + 1));
                ++indx;
                continue;
            }
            if (!lastCharacter && '/' == c && '*' == sqlString.charAt(indx + 1)) {
                inDelimitedComment = true;
                recognizer.other(c);
                recognizer.other(sqlString.charAt(indx + 1));
                ++indx;
                continue;
            }
            if ('-' == c) {
                recognizer.other(c);
                if (lastCharacter || '-' != sqlString.charAt(indx + 1)) continue;
                inLineComment = true;
                recognizer.other(sqlString.charAt(indx + 1));
                ++indx;
                continue;
            }
            if ('\"' == c) {
                inDoubleQuotes = true;
                recognizer.other(c);
                continue;
            }
            if ('\'' == c) {
                inSingleQuotes = true;
                recognizer.other(c);
                continue;
            }
            if ('\\' == c) {
                recognizer.other(sqlString.charAt(++indx));
                continue;
            }
            if (c == ':' && indx < stringLength - 1 && sqlString.charAt(indx + 1) == ':') {
                recognizer.other(c);
                ++indx;
                continue;
            }
            if (c == ':') {
                right = StringHelper.firstIndexOfChar(sqlString, ParserHelper.HQL_SEPARATORS_BITSET, indx + 1);
                chopLocation = right < 0 ? sqlString.length() : right;
                param = sqlString.substring(indx + 1, chopLocation);
                if (param.isEmpty()) {
                    throw new QueryException("Space is not allowed after parameter prefix ':' [" + sqlString + "]");
                }
                recognizer.namedParameter(param, indx);
                indx = chopLocation - 1;
                continue;
            }
            if (c == '?') {
                if (indx < stringLength - 1 && Character.isDigit(sqlString.charAt(indx + 1))) {
                    right = StringHelper.firstIndexOfChar(sqlString, " \n\r\f\t,()=<>&|+-=/*'^![]#~\\", indx + 1);
                    chopLocation = right < 0 ? sqlString.length() : right;
                    param = sqlString.substring(indx + 1, chopLocation);
                    try {
                        recognizer.jpaPositionalParameter(Integer.parseInt(param), indx);
                        indx = chopLocation - 1;
                        continue;
                    }
                    catch (NumberFormatException e) {
                        throw new QueryException("JPA-style positional param was not an integral ordinal");
                    }
                }
                if (hasMainOutputParameter && !foundMainOutputParam) {
                    foundMainOutputParam = true;
                    recognizer.outParameter(indx);
                    continue;
                }
                recognizer.ordinalParameter(indx);
                continue;
            }
            recognizer.other(c);
        }
        recognizer.complete();
    }

    public static boolean startsWithEscapeCallTemplate(String sqlString) {
        if (!sqlString.startsWith("{") || !sqlString.endsWith("}")) {
            return false;
        }
        int chopLocation = sqlString.indexOf("call");
        if (chopLocation <= 0) {
            return false;
        }
        String checkString = sqlString.substring(1, chopLocation + 4);
        String fixture = "?=call";
        int fixturePosition = 0;
        boolean matches = true;
        int max = checkString.length();
        for (int i = 0; i < max; ++i) {
            char c = Character.toLowerCase(checkString.charAt(i));
            if (Character.isWhitespace(c)) continue;
            if (c == "?=call".charAt(fixturePosition)) {
                ++fixturePosition;
                continue;
            }
            matches = false;
            break;
        }
        return matches;
    }

    public static interface Recognizer {
        public void outParameter(int var1);

        public void ordinalParameter(int var1);

        public void namedParameter(String var1, int var2);

        public void jpaPositionalParameter(int var1, int var2);

        public void other(char var1);

        public void complete();
    }
}

