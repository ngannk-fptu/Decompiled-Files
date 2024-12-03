/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.async.ITypeListener;
import java.util.concurrent.Future;

public interface AsyncUniformInterface {
    public Future<ClientResponse> head();

    public Future<ClientResponse> head(ITypeListener<ClientResponse> var1);

    public <T> Future<T> options(Class<T> var1);

    public <T> Future<T> options(GenericType<T> var1);

    public <T> Future<T> options(ITypeListener<T> var1);

    public <T> Future<T> get(Class<T> var1) throws UniformInterfaceException;

    public <T> Future<T> get(GenericType<T> var1) throws UniformInterfaceException;

    public <T> Future<T> get(ITypeListener<T> var1);

    public Future<?> put();

    public Future<?> put(Object var1);

    public <T> Future<T> put(Class<T> var1);

    public <T> Future<T> put(GenericType<T> var1);

    public <T> Future<T> put(ITypeListener<T> var1);

    public <T> Future<T> put(Class<T> var1, Object var2);

    public <T> Future<T> put(GenericType<T> var1, Object var2);

    public <T> Future<T> put(ITypeListener<T> var1, Object var2);

    public Future<?> post();

    public Future<?> post(Object var1);

    public <T> Future<T> post(Class<T> var1);

    public <T> Future<T> post(GenericType<T> var1);

    public <T> Future<T> post(ITypeListener<T> var1);

    public <T> Future<T> post(Class<T> var1, Object var2);

    public <T> Future<T> post(GenericType<T> var1, Object var2);

    public <T> Future<T> post(ITypeListener<T> var1, Object var2);

    public Future<?> delete();

    public Future<?> delete(Object var1);

    public <T> Future<T> delete(Class<T> var1);

    public <T> Future<T> delete(GenericType<T> var1);

    public <T> Future<T> delete(ITypeListener<T> var1);

    public <T> Future<T> delete(Class<T> var1, Object var2);

    public <T> Future<T> delete(GenericType<T> var1, Object var2);

    public <T> Future<T> delete(ITypeListener<T> var1, Object var2);

    public Future<?> method(String var1);

    public Future<?> method(String var1, Object var2);

    public <T> Future<T> method(String var1, Class<T> var2);

    public <T> Future<T> method(String var1, GenericType<T> var2);

    public <T> Future<T> method(String var1, ITypeListener<T> var2);

    public <T> Future<T> method(String var1, Class<T> var2, Object var3);

    public <T> Future<T> method(String var1, GenericType<T> var2, Object var3);

    public <T> Future<T> method(String var1, ITypeListener<T> var2, Object var3);
}

