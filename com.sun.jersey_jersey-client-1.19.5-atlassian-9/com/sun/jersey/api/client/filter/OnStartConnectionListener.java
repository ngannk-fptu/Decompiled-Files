/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.filter.ContainerListener;

public interface OnStartConnectionListener {
    public ContainerListener onStart(ClientRequest var1);
}

