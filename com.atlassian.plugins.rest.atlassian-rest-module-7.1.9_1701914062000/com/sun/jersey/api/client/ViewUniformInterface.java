/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

public interface ViewUniformInterface {
    public <T> T head(Class<T> var1);

    public <T> T head(T var1);

    public <T> T options(Class<T> var1);

    public <T> T options(T var1);

    public <T> T get(Class<T> var1);

    public <T> T get(T var1);

    public <T> T put(Class<T> var1);

    public <T> T put(T var1);

    public <T> T put(Class<T> var1, Object var2);

    public <T> T put(T var1, Object var2);

    public <T> T post(Class<T> var1);

    public <T> T post(T var1);

    public <T> T post(Class<T> var1, Object var2);

    public <T> T post(T var1, Object var2);

    public <T> T delete(Class<T> var1);

    public <T> T delete(T var1);

    public <T> T delete(Class<T> var1, Object var2);

    public <T> T delete(T var1, Object var2);

    public <T> T method(String var1, Class<T> var2);

    public <T> T method(String var1, T var2);

    public <T> T method(String var1, Class<T> var2, Object var3);

    public <T> T method(String var1, T var2, Object var3);
}

