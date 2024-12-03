/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.FileNotFoundException;

public final class IndexNotFoundException
extends FileNotFoundException {
    public IndexNotFoundException(String msg) {
        super(msg);
    }
}

