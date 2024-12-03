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
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.gadgets.directory.internal.jaxb;

import com.atlassian.gadgets.directory.Category;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.DirectoryEntryVisitor;
import com.atlassian.plugin.ModuleCompleteKey;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement
public final class JAXBDirectoryEntry {
    @XmlElement
    private final URI self;
    @XmlElement
    private final Boolean isDeletable;
    @XmlElement
    private final String authorEmail;
    @XmlElement
    private final String authorName;
    @XmlElement
    private final Collection<String> categories;
    @XmlElement
    private final String description;
    @XmlElement
    private URI gadgetSpecUri;
    @XmlElement
    private String moduleId;
    @XmlElement
    private final URI thumbnailUri;
    @XmlElement
    private final String title;
    @XmlElement
    private final URI titleUri;

    private JAXBDirectoryEntry() {
        this.self = null;
        this.isDeletable = null;
        this.authorEmail = null;
        this.authorName = null;
        this.categories = null;
        this.description = null;
        this.gadgetSpecUri = null;
        this.thumbnailUri = null;
        this.title = null;
        this.titleUri = null;
        this.moduleId = null;
    }

    public JAXBDirectoryEntry(Directory.Entry<?> entry) {
        this.self = entry.getSelf();
        this.isDeletable = entry.isDeletable();
        this.authorEmail = entry.getAuthorEmail();
        this.authorName = entry.getAuthorName();
        this.categories = this.transformCollectionCategoriesToNameStrings(entry.getCategories());
        this.description = entry.getDescription();
        this.thumbnailUri = entry.getThumbnailUri();
        this.title = entry.getTitle();
        this.titleUri = entry.getTitleUri();
        entry.accept((DirectoryEntryVisitor)new DirectoryEntryVisitor<Void>(){

            public Void visit(Directory.OpenSocialDirectoryEntry openSocialDirectoryEntry) {
                JAXBDirectoryEntry.this.gadgetSpecUri = (URI)openSocialDirectoryEntry.getId();
                return null;
            }

            public Void visit(Directory.DashboardDirectoryEntry dashboardDirectoryEntry) {
                JAXBDirectoryEntry.this.moduleId = ((ModuleCompleteKey)dashboardDirectoryEntry.getId()).getCompleteKey();
                return null;
            }
        });
    }

    private Collection<String> transformCollectionCategoriesToNameStrings(Collection<Category> from) {
        HashSet<String> result = new HashSet<String>();
        for (Category category : from) {
            result.add(category.getName());
        }
        return result;
    }

    public URI getSelf() {
        return this.self;
    }

    public Boolean isDeletable() {
        return this.isDeletable;
    }

    public String getAuthorEmail() {
        return this.authorEmail;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public Collection<String> getCategories() {
        return this.categories;
    }

    public String getDescription() {
        return this.description;
    }

    public URI getGadgetSpecUri() {
        return this.gadgetSpecUri;
    }

    public String getModuleId() {
        return this.moduleId;
    }

    public URI getThumbnailUri() {
        return this.thumbnailUri;
    }

    public String getTitle() {
        return this.title;
    }

    public URI getTitleUri() {
        return this.titleUri;
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 29).append((Object)this.self).append((Object)this.isDeletable).append((Object)this.authorEmail).append((Object)this.authorName).append(this.categories).append((Object)this.description).append((Object)this.moduleId).append((Object)this.gadgetSpecUri).append((Object)this.thumbnailUri).append((Object)this.title).append((Object)this.titleUri).toHashCode();
    }

    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (this == that) {
            return true;
        }
        if (that.getClass() != this.getClass()) {
            return false;
        }
        JAXBDirectoryEntry other = (JAXBDirectoryEntry)that;
        return new EqualsBuilder().append((Object)this.self, (Object)other.self).append((Object)this.isDeletable, (Object)other.isDeletable).append((Object)this.authorEmail, (Object)other.authorEmail).append((Object)this.authorName, (Object)other.authorName).append(this.categories, other.categories).append((Object)this.description, (Object)other.description).append((Object)this.gadgetSpecUri, (Object)other.gadgetSpecUri).append((Object)this.thumbnailUri, (Object)other.thumbnailUri).append((Object)this.title, (Object)other.title).append((Object)this.titleUri, (Object)other.titleUri).append((Object)this.moduleId, (Object)other.moduleId).isEquals();
    }
}

