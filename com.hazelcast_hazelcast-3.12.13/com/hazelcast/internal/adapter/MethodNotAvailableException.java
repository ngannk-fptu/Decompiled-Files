/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.adapter;

class MethodNotAvailableException
extends RuntimeException {
    MethodNotAvailableException() {
        super("The invoked method is not available in your DataStructureAdapter implementation!");
    }
}

