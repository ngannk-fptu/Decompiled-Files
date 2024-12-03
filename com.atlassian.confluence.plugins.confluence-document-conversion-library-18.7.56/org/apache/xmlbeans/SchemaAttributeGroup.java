/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaAnnotated;
import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.SchemaTypeSystem;

public interface SchemaAttributeGroup
extends SchemaComponent,
SchemaAnnotated {
    @Override
    public int getComponentType();

    @Override
    public QName getName();

    public Object getUserData();

    public static final class Ref
    extends SchemaComponent.Ref {
        public Ref(SchemaAttributeGroup attributeGroup) {
            super(attributeGroup);
        }

        public Ref(SchemaTypeSystem system, String handle) {
            super(system, handle);
        }

        @Override
        public final int getComponentType() {
            return 4;
        }

        public final SchemaAttributeGroup get() {
            return (SchemaAttributeGroup)this.getComponent();
        }
    }
}

