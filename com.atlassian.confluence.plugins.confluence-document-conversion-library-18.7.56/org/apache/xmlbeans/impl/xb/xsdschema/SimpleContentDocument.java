/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleExtensionType;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleRestrictionType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface SimpleContentDocument
extends XmlObject {
    public static final DocumentFactory<SimpleContentDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "simplecontent8acedoctype");
    public static final SchemaType type = Factory.getType();

    public SimpleContent getSimpleContent();

    public void setSimpleContent(SimpleContent var1);

    public SimpleContent addNewSimpleContent();

    public static interface SimpleContent
    extends Annotated {
        public static final ElementFactory<SimpleContent> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "simplecontent9a5belemtype");
        public static final SchemaType type = Factory.getType();

        public SimpleRestrictionType getRestriction();

        public boolean isSetRestriction();

        public void setRestriction(SimpleRestrictionType var1);

        public SimpleRestrictionType addNewRestriction();

        public void unsetRestriction();

        public SimpleExtensionType getExtension();

        public boolean isSetExtension();

        public void setExtension(SimpleExtensionType var1);

        public SimpleExtensionType addNewExtension();

        public void unsetExtension();
    }
}

