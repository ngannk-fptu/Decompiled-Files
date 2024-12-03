/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xmlschema.LangAttribute;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface DocumentationDocument
extends XmlObject {
    public static final DocumentFactory<DocumentationDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "documentation6cdbdoctype");
    public static final SchemaType type = Factory.getType();

    public Documentation getDocumentation();

    public void setDocumentation(Documentation var1);

    public Documentation addNewDocumentation();

    public static interface Documentation
    extends XmlObject {
        public static final ElementFactory<Documentation> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "documentationa475elemtype");
        public static final SchemaType type = Factory.getType();

        public String getSource();

        public XmlAnyURI xgetSource();

        public boolean isSetSource();

        public void setSource(String var1);

        public void xsetSource(XmlAnyURI var1);

        public void unsetSource();

        public String getLang();

        public LangAttribute.Lang xgetLang();

        public boolean isSetLang();

        public void setLang(String var1);

        public void xsetLang(LangAttribute.Lang var1);

        public void unsetLang();
    }
}

