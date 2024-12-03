/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.STDrawingElementId;

public interface CTNonVisualDrawingProps
extends XmlObject {
    public static final DocumentFactory<CTNonVisualDrawingProps> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnonvisualdrawingprops8fb0type");
    public static final SchemaType type = Factory.getType();

    public CTHyperlink getHlinkClick();

    public boolean isSetHlinkClick();

    public void setHlinkClick(CTHyperlink var1);

    public CTHyperlink addNewHlinkClick();

    public void unsetHlinkClick();

    public CTHyperlink getHlinkHover();

    public boolean isSetHlinkHover();

    public void setHlinkHover(CTHyperlink var1);

    public CTHyperlink addNewHlinkHover();

    public void unsetHlinkHover();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getId();

    public STDrawingElementId xgetId();

    public void setId(long var1);

    public void xsetId(STDrawingElementId var1);

    public String getName();

    public XmlString xgetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public String getDescr();

    public XmlString xgetDescr();

    public boolean isSetDescr();

    public void setDescr(String var1);

    public void xsetDescr(XmlString var1);

    public void unsetDescr();

    public boolean getHidden();

    public XmlBoolean xgetHidden();

    public boolean isSetHidden();

    public void setHidden(boolean var1);

    public void xsetHidden(XmlBoolean var1);

    public void unsetHidden();

    public String getTitle();

    public XmlString xgetTitle();

    public boolean isSetTitle();

    public void setTitle(String var1);

    public void xsetTitle(XmlString var1);

    public void unsetTitle();
}

