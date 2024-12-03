/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.migration;

import com.atlassian.crowd.event.migration.XMLRestoreEvent;

public class XMLRestoreFinishedEvent
extends XMLRestoreEvent {
    public XMLRestoreFinishedEvent(Object source, String filePath) {
        super(source, filePath);
    }
}

