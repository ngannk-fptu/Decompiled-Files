/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

public interface TupleElement<X> {
    public Class<? extends X> getJavaType();

    public String getAlias();
}

