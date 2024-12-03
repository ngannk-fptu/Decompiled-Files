/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ContentTypeAware;

public interface Addressable
extends ContentTypeAware {
    public long getId();

    public String getDisplayTitle();

    public String getUrlPath();
}

