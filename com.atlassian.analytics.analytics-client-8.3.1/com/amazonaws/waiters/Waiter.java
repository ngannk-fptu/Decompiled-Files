/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.waiters.WaiterHandler;
import com.amazonaws.waiters.WaiterParameters;
import com.amazonaws.waiters.WaiterTimedOutException;
import com.amazonaws.waiters.WaiterUnrecoverableException;
import java.util.concurrent.Future;

public interface Waiter<Input extends AmazonWebServiceRequest> {
    public void run(WaiterParameters<Input> var1) throws AmazonServiceException, WaiterTimedOutException, WaiterUnrecoverableException;

    public Future<Void> runAsync(WaiterParameters<Input> var1, WaiterHandler var2) throws AmazonServiceException, WaiterTimedOutException, WaiterUnrecoverableException;
}

