/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.business.insights.core.rest.exception;

import com.atlassian.business.insights.core.rest.exception.NotFoundException;

public class NotImplementedException
extends NotFoundException {
    public NotImplementedException() {
        super("Not implemented");
    }
}

