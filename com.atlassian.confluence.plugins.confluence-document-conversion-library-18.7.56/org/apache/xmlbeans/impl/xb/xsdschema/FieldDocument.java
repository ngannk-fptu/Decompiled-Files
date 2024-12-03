/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface FieldDocument
extends XmlObject {
    public static final DocumentFactory<FieldDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "field3f9bdoctype");
    public static final SchemaType type = Factory.getType();

    public Field getField();

    public void setField(Field var1);

    public Field addNewField();

    public static interface Field
    extends Annotated {
        public static final ElementFactory<Field> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "field12f5elemtype");
        public static final SchemaType type = Factory.getType();

        public String getXpath();

        public Xpath xgetXpath();

        public void setXpath(String var1);

        public void xsetXpath(Xpath var1);

        public static interface Xpath
        extends XmlToken {
            public static final ElementFactory<Xpath> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "xpath7f90attrtype");
            public static final SchemaType type = Factory.getType();
        }
    }
}

