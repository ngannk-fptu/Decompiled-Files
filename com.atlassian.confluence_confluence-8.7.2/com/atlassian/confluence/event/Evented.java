/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event;

public interface Evented<T> {
    public T getEventToPublish(String var1);
}

