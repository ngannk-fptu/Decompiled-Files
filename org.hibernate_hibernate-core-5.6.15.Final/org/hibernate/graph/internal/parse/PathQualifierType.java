/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.graph.internal.parse;

import org.hibernate.graph.CannotContainSubGraphException;
import org.hibernate.graph.internal.parse.SubGraphGenerator;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;

public enum PathQualifierType {
    KEY((attributeNode, subTypeName, sessionFactory) -> attributeNode.makeKeySubGraph(PathQualifierType.resolveSubTypeManagedType(attributeNode.getAttributeDescriptor().getKeyGraphType(), subTypeName))),
    VALUE((attributeNode, subTypeName, sessionFactory) -> attributeNode.makeSubGraph(PathQualifierType.resolveSubTypeManagedType(attributeNode.getAttributeDescriptor().getValueGraphType(), subTypeName)));

    private final SubGraphGenerator subGraphCreator;

    private static ManagedTypeDescriptor resolveSubTypeManagedType(SimpleTypeDescriptor<?> graphType, String subTypeName) {
        if (!(graphType instanceof ManagedTypeDescriptor)) {
            throw new CannotContainSubGraphException("The given type [" + graphType + "] is not a ManagedType");
        }
        ManagedTypeDescriptor managedType = (ManagedTypeDescriptor)graphType;
        if (subTypeName != null) {
            managedType = managedType.findSubType(subTypeName);
        }
        return managedType;
    }

    private PathQualifierType(SubGraphGenerator subGraphCreator) {
        this.subGraphCreator = subGraphCreator;
    }

    public SubGraphGenerator getSubGraphCreator() {
        return this.subGraphCreator;
    }
}

