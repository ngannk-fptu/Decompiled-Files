/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import org.apache.hc.core5.io.ModalCloseable;
import org.apache.hc.core5.util.Timeout;

public interface SocketModalCloseable
extends ModalCloseable {
    public Timeout getSocketTimeout();

    public void setSocketTimeout(Timeout var1);
}

