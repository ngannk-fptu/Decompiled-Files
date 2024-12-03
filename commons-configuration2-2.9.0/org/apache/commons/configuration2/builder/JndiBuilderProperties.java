/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import javax.naming.Context;

public interface JndiBuilderProperties<T> {
    public T setContext(Context var1);

    public T setPrefix(String var1);
}

