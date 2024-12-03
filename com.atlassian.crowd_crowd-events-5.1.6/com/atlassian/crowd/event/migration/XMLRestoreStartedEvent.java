/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.migration;

import com.atlassian.crowd.event.migration.XMLRestoreEvent;

public class XMLRestoreStartedEvent
extends XMLRestoreEvent {
    public XMLRestoreStartedEvent(Object source, String filePath) {
        super(source, filePath);
    }
}

