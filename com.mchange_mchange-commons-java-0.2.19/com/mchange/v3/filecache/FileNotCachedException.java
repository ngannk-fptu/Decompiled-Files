/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.filecache;

import java.io.FileNotFoundException;

public class FileNotCachedException
extends FileNotFoundException {
    FileNotCachedException(String string) {
        super(string);
    }

    FileNotCachedException() {
    }
}

