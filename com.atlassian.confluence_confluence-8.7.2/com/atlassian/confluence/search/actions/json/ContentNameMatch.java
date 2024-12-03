/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.search.actions.json;

import com.atlassian.confluence.json.jsonator.Gsonable;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ContentNameMatch
implements Serializable,
Gsonable {
    private String id;
    private String name;
    private String href;
    private String className;
    private String icon;
    private String spaceName;
    private String spaceKey;
    private String username;

    public ContentNameMatch() {
    }

    public ContentNameMatch(String className, String name, String href) {
        this(className, href, null, name, null, null);
    }

    public ContentNameMatch(String className, String location, String icon, String name, String spaceName, String spaceKey) {
        this.className = className;
        this.href = location;
        this.icon = icon;
        this.name = name;
        this.spaceName = spaceName;
        this.spaceKey = spaceKey;
    }

    @XmlElement
    public String getId() {
        return this.id;
    }

    @XmlElement
    public String getName() {
        return this.name;
    }

    @XmlElement
    public String getHref() {
        return this.href;
    }

    @XmlElement
    public String getClassName() {
        return this.className;
    }

    @XmlElement
    public String getIcon() {
        return this.icon;
    }

    @XmlElement
    public String getSpaceName() {
        return this.spaceName;
    }

    @XmlElement
    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(null);
        builder.append("name", (Object)this.name).append("href", (Object)this.href).append("category", (Object)this.className).append("icon", (Object)this.icon).append("spaceKey", (Object)this.spaceKey).append("spaceName", (Object)this.spaceName);
        return builder.toString();
    }
}

