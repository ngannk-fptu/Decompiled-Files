/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.validation;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.messages.Message;

@ExperimentalApi
public interface ValidationError {
    public Message getMessage();
}

