/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.event.events.ConfluenceEvent;

@Deprecated
public class ConfigurationMigrationEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -6447882992266122569L;

    public ConfigurationMigrationEvent(Object src) {
        super(src);
    }
}

