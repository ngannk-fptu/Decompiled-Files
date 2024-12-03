/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import org.hibernate.hql.internal.classic.QueryTranslatorImpl;
import org.hibernate.hql.internal.classic.WhereParser;

public class HavingParser
extends WhereParser {
    @Override
    void appendToken(QueryTranslatorImpl q, String token) {
        q.appendHavingToken(token);
    }
}

