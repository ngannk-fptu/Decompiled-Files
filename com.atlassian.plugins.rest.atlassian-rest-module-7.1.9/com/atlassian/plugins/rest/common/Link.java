/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.plugins.rest.common;

import java.net.URI;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement
public class Link {
    @XmlAttribute
    private final URI href;
    @XmlAttribute
    private final String type;
    @XmlAttribute
    private final String rel;

    private Link() {
        this.href = null;
        this.rel = null;
        this.type = null;
    }

    private Link(URI href, String rel, String type) {
        this.href = Objects.requireNonNull(href);
        this.rel = Objects.requireNonNull(rel);
        this.type = type;
    }

    public static Link link(URI uri, String rel) {
        return new Link(uri, rel, null);
    }

    public static Link link(URI uri, String rel, String type) {
        return new Link(uri, rel, type);
    }

    public static Link self(URI uri) {
        return Link.link(uri, "self");
    }

    public static Link edit(URI uri) {
        return Link.link(uri, "edit");
    }

    public static Link add(URI uri) {
        return Link.link(uri, "add");
    }

    public static Link delete(URI uri) {
        return Link.link(uri, "delete");
    }

    public URI getHref() {
        return this.href;
    }

    public String getRel() {
        return this.rel;
    }

    public int hashCode() {
        return new HashCodeBuilder(3, 7).append((Object)this.href).append((Object)this.rel).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Link link = (Link)obj;
        return new EqualsBuilder().append((Object)this.href, (Object)link.href).append((Object)this.rel, (Object)link.rel).isEquals();
    }

    public String toString() {
        return "Link{href=" + this.href + ", type='" + this.type + '\'' + ", rel='" + this.rel + '\'' + '}';
    }
}

