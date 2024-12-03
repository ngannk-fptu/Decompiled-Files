/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;

public class LockReleaseFailedException
extends IOException {
    public LockReleaseFailedException(String message) {
        super(message);
    }
}

