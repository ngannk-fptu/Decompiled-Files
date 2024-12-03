/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.migration;

import com.atlassian.crowd.event.Event;

public abstract class XMLRestoreEvent
extends Event {
    private final String filePath;

    public XMLRestoreEvent(Object source, String filePath) {
        super(source);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }
}

