/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface AppinfoDocument
extends XmlObject {
    public static final DocumentFactory<AppinfoDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "appinfo2ea6doctype");
    public static final SchemaType type = Factory.getType();

    public Appinfo getAppinfo();

    public void setAppinfo(Appinfo var1);

    public Appinfo addNewAppinfo();

    public static interface Appinfo
    extends XmlObject {
        public static final ElementFactory<Appinfo> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "appinfo650belemtype");
        public static final SchemaType type = Factory.getType();

        public String getSource();

        public XmlAnyURI xgetSource();

        public boolean isSetSource();

        public void setSource(String var1);

        public void xsetSource(XmlAnyURI var1);

        public void unsetSource();
    }
}

