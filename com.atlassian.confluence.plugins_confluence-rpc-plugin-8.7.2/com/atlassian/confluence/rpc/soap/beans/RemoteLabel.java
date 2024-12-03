/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.labels.Label;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteLabel {
    private String name;
    private String owner;
    private String namespace;
    protected long id;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.labels.Label label \nequals java.lang.Object other \nsetId long id \nsetName java.lang.String name \nsetNamespace java.lang.String namespace \nsetOwner java.lang.String owner \n";

    public RemoteLabel() {
    }

    public RemoteLabel(Label label) {
        this.id = label.getId();
        this.name = label.getName();
        this.owner = label.getOwner();
        this.namespace = label.getNamespace().toString();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean equals(Object other) {
        if (!(other instanceof RemoteLabel)) {
            return false;
        }
        RemoteLabel otherLabel = (RemoteLabel)other;
        if (this.getId() == 0L && otherLabel.getId() == 0L) {
            return this == otherLabel;
        }
        return this.getId() == otherLabel.getId();
    }

    public int hashCode() {
        return (int)(this.id ^ this.id >>> 32);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}

