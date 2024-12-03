/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

public interface Parameter<T> {
    public String getName();

    public Integer getPosition();

    public Class<T> getParameterType();
}

