/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.CrowdException
 */
package com.atlassian.crowd.event;

import com.atlassian.crowd.exception.CrowdException;

public class EventTokenExpiredException
extends CrowdException {
    public EventTokenExpiredException() {
    }

    public EventTokenExpiredException(String message) {
        super(message);
    }
}

