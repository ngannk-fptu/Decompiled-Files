/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.gadgets.directory;

import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.Category;
import com.atlassian.gadgets.directory.DirectoryEntryVisitor;
import com.atlassian.plugin.ModuleCompleteKey;
import java.net.URI;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Directory {
    public static final String DIRECTORY_FEATURE_NAME = "gadget-directory";
    public static final String DIRECTORY_FEATURE_CATEGORIES_NAME = "categories";

    public Iterable<Entry<?>> getEntries(GadgetRequestContext var1);

    public Iterable<Entry<?>> getEntries(GadgetRequestContext var1, EntryScope var2);

    public boolean contains(URI var1);

    public static enum EntryScope {
        ALL,
        LOCAL,
        EXTERNAL;

    }

    public static interface DashboardDirectoryEntry
    extends Entry<ModuleCompleteKey> {
    }

    public static interface OpenSocialDirectoryEntry
    extends Entry<URI> {
    }

    public static interface Entry<T> {
        @Nullable
        public URI getSelf();

        public boolean isDeletable();

        public T getId();

        public String getTitle();

        @Nullable
        public URI getTitleUri();

        @Nullable
        public URI getThumbnailUri();

        public String getAuthorName();

        public String getAuthorEmail();

        public String getDescription();

        @Nonnull
        public Set<Category> getCategories();

        public <V> V accept(DirectoryEntryVisitor<V> var1);
    }
}

