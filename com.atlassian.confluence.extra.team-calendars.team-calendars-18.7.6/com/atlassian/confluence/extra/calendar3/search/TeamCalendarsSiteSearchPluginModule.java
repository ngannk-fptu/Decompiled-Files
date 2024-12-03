/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor
 *  com.atlassian.confluence.search.plugin.SiteSearchPluginModule
 */
package com.atlassian.confluence.extra.calendar3.search;

import com.atlassian.confluence.extra.calendar3.search.CalendarSearchContentTypeDescriptor;
import com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor;
import com.atlassian.confluence.search.plugin.SiteSearchPluginModule;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TeamCalendarsSiteSearchPluginModule
implements SiteSearchPluginModule {
    private final List<ContentTypeSearchDescriptor> descriptors = Collections.unmodifiableList(Collections.singletonList(new CalendarSearchContentTypeDescriptor()));

    public Collection<ContentTypeSearchDescriptor> getContentTypeDescriptors() {
        return this.descriptors;
    }
}

