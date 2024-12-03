/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.renderer;

public class RenderingTracker {
    private int id = 0;

    public synchronized int getNextId() {
        return ++this.id;
    }
}

