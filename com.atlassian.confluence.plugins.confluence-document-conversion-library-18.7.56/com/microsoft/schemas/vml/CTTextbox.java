/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import com.microsoft.schemas.office.office.STInsetMode;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTxbxContent;

public interface CTTextbox
extends XmlObject {
    public static final DocumentFactory<CTTextbox> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextboxf712type");
    public static final SchemaType type = Factory.getType();

    public CTTxbxContent getTxbxContent();

    public boolean isSetTxbxContent();

    public void setTxbxContent(CTTxbxContent var1);

    public CTTxbxContent addNewTxbxContent();

    public void unsetTxbxContent();

    public String getId();

    public XmlString xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlString var1);

    public void unsetId();

    public String getStyle();

    public XmlString xgetStyle();

    public boolean isSetStyle();

    public void setStyle(String var1);

    public void xsetStyle(XmlString var1);

    public void unsetStyle();

    public String getInset();

    public XmlString xgetInset();

    public boolean isSetInset();

    public void setInset(String var1);

    public void xsetInset(XmlString var1);

    public void unsetInset();

    public STTrueFalse.Enum getSingleclick();

    public STTrueFalse xgetSingleclick();

    public boolean isSetSingleclick();

    public void setSingleclick(STTrueFalse.Enum var1);

    public void xsetSingleclick(STTrueFalse var1);

    public void unsetSingleclick();

    public STInsetMode.Enum getInsetmode();

    public STInsetMode xgetInsetmode();

    public boolean isSetInsetmode();

    public void setInsetmode(STInsetMode.Enum var1);

    public void xsetInsetmode(STInsetMode var1);

    public void unsetInsetmode();
}

