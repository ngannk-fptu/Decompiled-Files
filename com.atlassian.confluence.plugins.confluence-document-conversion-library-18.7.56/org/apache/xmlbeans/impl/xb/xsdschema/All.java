/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.AllNNI;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface All
extends ExplicitGroup {
    public static final DocumentFactory<All> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "all3c04type");
    public static final SchemaType type = Factory.getType();

    public static interface MaxOccurs
    extends AllNNI {
        public static final ElementFactory<MaxOccurs> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "maxoccurse8b1attrtype");
        public static final SchemaType type = Factory.getType();

        @Override
        public Object getObjectValue();

        @Override
        public void setObjectValue(Object var1);

        @Override
        public SchemaType instanceType();
    }

    public static interface MinOccurs
    extends XmlNonNegativeInteger {
        public static final ElementFactory<MinOccurs> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "minoccurs9283attrtype");
        public static final SchemaType type = Factory.getType();
    }
}

