/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.gadgets.directory.internal.jaxb;

import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;

public abstract class DashboardItemRepresentation {
    @XmlElement
    private final String id;
    @XmlElement
    private final String titleLabel;
    @XmlElement
    private final URI titleUri;
    @XmlElement
    private final String authorLabel;
    @XmlElement
    private final String description;
    @XmlElement
    private final Collection<String> categories;
    @XmlElement
    private final URI thumbnailUri;

    public DashboardItemRepresentation(String titleLabel, URI titleUri, String authorLabel, String description, String id, Collection<String> categories, URI thumbnailUri) {
        this.id = id;
        this.titleLabel = titleLabel;
        this.titleUri = titleUri;
        this.authorLabel = authorLabel;
        this.description = description;
        this.categories = ImmutableList.copyOf(categories);
        this.thumbnailUri = thumbnailUri;
    }
}

