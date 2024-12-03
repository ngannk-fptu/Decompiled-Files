/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.Category
 *  com.atlassian.gadgets.directory.Directory$OpenSocialDirectoryEntry
 *  com.atlassian.gadgets.directory.DirectoryEntryVisitor
 *  com.atlassian.gadgets.spec.Feature
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.gadgets.directory.Category;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.DirectoryEntryVisitor;
import com.atlassian.gadgets.spec.Feature;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

class GadgetSpecDirectoryEntry
implements Directory.OpenSocialDirectoryEntry {
    private static final String LINEBREAK_PATTERN_STRING = "\r\n|\r|\n";
    private final GadgetSpec spec;
    private final boolean isDeletable;
    private final URI selfUri;

    public GadgetSpecDirectoryEntry(GadgetSpec spec, boolean isDeleteable, URI selfUri) {
        this.spec = (GadgetSpec)Preconditions.checkNotNull((Object)spec, (Object)"spec");
        this.isDeletable = isDeleteable;
        this.selfUri = selfUri;
    }

    public URI getSelf() {
        return this.selfUri;
    }

    public boolean isDeletable() {
        return this.isDeletable;
    }

    public URI getId() {
        return (URI)Preconditions.checkNotNull((Object)this.spec.getUrl(), (Object)"spec url");
    }

    public String getTitle() {
        return StringUtils.defaultString((String)this.spec.getDirectoryTitle(), (String)StringUtils.defaultString((String)this.spec.getTitle()));
    }

    public URI getTitleUri() {
        return this.spec.getTitleUrl();
    }

    public URI getThumbnailUri() {
        return this.spec.getThumbnail();
    }

    public String getAuthorName() {
        return StringUtils.defaultString((String)this.spec.getAuthor());
    }

    public String getAuthorEmail() {
        return StringUtils.defaultString((String)this.spec.getAuthorEmail());
    }

    public String getDescription() {
        return StringUtils.defaultString((String)this.spec.getDescription());
    }

    @Nonnull
    public Set<Category> getCategories() {
        String categoriesString;
        HashSet<Category> categorySetForGadget = new HashSet<Category>();
        Feature gadgetDirectoryFeature = (Feature)this.spec.getFeatures().get("gadget-directory");
        if (gadgetDirectoryFeature != null && (categoriesString = gadgetDirectoryFeature.getParameterValue("categories")) != null) {
            String[] categories;
            for (String categoryString : categories = categoriesString.split(LINEBREAK_PATTERN_STRING)) {
                Category category = Category.named((String)categoryString.trim());
                if (category.equals((Object)Category.OTHER)) continue;
                categorySetForGadget.add(category);
            }
        }
        if (categorySetForGadget.isEmpty()) {
            categorySetForGadget.add(Category.OTHER);
        }
        return categorySetForGadget;
    }

    public <V> V accept(DirectoryEntryVisitor<V> visitor) {
        return (V)visitor.visit((Directory.OpenSocialDirectoryEntry)this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GadgetSpecDirectoryEntry that = (GadgetSpecDirectoryEntry)o;
        return this.isDeletable == that.isDeletable && !(this.selfUri == null ? that.selfUri != null : !this.selfUri.equals(that.selfUri)) && this.spec.equals(that.spec);
    }

    public int hashCode() {
        int result = this.spec.hashCode();
        result = 31 * result + (this.isDeletable ? 1 : 0);
        result = 31 * result + (this.selfUri != null ? this.selfUri.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "GadgetSpecDirectoryEntry{spec=" + this.spec + ", isDeletable=" + this.isDeletable + ", selfUri=" + this.selfUri + '}';
    }
}

