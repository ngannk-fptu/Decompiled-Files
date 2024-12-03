/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpMessage
 */
package org.apache.hc.client5.http.impl;

import org.apache.hc.core5.http.HttpMessage;

@Deprecated
public interface MessageCopier<T extends HttpMessage> {
    public T copy(T var1);
}

