/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.event.MyWorkEvent
 */
package com.atlassian.mywork.host.event;

import com.atlassian.mywork.event.MyWorkEvent;

public class BeforeCountNewNotificationsEvent
extends MyWorkEvent {
    public BeforeCountNewNotificationsEvent(String username) {
        super(username);
    }
}

