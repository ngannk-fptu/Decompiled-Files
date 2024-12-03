/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import com.atlassian.applinks.core.rest.model.AuthenticationProviderEntity;
import com.atlassian.applinks.core.rest.model.ConsumerEntity;
import com.atlassian.plugins.rest.common.Link;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(value={ApplicationLinkEntity.class, AuthenticationProviderEntity.class, ConsumerEntity.class})
public class LinkedEntity {
    @XmlElement(name="link")
    private List<Link> links;

    public LinkedEntity() {
    }

    protected LinkedEntity(List<Link> links) {
        if (links != null) {
            for (Link link : links) {
                this.addLink(link);
            }
        }
    }

    public LinkedEntity(Link ... links) {
        if (links != null) {
            for (Link link : links) {
                this.addLink(link);
            }
        }
    }

    public void addLink(Link link) {
        if (this.links == null) {
            this.links = new ArrayList<Link>();
        }
        this.links.add(link);
    }

    protected List<Link> getLinks() {
        return this.links;
    }
}

