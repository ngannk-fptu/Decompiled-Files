/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

public interface GenerationTarget {
    public void prepare();

    public void accept(String var1);

    public void release();
}

