/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.commons.QNodeTypeDefinitionImpl;
import org.apache.jackrabbit.spi.commons.name.NameConstants;

public class QNodeTypeDefinitionBuilder {
    private Name name = null;
    private List<Name> supertypes = new ArrayList<Name>();
    private boolean isMixin = false;
    private boolean isOrderable = false;
    private Name primaryItemName = null;
    private List<QPropertyDefinition> propertyDefinitions = new ArrayList<QPropertyDefinition>();
    private List<QNodeDefinition> childNodeDefinitions = new ArrayList<QNodeDefinition>();
    private boolean isAbstract = false;
    private boolean isQueryable = true;
    private List<Name> supportedMixins = null;

    public void setName(Name name) {
        this.name = name;
    }

    public Name getName() {
        return this.name;
    }

    public void setSupertypes(Name[] supertypes) {
        this.supertypes.clear();
        this.supertypes.addAll(Arrays.asList(supertypes));
    }

    public Name[] getSuperTypes() {
        if (this.supertypes.size() > 0 || this.isMixin() || NameConstants.NT_BASE.equals(this.getName())) {
            return this.supertypes.toArray(new Name[this.supertypes.size()]);
        }
        return new Name[]{NameConstants.NT_BASE};
    }

    public void setMixin(boolean isMixin) {
        this.isMixin = isMixin;
    }

    public boolean isMixin() {
        return this.isMixin;
    }

    public void setSupportedMixinTypes(Name[] names) {
        this.supportedMixins = names == null ? null : new ArrayList<Name>(Arrays.asList(names));
    }

    public Name[] getSupportedMixinTypes() {
        if (this.supportedMixins == null) {
            return null;
        }
        return this.supportedMixins.toArray(new Name[this.supportedMixins.size()]);
    }

    public void setOrderableChildNodes(boolean isOrderable) {
        this.isOrderable = isOrderable;
    }

    public boolean hasOrderableChildNodes() {
        return this.isOrderable;
    }

    public void setPrimaryItemName(Name primaryItemName) {
        this.primaryItemName = primaryItemName;
    }

    public Name getPrimaryItemName() {
        return this.primaryItemName;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean isQueryable() {
        return this.isQueryable;
    }

    public void setQueryable(boolean queryable) {
        this.isQueryable = queryable;
    }

    public void setPropertyDefs(QPropertyDefinition[] propDefs) {
        this.propertyDefinitions.clear();
        this.propertyDefinitions.addAll(Arrays.asList(propDefs));
    }

    public QPropertyDefinition[] getPropertyDefs() {
        return this.propertyDefinitions.toArray(new QPropertyDefinition[this.propertyDefinitions.size()]);
    }

    public void setChildNodeDefs(QNodeDefinition[] childDefs) {
        this.childNodeDefinitions.clear();
        this.childNodeDefinitions.addAll(Arrays.asList(childDefs));
    }

    public QNodeDefinition[] getChildNodeDefs() {
        return this.childNodeDefinitions.toArray(new QNodeDefinition[this.childNodeDefinitions.size()]);
    }

    public QNodeTypeDefinition build() throws IllegalStateException {
        return new QNodeTypeDefinitionImpl(this.getName(), this.getSuperTypes(), this.getSupportedMixinTypes(), this.isMixin(), this.isAbstract(), this.isQueryable(), this.hasOrderableChildNodes(), this.getPrimaryItemName(), this.getPropertyDefs(), this.getChildNodeDefs());
    }
}

