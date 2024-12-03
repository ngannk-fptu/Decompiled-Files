/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.helper;

import com.atlassian.confluence.extra.jira.columns.Epic;
import java.util.Collection;
import java.util.Set;

public interface EpicInfoRetriever {
    public Collection<Epic> getEpicInformation(Set<String> var1);

    public Collection<Epic> getEpicInformationByEpicName(Set<String> var1);
}

