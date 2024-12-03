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
public class LocalDashboardItemRepresentation
extends DashboardItemRepresentation {
    @XmlElement
    private final String completeModuleKey;

    public LocalDashboardItemRepresentation(String titleLabel, URI titleUri, String authorLabel, String description, String id, Collection<String> categories, URI thumbnailUri, String completeModuleKey) {
        super(titleLabel, titleUri, authorLabel, description, id, categories, thumbnailUri);
        this.completeModuleKey = completeModuleKey;
    }
}

