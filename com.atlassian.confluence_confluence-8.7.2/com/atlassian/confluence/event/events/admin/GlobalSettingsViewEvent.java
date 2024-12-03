/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ConfigurationEvent;
import com.atlassian.confluence.event.events.types.Viewed;

public class GlobalSettingsViewEvent
extends ConfigurationEvent
implements Viewed {
    private static final long serialVersionUID = 4637199101172163141L;

    public GlobalSettingsViewEvent(Object src) {
        super(src);
    }
}

