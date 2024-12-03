/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDisplacedByCustomXml;

public interface CTPerm
extends XmlObject {
    public static final DocumentFactory<CTPerm> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctperm7878type");
    public static final SchemaType type = Factory.getType();

    public String getId();

    public STString xgetId();

    public void setId(String var1);

    public void xsetId(STString var1);

    public STDisplacedByCustomXml.Enum getDisplacedByCustomXml();

    public STDisplacedByCustomXml xgetDisplacedByCustomXml();

    public boolean isSetDisplacedByCustomXml();

    public void setDisplacedByCustomXml(STDisplacedByCustomXml.Enum var1);

    public void xsetDisplacedByCustomXml(STDisplacedByCustomXml var1);

    public void unsetDisplacedByCustomXml();
}

