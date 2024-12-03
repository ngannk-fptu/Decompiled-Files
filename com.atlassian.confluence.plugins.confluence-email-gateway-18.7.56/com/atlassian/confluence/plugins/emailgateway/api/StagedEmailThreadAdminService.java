/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.validation.MessageHolder
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.emailgateway.api.EmailToContentConverter;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadKey;
import com.atlassian.confluence.validation.MessageHolder;
import org.joda.time.DateTime;

@PublicApi
public interface StagedEmailThreadAdminService {
    public <C extends ContentEntityObject> C convertAndPublishStagedEmailThread(StagedEmailThreadKey var1, MessageHolder var2, EmailToContentConverter<C> var3);

    public void deleteStagedEmailThread(StagedEmailThreadKey var1);

    public int clearExpiredEmailThreads(DateTime var1);
}

