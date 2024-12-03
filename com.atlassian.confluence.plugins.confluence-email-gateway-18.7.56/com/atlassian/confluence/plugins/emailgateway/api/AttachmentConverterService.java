/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentFile;
import com.atlassian.confluence.plugins.emailgateway.api.SerializableAttachment;
import java.util.List;

@PublicApi
public interface AttachmentConverterService {
    @Deprecated
    public Streamable convert(AttachmentFile var1);

    public void attachTo(ContentEntityObject var1, List<SerializableAttachment> var2);
}

