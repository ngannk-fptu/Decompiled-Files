/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.core;

import org.springframework.core.io.Resource;

public interface AttachmentResource
extends Resource {
    public String getContentType();

    @Deprecated
    public long getContentLength();

    public String getComment();

    public boolean isMinorEdit();

    public boolean isHidden();
}

