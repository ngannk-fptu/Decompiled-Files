/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.PropertyDefinition;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueConstraint;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.commons.QNodeDefinitionImpl;
import org.apache.jackrabbit.spi.commons.QPropertyDefinitionImpl;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;
import org.apache.jackrabbit.spi.commons.value.ValueFormat;

public class QNodeTypeDefinitionImpl
implements QNodeTypeDefinition,
Serializable {
    private static final long serialVersionUID = -4065300714874671511L;
    private final Name name;
    private final Name[] supertypes;
    private final Name[] supportedMixins;
    private final boolean isMixin;
    private final boolean isAbstract;
    private final boolean isQueryable;
    private final boolean hasOrderableChildNodes;
    private final Name primaryItemName;
    private final Set<QPropertyDefinition> propertyDefs;
    private final Set<QNodeDefinition> childNodeDefs;
    private volatile transient Collection<Name> dependencies;

    public QNodeTypeDefinitionImpl() {
        this(null, Name.EMPTY_ARRAY, null, false, false, true, false, null, QPropertyDefinition.EMPTY_ARRAY, QNodeDefinition.EMPTY_ARRAY);
    }

    public QNodeTypeDefinitionImpl(QNodeTypeDefinition nt) {
        this(nt.getName(), nt.getSupertypes(), nt.getSupportedMixinTypes(), nt.isMixin(), nt.isAbstract(), nt.isQueryable(), nt.hasOrderableChildNodes(), nt.getPrimaryItemName(), nt.getPropertyDefs(), nt.getChildNodeDefs());
    }

    public QNodeTypeDefinitionImpl(Name name, Name[] supertypes, Name[] supportedMixins, boolean isMixin, boolean isAbstract, boolean isQueryable, boolean hasOrderableChildNodes, Name primaryItemName, QPropertyDefinition[] declaredPropDefs, QNodeDefinition[] declaredNodeDefs) {
        this.name = name;
        this.supportedMixins = supportedMixins;
        this.isMixin = isMixin;
        this.isAbstract = isAbstract;
        this.isQueryable = isQueryable;
        this.hasOrderableChildNodes = hasOrderableChildNodes;
        this.primaryItemName = primaryItemName;
        this.propertyDefs = QNodeTypeDefinitionImpl.getSerializablePropertyDefs(declaredPropDefs);
        this.childNodeDefs = QNodeTypeDefinitionImpl.getSerializableNodeDefs(declaredNodeDefs);
        TreeSet<Name> types = new TreeSet<Name>();
        types.addAll(Arrays.asList(supertypes));
        this.supertypes = types.toArray(new Name[types.size()]);
    }

    public QNodeTypeDefinitionImpl(NodeTypeDefinition def, NamePathResolver resolver, QValueFactory qValueFactory) throws RepositoryException {
        this(resolver.getQName(def.getName()), def, resolver, qValueFactory);
    }

    private QNodeTypeDefinitionImpl(Name name, NodeTypeDefinition def, NamePathResolver resolver, QValueFactory qValueFactory) throws RepositoryException {
        this(name, QNodeTypeDefinitionImpl.getNames(def.getDeclaredSupertypeNames(), resolver), null, def.isMixin(), def.isAbstract(), def.isQueryable(), def.hasOrderableChildNodes(), def.getPrimaryItemName() == null ? null : resolver.getQName(def.getPrimaryItemName()), QNodeTypeDefinitionImpl.createQPropertyDefinitions(name, def.getDeclaredPropertyDefinitions(), resolver, qValueFactory), QNodeTypeDefinitionImpl.createQNodeDefinitions(name, def.getDeclaredChildNodeDefinitions(), resolver));
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public Name[] getSupertypes() {
        if (this.supertypes.length > 0 || this.isMixin() || NameConstants.NT_BASE.equals(this.getName())) {
            return this.supertypes;
        }
        return new Name[]{NameConstants.NT_BASE};
    }

    @Override
    public boolean isMixin() {
        return this.isMixin;
    }

    @Override
    public boolean isAbstract() {
        return this.isAbstract;
    }

    @Override
    public boolean isQueryable() {
        return this.isQueryable;
    }

    @Override
    public boolean hasOrderableChildNodes() {
        return this.hasOrderableChildNodes;
    }

    @Override
    public Name getPrimaryItemName() {
        return this.primaryItemName;
    }

    @Override
    public QPropertyDefinition[] getPropertyDefs() {
        return this.propertyDefs.toArray(new QPropertyDefinition[this.propertyDefs.size()]);
    }

    @Override
    public QNodeDefinition[] getChildNodeDefs() {
        return this.childNodeDefs.toArray(new QNodeDefinition[this.childNodeDefs.size()]);
    }

    @Override
    public Collection<Name> getDependencies() {
        if (this.dependencies == null) {
            HashSet<Name> deps = new HashSet<Name>();
            deps.addAll(Arrays.asList(this.supertypes));
            for (QNodeDefinition childNodeDef : this.childNodeDefs) {
                Name[] ntNames;
                Name ntName = childNodeDef.getDefaultPrimaryType();
                if (ntName != null && !this.name.equals(ntName)) {
                    deps.add(ntName);
                }
                Name[] nameArray = ntNames = childNodeDef.getRequiredPrimaryTypes();
                int n = nameArray.length;
                for (int i = 0; i < n; ++i) {
                    Name ntName1 = nameArray[i];
                    if (ntName1 == null || this.name.equals(ntName1)) continue;
                    deps.add(ntName1);
                }
            }
            for (QPropertyDefinition propertyDef : this.propertyDefs) {
                QValueConstraint[] ca;
                if (propertyDef.getRequiredType() != 9 && propertyDef.getRequiredType() != 10 || (ca = propertyDef.getValueConstraints()) == null) continue;
                for (QValueConstraint aCa : ca) {
                    NameFactory factory = NameFactoryImpl.getInstance();
                    Name ntName = factory.create(aCa.getString());
                    if (this.name.equals(ntName)) continue;
                    deps.add(ntName);
                }
            }
            this.dependencies = Collections.unmodifiableCollection(deps);
        }
        return this.dependencies;
    }

    @Override
    public Name[] getSupportedMixinTypes() {
        if (this.supportedMixins == null) {
            return null;
        }
        Name[] mixins = new Name[this.supportedMixins.length];
        System.arraycopy(this.supportedMixins, 0, mixins, 0, this.supportedMixins.length);
        return mixins;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof QNodeTypeDefinitionImpl) {
            QNodeTypeDefinitionImpl other = (QNodeTypeDefinitionImpl)obj;
            return (this.name == null ? other.name == null : this.name.equals(other.name)) && (this.primaryItemName == null ? other.primaryItemName == null : this.primaryItemName.equals(other.primaryItemName)) && new HashSet<Name>(Arrays.asList(this.getSupertypes())).equals(new HashSet<Name>(Arrays.asList(other.getSupertypes()))) && this.isMixin == other.isMixin && this.hasOrderableChildNodes == other.hasOrderableChildNodes && this.isAbstract == other.isAbstract && this.isQueryable == other.isQueryable && this.propertyDefs.equals(other.propertyDefs) && this.childNodeDefs.equals(other.childNodeDefs);
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    private static Set<QPropertyDefinition> getSerializablePropertyDefs(QPropertyDefinition[] propDefs) {
        HashSet<QPropertyDefinition> defs = new HashSet<QPropertyDefinition>();
        for (QPropertyDefinition pd : propDefs) {
            if (pd instanceof Serializable) {
                defs.add(pd);
                continue;
            }
            defs.add(new QPropertyDefinitionImpl(pd));
        }
        return defs;
    }

    private static Set<QNodeDefinition> getSerializableNodeDefs(QNodeDefinition[] nodeDefs) {
        HashSet<QNodeDefinition> defs = new HashSet<QNodeDefinition>();
        for (QNodeDefinition nd : nodeDefs) {
            if (nd instanceof Serializable) {
                defs.add(nd);
                continue;
            }
            defs.add(new QNodeDefinitionImpl(nd));
        }
        return defs;
    }

    private static Name[] getNames(String[] jcrNames, NamePathResolver resolver) throws NamespaceException, IllegalNameException {
        Name[] names = new Name[jcrNames.length];
        for (int i = 0; i < jcrNames.length; ++i) {
            names[i] = resolver.getQName(jcrNames[i]);
        }
        return names;
    }

    private static QPropertyDefinition[] createQPropertyDefinitions(Name declName, PropertyDefinition[] pds, NamePathResolver resolver, QValueFactory qValueFactory) throws RepositoryException {
        if (pds == null || pds.length == 0) {
            return QPropertyDefinition.EMPTY_ARRAY;
        }
        QPropertyDefinition[] declaredPropDefs = new QPropertyDefinition[pds.length];
        for (int i = 0; i < pds.length; ++i) {
            Name name;
            PropertyDefinition propDef = pds[i];
            Name name2 = name = propDef.getName().equals(NameConstants.ANY_NAME.getLocalName()) ? NameConstants.ANY_NAME : resolver.getQName(propDef.getName());
            if (propDef.getDeclaringNodeType() != null && !declName.equals(resolver.getQName(propDef.getDeclaringNodeType().getName()))) {
                throw new RepositoryException("Property definition specified invalid declaring nodetype: " + propDef.getDeclaringNodeType().getName() + ", but should be " + declName);
            }
            QValue[] defVls = propDef.getDefaultValues() == null ? QValue.EMPTY_ARRAY : ValueFormat.getQValues(propDef.getDefaultValues(), resolver, qValueFactory);
            String[] jcrConstraints = propDef.getValueConstraints();
            QValueConstraint[] constraints = QValueConstraint.EMPTY_ARRAY;
            if (jcrConstraints != null && jcrConstraints.length > 0) {
                constraints = new QValueConstraint[jcrConstraints.length];
                for (int j = 0; j < constraints.length; ++j) {
                    constraints[j] = ValueConstraint.create(propDef.getRequiredType(), jcrConstraints[j], resolver);
                }
            }
            declaredPropDefs[i] = new QPropertyDefinitionImpl(name, declName, propDef.isAutoCreated(), propDef.isMandatory(), propDef.getOnParentVersion(), propDef.isProtected(), defVls, propDef.isMultiple(), propDef.getRequiredType(), constraints, propDef.getAvailableQueryOperators(), propDef.isFullTextSearchable(), propDef.isQueryOrderable());
        }
        return declaredPropDefs;
    }

    private static QNodeDefinition[] createQNodeDefinitions(Name declName, NodeDefinition[] nds, NamePathResolver resolver) throws RepositoryException {
        if (nds == null || nds.length == 0) {
            return QNodeDefinition.EMPTY_ARRAY;
        }
        QNodeDefinition[] declaredNodeDefs = new QNodeDefinition[nds.length];
        for (int i = 0; i < nds.length; ++i) {
            Name name;
            NodeDefinition nodeDef = nds[i];
            Name name2 = name = nodeDef.getName().equals(NameConstants.ANY_NAME.getLocalName()) ? NameConstants.ANY_NAME : resolver.getQName(nodeDef.getName());
            if (nodeDef.getDeclaringNodeType() != null && !declName.equals(resolver.getQName(nodeDef.getDeclaringNodeType().getName()))) {
                throw new RepositoryException("Childnode definition specified invalid declaring nodetype: " + nodeDef.getDeclaringNodeType().getName() + ", but should be " + declName);
            }
            Name defaultPrimaryType = nodeDef.getDefaultPrimaryTypeName() == null ? null : resolver.getQName(nodeDef.getDefaultPrimaryTypeName());
            Name[] requiredPrimaryTypes = QNodeTypeDefinitionImpl.getNames(nodeDef.getRequiredPrimaryTypeNames(), resolver);
            declaredNodeDefs[i] = new QNodeDefinitionImpl(name, declName, nodeDef.isAutoCreated(), nodeDef.isMandatory(), nodeDef.getOnParentVersion(), nodeDef.isProtected(), defaultPrimaryType, requiredPrimaryTypes, nodeDef.allowsSameNameSiblings());
        }
        return declaredNodeDefs;
    }
}

