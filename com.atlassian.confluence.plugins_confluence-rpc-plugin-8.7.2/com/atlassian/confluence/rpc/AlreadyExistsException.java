/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.RemoteException
 */
package com.atlassian.confluence.rpc;

import com.atlassian.confluence.rpc.RemoteException;

public class AlreadyExistsException
extends RemoteException {
    public static final String __PARANAMER_DATA = "<init> java.lang.String message \n<init> java.lang.Throwable cause \n<init> java.lang.String,java.lang.Throwable message,cause \n";

    public AlreadyExistsException() {
    }

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

