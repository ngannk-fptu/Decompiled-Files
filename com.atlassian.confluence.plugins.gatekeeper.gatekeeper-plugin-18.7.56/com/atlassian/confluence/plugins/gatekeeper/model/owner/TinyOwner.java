/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.model.owner;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE, creatorVisibility=JsonAutoDetect.Visibility.NONE)
public abstract class TinyOwner
implements Cloneable {
    public static final String ANONYMOUS_NAME = "<anonymous>";
    public static final String CONFLUENCE_ADMINISTRATORS_GROUP_NAME = "confluence-administrators";
    @JsonProperty(value="n")
    protected String name;
    @JsonProperty(value="d")
    @JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
    protected String displayName = "";
    @JsonProperty(value="f")
    protected int flags = 0;

    protected TinyOwner() {
    }

    protected TinyOwner(String name, boolean active) {
        this.name = name.intern();
        this.flags = active ? 2 : 0;
    }

    protected TinyOwner(String name, String displayName, boolean active, boolean canUse) {
        this.name = name.intern();
        this.displayName = displayName.intern();
        this.flags = (active ? 2 : 0) + (canUse ? 1 : 0);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name.intern();
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName.intern();
    }

    public boolean isActive() {
        return (this.flags & 2) != 0;
    }

    public void setActive(boolean active) {
        this.flags = (this.flags & 1) + (active ? 2 : 0);
    }

    public boolean hasCanUse() {
        return (this.flags & 1) != 0;
    }

    public void setCanUse(boolean canUse) {
        this.flags = (this.flags & 2) + (canUse ? 1 : 0);
    }

    public boolean canLogin() {
        return this.flags == 3;
    }

    public abstract boolean isAnonymous();

    public abstract boolean isUser();

    public abstract boolean isGroup();

    public abstract boolean equals(Object var1);

    public abstract int hashCode();

    public String toString() {
        return this.getClass().getSimpleName() + "{, name='" + this.name + "', displayName='" + this.displayName + "', flags=" + this.flags + "}";
    }
}

