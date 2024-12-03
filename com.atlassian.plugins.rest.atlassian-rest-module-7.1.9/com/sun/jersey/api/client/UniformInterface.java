/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;

public interface UniformInterface {
    public ClientResponse head() throws ClientHandlerException;

    public <T> T options(Class<T> var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T options(GenericType<T> var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T get(Class<T> var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T get(GenericType<T> var1) throws UniformInterfaceException, ClientHandlerException;

    public void put() throws UniformInterfaceException, ClientHandlerException;

    public void put(Object var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T put(Class<T> var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T put(GenericType<T> var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T put(Class<T> var1, Object var2) throws UniformInterfaceException, ClientHandlerException;

    public <T> T put(GenericType<T> var1, Object var2) throws UniformInterfaceException, ClientHandlerException;

    public void post() throws UniformInterfaceException, ClientHandlerException;

    public void post(Object var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T post(Class<T> var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T post(GenericType<T> var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T post(Class<T> var1, Object var2) throws UniformInterfaceException, ClientHandlerException;

    public <T> T post(GenericType<T> var1, Object var2) throws UniformInterfaceException, ClientHandlerException;

    public void delete() throws UniformInterfaceException, ClientHandlerException;

    public void delete(Object var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T delete(Class<T> var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T delete(GenericType<T> var1) throws UniformInterfaceException, ClientHandlerException;

    public <T> T delete(Class<T> var1, Object var2) throws UniformInterfaceException, ClientHandlerException;

    public <T> T delete(GenericType<T> var1, Object var2) throws UniformInterfaceException, ClientHandlerException;

    public void method(String var1) throws UniformInterfaceException, ClientHandlerException;

    public void method(String var1, Object var2) throws UniformInterfaceException, ClientHandlerException;

    public <T> T method(String var1, Class<T> var2) throws UniformInterfaceException, ClientHandlerException;

    public <T> T method(String var1, GenericType<T> var2) throws UniformInterfaceException, ClientHandlerException;

    public <T> T method(String var1, Class<T> var2, Object var3) throws UniformInterfaceException, ClientHandlerException;

    public <T> T method(String var1, GenericType<T> var2, Object var3) throws UniformInterfaceException, ClientHandlerException;
}

