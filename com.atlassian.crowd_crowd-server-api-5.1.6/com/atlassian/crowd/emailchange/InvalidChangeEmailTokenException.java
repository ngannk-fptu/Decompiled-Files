/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.CrowdException
 */
package com.atlassian.crowd.emailchange;

import com.atlassian.crowd.exception.CrowdException;

public class InvalidChangeEmailTokenException
extends CrowdException {
    public InvalidChangeEmailTokenException(String message) {
        super(message);
    }
}

