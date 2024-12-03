/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QItemDefinition;

public abstract class QItemDefinitionImpl
implements QItemDefinition,
Serializable {
    private final Name name;
    private final Name declaringNodeType;
    private final boolean autoCreated;
    private final int onParentVersion;
    private final boolean writeProtected;
    private final boolean mandatory;
    protected transient int hashCode = 0;

    QItemDefinitionImpl(Name name, Name declaringNodeType, boolean isAutoCreated, boolean isMandatory, int onParentVersion, boolean isProtected) {
        this.name = name;
        this.declaringNodeType = declaringNodeType;
        this.autoCreated = isAutoCreated;
        this.mandatory = isMandatory;
        this.onParentVersion = onParentVersion;
        this.writeProtected = isProtected;
    }

    @Override
    public Name getDeclaringNodeType() {
        return this.declaringNodeType;
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public boolean isAutoCreated() {
        return this.autoCreated;
    }

    @Override
    public int getOnParentVersion() {
        return this.onParentVersion;
    }

    @Override
    public boolean isProtected() {
        return this.writeProtected;
    }

    @Override
    public boolean isMandatory() {
        return this.mandatory;
    }

    @Override
    public boolean definesResidual() {
        return "".equals(this.name.getNamespaceURI()) && "*".equals(this.name.getLocalName());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof QItemDefinition) {
            QItemDefinition other = (QItemDefinition)obj;
            return (this.declaringNodeType == null ? other.getDeclaringNodeType() == null : this.declaringNodeType.equals(other.getDeclaringNodeType())) && (this.name == null ? other.getName() == null : this.name.equals(other.getName())) && this.autoCreated == other.isAutoCreated() && this.onParentVersion == other.getOnParentVersion() && this.writeProtected == other.isProtected() && this.mandatory == other.isMandatory();
        }
        return false;
    }

    public int hashCode() {
        int h = 17;
        h = 37 * h + this.getDeclaringNodeType().hashCode();
        h = 37 * h + this.getName().hashCode();
        h = 37 * h + this.getOnParentVersion();
        h = 37 * h + (this.isProtected() ? 11 : 43);
        h = 37 * h + (this.isMandatory() ? 11 : 43);
        h = 37 * h + (this.isAutoCreated() ? 11 : 43);
        return h;
    }
}

