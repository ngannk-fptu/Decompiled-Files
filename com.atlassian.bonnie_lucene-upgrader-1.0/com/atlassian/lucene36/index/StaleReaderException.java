/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import java.io.IOException;

public class StaleReaderException
extends IOException {
    public StaleReaderException(String message) {
        super(message);
    }
}

