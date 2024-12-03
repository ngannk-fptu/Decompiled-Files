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

public interface IncludeDocument
extends XmlObject {
    public static final DocumentFactory<IncludeDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "includeaf6ddoctype");
    public static final SchemaType type = Factory.getType();

    public Include getInclude();

    public void setInclude(Include var1);

    public Include addNewInclude();

    public static interface Include
    extends Annotated {
        public static final ElementFactory<Include> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "include59d9elemtype");
        public static final SchemaType type = Factory.getType();

        public String getSchemaLocation();

        public XmlAnyURI xgetSchemaLocation();

        public void setSchemaLocation(String var1);

        public void xsetSchemaLocation(XmlAnyURI var1);
    }
}

