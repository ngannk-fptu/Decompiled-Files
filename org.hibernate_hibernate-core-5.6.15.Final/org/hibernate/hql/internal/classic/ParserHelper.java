/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import java.util.BitSet;
import java.util.StringTokenizer;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.classic.Parser;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;

public final class ParserHelper {
    public static final String HQL_VARIABLE_PREFIX = ":";
    public static final String HQL_SEPARATORS = " \n\r\f\t,()=<>&|+-=/*'^![]#~\\";
    public static final BitSet HQL_SEPARATORS_BITSET = new BitSet();
    public static final String PATH_SEPARATORS = ".";

    public static boolean isWhitespace(String str) {
        return " \n\r\f\t".contains(str);
    }

    private ParserHelper() {
    }

    public static void parse(Parser p, String text, String seperators, QueryTranslatorImpl q) throws QueryException {
        StringTokenizer tokens = new StringTokenizer(text, seperators, true);
        p.start(q);
        while (tokens.hasMoreElements()) {
            p.token(tokens.nextToken(), q);
        }
        p.end(q);
    }

    static {
        for (int i = 0; i < HQL_SEPARATORS.length(); ++i) {
            HQL_SEPARATORS_BITSET.set(HQL_SEPARATORS.charAt(i));
        }
    }
}

