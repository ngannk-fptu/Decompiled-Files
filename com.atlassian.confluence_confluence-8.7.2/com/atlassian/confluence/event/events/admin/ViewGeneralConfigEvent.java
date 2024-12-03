/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.admin.ConfigurationEvent;

@EventName(value="confluence.admin.view.general.configuration")
public class ViewGeneralConfigEvent
extends ConfigurationEvent {
    private static final long serialVersionUID = 7013886452668242335L;

    public ViewGeneralConfigEvent(Object src) {
        super(src);
    }
}

