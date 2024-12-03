/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTSchema
extends XmlObject {
    public static final DocumentFactory<CTSchema> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctschema0e6atype");
    public static final SchemaType type = Factory.getType();

    public String getID();

    public XmlString xgetID();

    public void setID(String var1);

    public void xsetID(XmlString var1);

    public String getSchemaRef();

    public XmlString xgetSchemaRef();

    public boolean isSetSchemaRef();

    public void setSchemaRef(String var1);

    public void xsetSchemaRef(XmlString var1);

    public void unsetSchemaRef();

    public String getNamespace();

    public XmlString xgetNamespace();

    public boolean isSetNamespace();

    public void setNamespace(String var1);

    public void xsetNamespace(XmlString var1);

    public void unsetNamespace();

    public String getSchemaLanguage();

    public XmlToken xgetSchemaLanguage();

    public boolean isSetSchemaLanguage();

    public void setSchemaLanguage(String var1);

    public void xsetSchemaLanguage(XmlToken var1);

    public void unsetSchemaLanguage();
}

