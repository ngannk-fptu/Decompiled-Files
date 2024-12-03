/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 */
package com.atlassian.confluence.content.custom;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.apisupport.ContentTypeApiSupport;

@ExperimentalSpi
public interface CustomContentType
extends ContentType {
    public ContentTypeApiSupport<CustomContentEntityObject> getApiSupport();
}

