/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.Category
 *  com.atlassian.gadgets.directory.Directory$DashboardDirectoryEntry
 *  com.atlassian.gadgets.directory.Directory$Entry
 *  com.atlassian.gadgets.directory.Directory$OpenSocialDirectoryEntry
 *  com.atlassian.gadgets.directory.DirectoryEntryVisitor
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.directory.internal.jaxb;

import com.atlassian.gadgets.directory.Category;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.DirectoryEntryVisitor;
import com.atlassian.gadgets.directory.internal.jaxb.DashboardItemRepresentation;
import com.atlassian.gadgets.directory.internal.jaxb.LocalDashboardItemRepresentation;
import com.atlassian.gadgets.directory.internal.jaxb.SpecDashboardItemRepresentation;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.net.URI;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DashboardItemRepresentationFactory {
    public DashboardItemRepresentation createDashboardItemRepresentation(Directory.Entry<?> directoryEntry) {
        return (DashboardItemRepresentation)directoryEntry.accept((DirectoryEntryVisitor)new DirectoryEntryVisitor<DashboardItemRepresentation>(){

            public DashboardItemRepresentation visit(Directory.OpenSocialDirectoryEntry openSocialDirectoryEntry) {
                return DashboardItemRepresentationFactory.this.createSpecBasedDirectoryRepresentation(openSocialDirectoryEntry);
            }

            public DashboardItemRepresentation visit(Directory.DashboardDirectoryEntry dashboardDirectoryEntry) {
                return DashboardItemRepresentationFactory.this.createLocalDirectoryRepresentation(dashboardDirectoryEntry);
            }
        });
    }

    private LocalDashboardItemRepresentation createLocalDirectoryRepresentation(Directory.DashboardDirectoryEntry directoryEntry) {
        List<String> categories = this.getCategories((Directory.Entry<?>)directoryEntry);
        return new LocalDashboardItemRepresentation(directoryEntry.getTitle(), directoryEntry.getTitleUri(), directoryEntry.getAuthorName(), directoryEntry.getDescription(), ((ModuleCompleteKey)directoryEntry.getId()).getCompleteKey(), categories, directoryEntry.getThumbnailUri(), ((ModuleCompleteKey)directoryEntry.getId()).getCompleteKey());
    }

    private SpecDashboardItemRepresentation createSpecBasedDirectoryRepresentation(Directory.OpenSocialDirectoryEntry directoryEntry) {
        List<String> categories = this.getCategories((Directory.Entry<?>)directoryEntry);
        return new SpecDashboardItemRepresentation(directoryEntry.getTitle(), directoryEntry.getTitleUri(), directoryEntry.getAuthorName(), directoryEntry.getDescription(), ((URI)directoryEntry.getId()).toString(), categories, directoryEntry.getThumbnailUri(), (URI)directoryEntry.getId());
    }

    private List<String> getCategories(Directory.Entry<?> directoryEntry) {
        return Lists.newArrayList((Iterable)Iterables.transform((Iterable)directoryEntry.getCategories(), (Function)new Function<Category, String>(){

            public String apply(Category category) {
                return category.getName();
            }
        }));
    }
}

