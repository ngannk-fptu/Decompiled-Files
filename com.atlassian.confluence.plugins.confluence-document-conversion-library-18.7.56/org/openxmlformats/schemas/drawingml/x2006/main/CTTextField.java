/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STGuid;

public interface CTTextField
extends XmlObject {
    public static final DocumentFactory<CTTextField> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextfield187etype");
    public static final SchemaType type = Factory.getType();

    public CTTextCharacterProperties getRPr();

    public boolean isSetRPr();

    public void setRPr(CTTextCharacterProperties var1);

    public CTTextCharacterProperties addNewRPr();

    public void unsetRPr();

    public CTTextParagraphProperties getPPr();

    public boolean isSetPPr();

    public void setPPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewPPr();

    public void unsetPPr();

    public String getT();

    public XmlString xgetT();

    public boolean isSetT();

    public void setT(String var1);

    public void xsetT(XmlString var1);

    public void unsetT();

    public String getId();

    public STGuid xgetId();

    public void setId(String var1);

    public void xsetId(STGuid var1);

    public String getType();

    public XmlString xgetType();

    public boolean isSetType();

    public void setType(String var1);

    public void xsetType(XmlString var1);

    public void unsetType();
}

