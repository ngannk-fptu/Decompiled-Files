/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.name.NameConstants;

public abstract class QItemDefinitionBuilder {
    private Name name = NameConstants.ANY_NAME;
    private Name declaringType = null;
    private boolean isAutocreated = false;
    private int onParentVersion = 1;
    private boolean isProtected = false;
    private boolean isMandatory = false;

    public void setName(Name name) {
        this.name = name;
    }

    public Name getName() {
        return this.name;
    }

    public void setDeclaringNodeType(Name type) {
        this.declaringType = type;
    }

    public Name getDeclaringNodeType() {
        return this.declaringType;
    }

    public void setAutoCreated(boolean autocreate) {
        this.isAutocreated = autocreate;
    }

    public boolean getAutoCreated() {
        return this.isAutocreated;
    }

    public void setOnParentVersion(int onParent) {
        this.onParentVersion = onParent;
    }

    public int getOnParentVersion() {
        return this.onParentVersion;
    }

    public void setProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }

    public boolean getProtected() {
        return this.isProtected;
    }

    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public boolean getMandatory() {
        return this.isMandatory;
    }
}

