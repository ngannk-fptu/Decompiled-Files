/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.filter;

public abstract class ContainerListener {
    public void onSent(long delta, long bytes) {
    }

    public void onReceiveStart(long totalBytes) {
    }

    public void onReceived(long delta, long bytes) {
    }

    public void onFinish() {
    }
}

