/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.AllNNI;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface AnyDocument
extends XmlObject {
    public static final DocumentFactory<AnyDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "anye729doctype");
    public static final SchemaType type = Factory.getType();

    public Any getAny();

    public void setAny(Any var1);

    public Any addNewAny();

    public static interface Any
    extends Wildcard {
        public static final ElementFactory<Any> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anye9d1elemtype");
        public static final SchemaType type = Factory.getType();

        public BigInteger getMinOccurs();

        public XmlNonNegativeInteger xgetMinOccurs();

        public boolean isSetMinOccurs();

        public void setMinOccurs(BigInteger var1);

        public void xsetMinOccurs(XmlNonNegativeInteger var1);

        public void unsetMinOccurs();

        public Object getMaxOccurs();

        public AllNNI xgetMaxOccurs();

        public boolean isSetMaxOccurs();

        public void setMaxOccurs(Object var1);

        public void xsetMaxOccurs(AllNNI var1);

        public void unsetMaxOccurs();
    }
}

