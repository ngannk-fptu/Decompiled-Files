/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.Critical
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.analytics.api.annotations.Critical;
import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.filestructure.disabled")
@Critical
public class V4AttachmentDisabledAnalyticsEvent {
}

