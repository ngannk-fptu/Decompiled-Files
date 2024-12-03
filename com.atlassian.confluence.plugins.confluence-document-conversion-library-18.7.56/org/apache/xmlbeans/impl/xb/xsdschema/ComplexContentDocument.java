/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexRestrictionType;
import org.apache.xmlbeans.impl.xb.xsdschema.ExtensionType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface ComplexContentDocument
extends XmlObject {
    public static final DocumentFactory<ComplexContentDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "complexcontentc57adoctype");
    public static final SchemaType type = Factory.getType();

    public ComplexContent getComplexContent();

    public void setComplexContent(ComplexContent var1);

    public ComplexContent addNewComplexContent();

    public static interface ComplexContent
    extends Annotated {
        public static final ElementFactory<ComplexContent> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "complexcontentaa7felemtype");
        public static final SchemaType type = Factory.getType();

        public ComplexRestrictionType getRestriction();

        public boolean isSetRestriction();

        public void setRestriction(ComplexRestrictionType var1);

        public ComplexRestrictionType addNewRestriction();

        public void unsetRestriction();

        public ExtensionType getExtension();

        public boolean isSetExtension();

        public void setExtension(ExtensionType var1);

        public ExtensionType addNewExtension();

        public void unsetExtension();

        public boolean getMixed();

        public XmlBoolean xgetMixed();

        public boolean isSetMixed();

        public void setMixed(boolean var1);

        public void xsetMixed(XmlBoolean var1);

        public void unsetMixed();
    }
}

