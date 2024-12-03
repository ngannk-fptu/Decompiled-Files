/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.jcr.NamespaceException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.commons.QItemDefinitionImpl;
import org.apache.jackrabbit.spi.commons.conversion.NameException;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;

public class QNodeDefinitionImpl
extends QItemDefinitionImpl
implements QNodeDefinition {
    private static final long serialVersionUID = -3671882394577685657L;
    private final Name defaultPrimaryType;
    private final Set<Name> requiredPrimaryTypes = new HashSet<Name>();
    private final boolean allowsSameNameSiblings;

    public QNodeDefinitionImpl(QNodeDefinition nodeDef) {
        this(nodeDef.getName(), nodeDef.getDeclaringNodeType(), nodeDef.isAutoCreated(), nodeDef.isMandatory(), nodeDef.getOnParentVersion(), nodeDef.isProtected(), nodeDef.getDefaultPrimaryType(), nodeDef.getRequiredPrimaryTypes(), nodeDef.allowsSameNameSiblings());
    }

    public QNodeDefinitionImpl(Name name, Name declaringNodeType, boolean isAutoCreated, boolean isMandatory, int onParentVersion, boolean isProtected, Name defaultPrimaryType, Name[] requiredPrimaryTypes, boolean allowsSameNameSiblings) {
        super(name, declaringNodeType, isAutoCreated, isMandatory, onParentVersion, isProtected);
        this.defaultPrimaryType = defaultPrimaryType;
        this.requiredPrimaryTypes.addAll(Arrays.asList(requiredPrimaryTypes));
        if (this.requiredPrimaryTypes.isEmpty()) {
            this.requiredPrimaryTypes.add(NameConstants.NT_BASE);
        }
        this.allowsSameNameSiblings = allowsSameNameSiblings;
    }

    public QNodeDefinitionImpl(NodeDefinition nodeDef, NamePathResolver resolver) throws NameException, NamespaceException {
        this(nodeDef.getName().equals(NameConstants.ANY_NAME.getLocalName()) ? NameConstants.ANY_NAME : resolver.getQName(nodeDef.getName()), nodeDef.getDeclaringNodeType() != null ? resolver.getQName(nodeDef.getDeclaringNodeType().getName()) : null, nodeDef.isAutoCreated(), nodeDef.isMandatory(), nodeDef.getOnParentVersion(), nodeDef.isProtected(), nodeDef.getDefaultPrimaryType() != null ? resolver.getQName(nodeDef.getDefaultPrimaryType().getName()) : null, QNodeDefinitionImpl.getNodeTypeNames(nodeDef.getRequiredPrimaryTypes(), resolver), nodeDef.allowsSameNameSiblings());
    }

    @Override
    public Name getDefaultPrimaryType() {
        return this.defaultPrimaryType;
    }

    @Override
    public Name[] getRequiredPrimaryTypes() {
        return this.requiredPrimaryTypes.toArray(new Name[this.requiredPrimaryTypes.size()]);
    }

    @Override
    public boolean allowsSameNameSiblings() {
        return this.allowsSameNameSiblings;
    }

    @Override
    public boolean definesNode() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof QNodeDefinition) {
            QNodeDefinition other = (QNodeDefinition)obj;
            return super.equals(obj) && this.requiredPrimaryTypes.equals(new HashSet<Name>(Arrays.asList(other.getRequiredPrimaryTypes()))) && (this.defaultPrimaryType == null ? other.getDefaultPrimaryType() == null : this.defaultPrimaryType.equals(other.getDefaultPrimaryType())) && this.allowsSameNameSiblings == other.allowsSameNameSiblings();
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int h = super.hashCode();
            h = 37 * h + (this.defaultPrimaryType == null ? 0 : this.defaultPrimaryType.hashCode());
            h = 37 * h + this.requiredPrimaryTypes.hashCode();
            this.hashCode = h = 37 * h + (this.allowsSameNameSiblings ? 11 : 43);
        }
        return this.hashCode;
    }

    private static Name[] getNodeTypeNames(NodeType[] nt, NamePathResolver resolver) throws NameException, NamespaceException {
        Name[] names = new Name[nt.length];
        for (int i = 0; i < nt.length; ++i) {
            Name ntName;
            names[i] = ntName = resolver.getQName(nt[i].getName());
        }
        return names;
    }
}

