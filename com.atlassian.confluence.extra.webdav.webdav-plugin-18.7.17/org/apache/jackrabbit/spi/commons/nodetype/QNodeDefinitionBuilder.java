/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.commons.QNodeDefinitionImpl;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.nodetype.QItemDefinitionBuilder;

public class QNodeDefinitionBuilder
extends QItemDefinitionBuilder {
    private Name defaultPrimaryType;
    private Set<Name> requiredPrimaryTypes = new HashSet<Name>();
    private boolean allowsSameNameSiblings;

    public void setDefaultPrimaryType(Name name) {
        this.defaultPrimaryType = name;
    }

    public Name getDefaultPrimaryType() {
        return this.defaultPrimaryType;
    }

    public void addRequiredPrimaryType(Name name) {
        this.requiredPrimaryTypes.add(name);
    }

    public void setRequiredPrimaryTypes(Name[] names) {
        this.requiredPrimaryTypes.clear();
        if (names != null) {
            this.requiredPrimaryTypes.addAll(Arrays.asList(names));
        }
    }

    public Name[] getRequiredPrimaryTypes() {
        if (this.requiredPrimaryTypes.isEmpty()) {
            return new Name[]{NameConstants.NT_BASE};
        }
        return this.requiredPrimaryTypes.toArray(new Name[this.requiredPrimaryTypes.size()]);
    }

    public void setAllowsSameNameSiblings(boolean allowSns) {
        this.allowsSameNameSiblings = allowSns;
    }

    public boolean getAllowsSameNameSiblings() {
        return this.allowsSameNameSiblings;
    }

    public QNodeDefinition build() throws IllegalStateException {
        return new QNodeDefinitionImpl(this.getName(), this.getDeclaringNodeType(), this.getAutoCreated(), this.getMandatory(), this.getOnParentVersion(), this.getProtected(), this.getDefaultPrimaryType(), this.getRequiredPrimaryTypes(), this.getAllowsSameNameSiblings());
    }
}

