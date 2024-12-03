/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws;

import javax.xml.ws.WebServiceException;

public interface Closeable
extends java.io.Closeable {
    @Override
    public void close() throws WebServiceException;
}

