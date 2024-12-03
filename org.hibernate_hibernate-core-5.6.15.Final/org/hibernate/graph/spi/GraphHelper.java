/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Type
 */
package org.hibernate.graph.spi;

import javax.persistence.metamodel.Type;
import org.hibernate.metamodel.model.domain.spi.IdentifiableTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.MapPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.PluralPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SingularPersistentAttribute;

public class GraphHelper {
    public static <J> SimpleTypeDescriptor<J> resolveKeyTypeDescriptor(SingularPersistentAttribute attribute) {
        Type attributeType = attribute.getType();
        if (attributeType instanceof IdentifiableTypeDescriptor) {
            return ((IdentifiableTypeDescriptor)attributeType).getIdType();
        }
        return null;
    }

    public static <J> SimpleTypeDescriptor<J> resolveKeyTypeDescriptor(PluralPersistentAttribute attribute) {
        if (attribute instanceof SingularPersistentAttribute) {
            Type attributeType = ((SingularPersistentAttribute)((Object)attribute)).getType();
            if (attributeType instanceof IdentifiableTypeDescriptor) {
                return ((IdentifiableTypeDescriptor)attributeType).getIdType();
            }
            return null;
        }
        if (attribute instanceof PluralPersistentAttribute) {
            if (attribute instanceof MapPersistentAttribute) {
                return ((MapPersistentAttribute)attribute).getKeyType();
            }
            return null;
        }
        throw new IllegalArgumentException("Unexpected Attribute Class [" + attribute.getClass().getName() + "] - expecting SingularAttributeImplementor or PluralAttributeImplementor");
    }
}

