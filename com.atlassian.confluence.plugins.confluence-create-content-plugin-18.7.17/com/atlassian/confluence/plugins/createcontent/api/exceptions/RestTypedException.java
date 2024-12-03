/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.api.exceptions;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;

public interface RestTypedException {
    public ResourceErrorType getErrorType();

    public Object getErrorData();
}

