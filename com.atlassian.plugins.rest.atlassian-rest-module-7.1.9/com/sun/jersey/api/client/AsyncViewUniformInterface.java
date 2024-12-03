/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import java.util.concurrent.Future;

public interface AsyncViewUniformInterface {
    public <T> Future<T> head(Class<T> var1);

    public <T> Future<T> head(T var1);

    public <T> Future<T> options(Class<T> var1);

    public <T> Future<T> options(T var1);

    public <T> Future<T> get(Class<T> var1);

    public <T> Future<T> get(T var1);

    public <T> Future<T> put(Class<T> var1);

    public <T> Future<T> put(T var1);

    public <T> Future<T> put(Class<T> var1, Object var2);

    public <T> Future<T> put(T var1, Object var2);

    public <T> Future<T> post(Class<T> var1);

    public <T> Future<T> post(T var1);

    public <T> Future<T> post(Class<T> var1, Object var2);

    public <T> Future<T> post(T var1, Object var2);

    public <T> Future<T> delete(Class<T> var1);

    public <T> Future<T> delete(T var1);

    public <T> Future<T> delete(Class<T> var1, Object var2);

    public <T> Future<T> delete(T var1, Object var2);

    public <T> Future<T> method(String var1, Class<T> var2);

    public <T> Future<T> method(String var1, T var2);

    public <T> Future<T> method(String var1, Class<T> var2, Object var3);

    public <T> Future<T> method(String var1, T var2, Object var3);
}

