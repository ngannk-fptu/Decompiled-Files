/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import java.io.IOException;

public class LockReleaseFailedException
extends IOException {
    public LockReleaseFailedException(String message) {
        super(message);
    }
}

