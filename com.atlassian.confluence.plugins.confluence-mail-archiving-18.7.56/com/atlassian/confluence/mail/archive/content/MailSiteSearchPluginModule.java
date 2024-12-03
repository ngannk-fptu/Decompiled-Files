/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor
 *  com.atlassian.confluence.search.plugin.SiteSearchPluginModule
 */
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.mail.archive.content.MailSearchContentTypeDescriptor;
import com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor;
import com.atlassian.confluence.search.plugin.SiteSearchPluginModule;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MailSiteSearchPluginModule
implements SiteSearchPluginModule {
    private final List<ContentTypeSearchDescriptor> descriptors = Collections.unmodifiableList(Collections.singletonList(new MailSearchContentTypeDescriptor()));

    public Collection<ContentTypeSearchDescriptor> getContentTypeDescriptors() {
        return this.descriptors;
    }
}

