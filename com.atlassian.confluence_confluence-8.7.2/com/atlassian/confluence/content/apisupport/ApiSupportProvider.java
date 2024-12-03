/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.content.apisupport.ContentTypeApiSupport;

public interface ApiSupportProvider {
    public ContentTypeApiSupport getForType(ContentType var1);
}

