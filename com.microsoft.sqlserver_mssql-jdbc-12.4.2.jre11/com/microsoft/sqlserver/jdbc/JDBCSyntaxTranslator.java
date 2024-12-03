/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class JDBCSyntaxTranslator {
    private String procedureName = null;
    private boolean hasReturnValueSyntax = false;
    private static final String SQL_IDENTIFIER_PART = "(?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))";
    private static final String SQL_IDENTIFIER_WITHOUT_GROUPS = "((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))(?:\\.(?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))){0,3}?)";
    private static final String SQL_IDENTIFIER_WITH_GROUPS = "((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?)))(?:\\.((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))))?";
    private static final Pattern JDBC_CALL_SYNTAX = Pattern.compile("(?s)\\s*?\\{\\s*?(\\?\\s*?=)?\\s*?[cC][aA][lL][lL]\\s+?((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))(?:\\.(?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))){0,3}?)(?:\\s*?\\((.*)\\))?\\s*\\}.*+");
    private static final Pattern SQL_EXEC_SYNTAX = Pattern.compile("\\s*?[eE][xX][eE][cC](?:[uU][tT][eE])??\\s+?(((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))(?:\\.(?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))){0,3}?)\\s*?=\\s+?)??((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))(?:\\.(?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))){0,3}?)(?:$|(?:\\s+?.*+))");
    private static final Pattern LIMIT_SYNTAX_WITH_OFFSET = Pattern.compile("\\{\\s*[lL][iI][mM][iI][tT]\\s+(.*)\\s+[oO][fF][fF][sS][eE][tT]\\s+(.*)\\}");
    private static final Pattern LIMIT_SYNTAX_GENERIC = Pattern.compile("\\{\\s*[lL][iI][mM][iI][tT]\\s+(.*)(\\s+[oO][fF][fF][sS][eE][tT](.*)\\}|\\s*\\})");
    private static final Pattern SELECT_PATTERN = Pattern.compile("([sS][eE][lL][eE][cC][tT])\\s+");
    private static final Pattern OPEN_QUERY_PATTERN = Pattern.compile("[oO][pP][eE][nN][qQ][uU][eE][rR][yY]\\s*\\(.*,\\s*'(.*)'\\s*\\)");
    private static final Pattern OPEN_ROWSET_PATTERN = Pattern.compile("[oO][pP][eE][nN][rR][oO][wW][sS][eE][tT]\\s*\\(.*,.*,\\s*'(.*)'\\s*\\)");
    private static final Pattern LIMIT_ONLY_PATTERN = Pattern.compile("\\{\\s*[lL][iI][mM][iI][tT]\\s+(((\\(|\\s)*)(\\d*|\\?)((\\)|\\s)*))\\s*\\}");

    JDBCSyntaxTranslator() {
    }

    String getProcedureName() {
        return this.procedureName;
    }

    boolean hasReturnValueSyntax() {
        return this.hasReturnValueSyntax;
    }

    static String getSQLIdentifierWithGroups() {
        return SQL_IDENTIFIER_WITH_GROUPS;
    }

    int translateLimit(StringBuffer sql, int indx, char endChar) throws SQLServerException {
        Matcher selectMatcher = SELECT_PATTERN.matcher(sql);
        Matcher openQueryMatcher = OPEN_QUERY_PATTERN.matcher(sql);
        Matcher openRowsetMatcher = OPEN_ROWSET_PATTERN.matcher(sql);
        Matcher limitMatcher = LIMIT_ONLY_PATTERN.matcher(sql);
        Matcher offsetMatcher = LIMIT_SYNTAX_WITH_OFFSET.matcher(sql);
        int startIndx = indx;
        Stack<Integer> topPosition = new Stack<Integer>();
        State nextState = State.START;
        while (indx < sql.length()) {
            char ch = sql.charAt(indx);
            switch (nextState) {
                case START: {
                    nextState = State.PROCESS;
                    break;
                }
                case PROCESS: {
                    if (endChar == ch) {
                        nextState = State.END;
                        break;
                    }
                    if ('\'' == ch) {
                        nextState = State.QUOTE;
                        break;
                    }
                    if ('(' == ch) {
                        nextState = State.SUBQUERY;
                        break;
                    }
                    if (limitMatcher.find(indx) && indx == limitMatcher.start()) {
                        nextState = State.LIMIT;
                        break;
                    }
                    if (offsetMatcher.find(indx) && indx == offsetMatcher.start()) {
                        nextState = State.OFFSET;
                        break;
                    }
                    if (openQueryMatcher.find(indx) && indx == openQueryMatcher.start()) {
                        nextState = State.OPENQUERY;
                        break;
                    }
                    if (openRowsetMatcher.find(indx) && indx == openRowsetMatcher.start()) {
                        nextState = State.OPENROWSET;
                        break;
                    }
                    if (selectMatcher.find(indx) && indx == selectMatcher.start()) {
                        nextState = State.SELECT;
                        break;
                    }
                    ++indx;
                    break;
                }
                case OFFSET: {
                    throw new SQLServerException(SQLServerException.getErrString("R_limitOffsetNotSupported"), null, 0, null);
                }
                case LIMIT: {
                    int openingParentheses = 0;
                    int closingParentheses = 0;
                    int pos = -1;
                    String openingStr = limitMatcher.group(2);
                    String closingStr = limitMatcher.group(5);
                    while (-1 != (pos = openingStr.indexOf(40, pos + 1))) {
                        ++openingParentheses;
                    }
                    pos = -1;
                    while (-1 != (pos = closingStr.indexOf(41, pos + 1))) {
                        ++closingParentheses;
                    }
                    if (openingParentheses != closingParentheses) {
                        throw new SQLServerException(SQLServerException.getErrString("R_limitEscapeSyntaxError"), null, 0, null);
                    }
                    if (!topPosition.empty()) {
                        Integer top = (Integer)topPosition.pop();
                        String rows = limitMatcher.group(1);
                        sql.delete(limitMatcher.start() - 1, limitMatcher.end());
                        if ('?' == rows.charAt(0)) {
                            sql.insert((int)top, " TOP (" + rows + ")");
                            indx += 7 + rows.length() - 1;
                        } else {
                            sql.insert((int)top, " TOP " + rows);
                            indx += 5 + rows.length() - 1;
                        }
                    } else {
                        indx = limitMatcher.end() - 1;
                    }
                    nextState = State.PROCESS;
                    break;
                }
                case SELECT: {
                    indx = selectMatcher.end(1);
                    topPosition.push(indx);
                    nextState = State.PROCESS;
                    break;
                }
                case QUOTE: {
                    if (sql.length() > ++indx && '\'' == sql.charAt(indx)) {
                        if (sql.length() > ++indx && '\'' == sql.charAt(indx)) {
                            nextState = State.QUOTE;
                            break;
                        }
                        nextState = State.PROCESS;
                        break;
                    }
                    nextState = State.QUOTE;
                    break;
                }
                case SUBQUERY: {
                    ++indx;
                    indx += this.translateLimit(sql, indx, ')');
                    nextState = State.PROCESS;
                    break;
                }
                case OPENQUERY: {
                    indx = openQueryMatcher.start(1);
                    indx += this.translateLimit(sql, indx, '\'');
                    nextState = State.PROCESS;
                    break;
                }
                case OPENROWSET: {
                    indx = openRowsetMatcher.start(1);
                    indx += this.translateLimit(sql, indx, '\'');
                    nextState = State.PROCESS;
                    break;
                }
                case END: {
                    return ++indx - startIndx;
                }
            }
        }
        return indx - startIndx;
    }

    String translate(String sql) throws SQLServerException {
        Matcher matcher = JDBC_CALL_SYNTAX.matcher((CharSequence)sql);
        if (matcher.matches()) {
            this.hasReturnValueSyntax = null != matcher.group(1);
            this.procedureName = matcher.group(2);
            String args = matcher.group(3);
            sql = "EXEC " + (this.hasReturnValueSyntax ? "? = " : "") + this.procedureName + (String)(null != args ? " " + args : "");
        } else {
            matcher = SQL_EXEC_SYNTAX.matcher((CharSequence)sql);
            if (matcher.matches()) {
                this.hasReturnValueSyntax = null != matcher.group(1);
                this.procedureName = matcher.group(3);
            }
        }
        matcher = LIMIT_SYNTAX_GENERIC.matcher((CharSequence)sql);
        if (matcher.find()) {
            StringBuffer sqlbuf = new StringBuffer((String)sql);
            this.translateLimit(sqlbuf, 0, '\u0000');
            return sqlbuf.toString();
        }
        return sql;
    }

    static enum State {
        START,
        END,
        SUBQUERY,
        SELECT,
        OPENQUERY,
        OPENROWSET,
        LIMIT,
        OFFSET,
        QUOTE,
        PROCESS;

    }
}

