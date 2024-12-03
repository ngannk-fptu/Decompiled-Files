/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl;

import org.apache.hc.core5.http.HttpMessage;

@Deprecated
public interface MessageCopier<T extends HttpMessage> {
    public T copy(T var1);
}

