/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import java.io.FileNotFoundException;

public class NoSuchDirectoryException
extends FileNotFoundException {
    public NoSuchDirectoryException(String message) {
        super(message);
    }
}

