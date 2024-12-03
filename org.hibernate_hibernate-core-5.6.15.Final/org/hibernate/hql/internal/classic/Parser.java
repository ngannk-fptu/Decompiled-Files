/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import org.hibernate.QueryException;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;

public interface Parser {
    public void token(String var1, QueryTranslatorImpl var2) throws QueryException;

    public void start(QueryTranslatorImpl var1) throws QueryException;

    public void end(QueryTranslatorImpl var1) throws QueryException;
}

