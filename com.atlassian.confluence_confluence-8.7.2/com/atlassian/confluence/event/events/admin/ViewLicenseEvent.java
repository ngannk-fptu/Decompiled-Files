/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Viewed;

@EventName(value="confluence.admin.view.license")
public class ViewLicenseEvent
extends ConfluenceEvent
implements Viewed {
    private static final long serialVersionUID = -7407358687303335628L;

    public ViewLicenseEvent(Object src) {
        super(src);
    }
}

