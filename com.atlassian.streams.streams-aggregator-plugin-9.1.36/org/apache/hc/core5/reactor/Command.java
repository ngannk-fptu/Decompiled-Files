/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import org.apache.hc.core5.concurrent.Cancellable;

public interface Command
extends Cancellable {

    public static enum Priority {
        NORMAL,
        IMMEDIATE;

    }
}

