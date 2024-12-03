/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.atlassian.plugins.rest.common.expand.Expander
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.plugin.rest.entity.GroupEntityExpander;
import com.atlassian.crowd.plugin.rest.entity.MultiValuedAttributeEntityList;
import com.atlassian.crowd.plugin.rest.entity.NamedEntity;
import com.atlassian.crowd.plugin.rest.util.LinkUriHelper;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.Expander;
import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement(name="group")
@XmlAccessorType(value=XmlAccessType.FIELD)
@Expander(value=GroupEntityExpander.class)
public class GroupEntity
implements NamedEntity {
    public static final String GROUP_ATTRIBUTES_FIELD_NAME = "attributes";
    @XmlAttribute
    private String expand;
    @XmlElement(name="link")
    private Link link;
    @XmlAttribute(name="name")
    private String name;
    @XmlElement(name="description")
    private String description;
    @XmlElement
    private final GroupType type;
    @XmlElement(name="active")
    private Boolean active;
    @Expandable
    @XmlElement(name="attributes")
    private MultiValuedAttributeEntityList attributes;
    @XmlTransient
    private String applicationName;

    private GroupEntity() {
        this.name = null;
        this.description = null;
        this.type = null;
        this.active = null;
        this.link = null;
    }

    public GroupEntity(String name, String description, GroupType type, Boolean active, Link link) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.active = active;
        this.link = link;
    }

    public String getDescription() {
        return this.description;
    }

    public GroupType getType() {
        return this.type;
    }

    public Boolean isActive() {
        return this.active;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setAttributes(MultiValuedAttributeEntityList attributes) {
        this.attributes = attributes;
    }

    public MultiValuedAttributeEntityList getAttributes() {
        return this.attributes;
    }

    String getApplicationName() {
        return this.applicationName;
    }

    public static GroupEntity newMinimalGroupEntity(String name, String applicationName, URI baseURI) {
        GroupEntity group = new GroupEntity(name, null, null, null, LinkUriHelper.buildGroupLink(baseURI, name));
        group.applicationName = applicationName;
        return group;
    }

    public boolean isExpanded() {
        return this.applicationName == null;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.getName()).append("active", (Object)this.isActive()).append("description", (Object)this.getDescription()).append("type", (Object)this.getType()).toString();
    }

    public Link getLink() {
        return this.link;
    }
}

