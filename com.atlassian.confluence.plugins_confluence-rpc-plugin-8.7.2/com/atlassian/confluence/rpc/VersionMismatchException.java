/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.RemoteException
 */
package com.atlassian.confluence.rpc;

import com.atlassian.confluence.rpc.RemoteException;

public class VersionMismatchException
extends RemoteException {
    public static final String __PARANAMER_DATA = "<init> java.lang.String message \n<init> java.lang.Throwable cause \n<init> java.lang.String,java.lang.Throwable message,cause \n";

    public VersionMismatchException() {
    }

    public VersionMismatchException(String message) {
        super(message);
    }

    public VersionMismatchException(Throwable cause) {
        super(cause);
    }

    public VersionMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}

