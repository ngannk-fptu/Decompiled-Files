/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface ImportDocument
extends XmlObject {
    public static final DocumentFactory<ImportDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "import99fedoctype");
    public static final SchemaType type = Factory.getType();

    public Import getImport();

    public void setImport(Import var1);

    public Import addNewImport();

    public static interface Import
    extends Annotated {
        public static final ElementFactory<Import> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "importe2ffelemtype");
        public static final SchemaType type = Factory.getType();

        public String getNamespace();

        public XmlAnyURI xgetNamespace();

        public boolean isSetNamespace();

        public void setNamespace(String var1);

        public void xsetNamespace(XmlAnyURI var1);

        public void unsetNamespace();

        public String getSchemaLocation();

        public XmlAnyURI xgetSchemaLocation();

        public boolean isSetSchemaLocation();

        public void setSchemaLocation(String var1);

        public void xsetSchemaLocation(XmlAnyURI var1);

        public void unsetSchemaLocation();
    }
}

