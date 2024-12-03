/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.type.Type;

public interface ParameterInfoCollector {
    public void addNamedParameter(String var1, Type var2);

    public void addPositionalParameter(int var1, Type var2);
}

