/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;

public class LockObtainFailedException
extends IOException {
    public LockObtainFailedException(String message) {
        super(message);
    }
}

