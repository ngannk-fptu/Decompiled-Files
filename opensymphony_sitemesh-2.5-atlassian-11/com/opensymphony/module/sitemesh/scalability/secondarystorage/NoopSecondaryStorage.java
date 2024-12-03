/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import java.io.Writer;

public class NoopSecondaryStorage
implements SecondaryStorage {
    public long getMemoryLimitBeforeUse() {
        return -1L;
    }

    public void write(int c) {
    }

    public void write(char[] chars, int off, int len) {
    }

    public void write(String str, int off, int len) {
    }

    public void write(String str) {
    }

    public void writeTo(Writer out) {
    }

    public void cleanUp() {
    }
}

