/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.UniqueId;

public interface ErrorHandler {
    public void handleError(ChannelException var1, UniqueId var2);

    public void handleCompletion(UniqueId var1);
}

