/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi;

import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.hql.spi.QueryTranslator;

public interface FilterTranslator
extends QueryTranslator {
    public void compile(String var1, Map var2, boolean var3) throws QueryException, MappingException;
}

