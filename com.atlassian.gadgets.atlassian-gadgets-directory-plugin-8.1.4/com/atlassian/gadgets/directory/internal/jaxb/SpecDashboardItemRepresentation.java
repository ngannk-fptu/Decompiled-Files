/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.gadgets.directory.internal.jaxb;

import com.atlassian.gadgets.directory.internal.jaxb.DashboardItemRepresentation;
import java.net.URI;
import java.util.Collection;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
public final class SpecDashboardItemRepresentation
extends DashboardItemRepresentation {
    @XmlElement
    private final URI specUri;

    public SpecDashboardItemRepresentation(String titleLabel, URI titleUri, String authorLabel, String description, String id, Collection<String> categories, URI thumbnailUri, URI specUri) {
        super(titleLabel, titleUri, authorLabel, description, id, categories, thumbnailUri);
        this.specUri = specUri;
    }
}

