/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.google.common.base.Function
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.createcontent.rest.entities;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.GeneralUtil;
import com.google.common.base.Function;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SpaceEntity {
    @XmlElement
    private String id;
    @XmlElement
    private String text;

    private SpaceEntity() {
    }

    public SpaceEntity(Space space) {
        this(space.getKey(), space.getDisplayTitle());
    }

    SpaceEntity(String id, String text) {
        this.id = id;
        this.text = GeneralUtil.htmlEncode((String)text);
    }

    public String getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpaceEntity that = (SpaceEntity)o;
        if (this.id != null ? !this.id.equals(that.id) : that.id != null) {
            return false;
        }
        return this.text != null ? this.text.equals(that.text) : that.text == null;
    }

    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        result = 31 * result + (this.text != null ? this.text.hashCode() : 0);
        return result;
    }

    public String toString() {
        return this.id;
    }

    public static Function<Space, SpaceEntity> spaceTransformer() {
        return SpaceEntity::new;
    }
}

