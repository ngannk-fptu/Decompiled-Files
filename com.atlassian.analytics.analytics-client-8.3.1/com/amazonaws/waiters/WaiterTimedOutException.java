/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.SdkClientException;

public class WaiterTimedOutException
extends SdkClientException {
    public WaiterTimedOutException(String message) {
        super(message);
    }
}

