/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public interface CTGraphicalObjectFrameLocking
extends XmlObject {
    public static final DocumentFactory<CTGraphicalObjectFrameLocking> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgraphicalobjectframelocking42adtype");
    public static final SchemaType type = Factory.getType();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public boolean getNoGrp();

    public XmlBoolean xgetNoGrp();

    public boolean isSetNoGrp();

    public void setNoGrp(boolean var1);

    public void xsetNoGrp(XmlBoolean var1);

    public void unsetNoGrp();

    public boolean getNoDrilldown();

    public XmlBoolean xgetNoDrilldown();

    public boolean isSetNoDrilldown();

    public void setNoDrilldown(boolean var1);

    public void xsetNoDrilldown(XmlBoolean var1);

    public void unsetNoDrilldown();

    public boolean getNoSelect();

    public XmlBoolean xgetNoSelect();

    public boolean isSetNoSelect();

    public void setNoSelect(boolean var1);

    public void xsetNoSelect(XmlBoolean var1);

    public void unsetNoSelect();

    public boolean getNoChangeAspect();

    public XmlBoolean xgetNoChangeAspect();

    public boolean isSetNoChangeAspect();

    public void setNoChangeAspect(boolean var1);

    public void xsetNoChangeAspect(XmlBoolean var1);

    public void unsetNoChangeAspect();

    public boolean getNoMove();

    public XmlBoolean xgetNoMove();

    public boolean isSetNoMove();

    public void setNoMove(boolean var1);

    public void xsetNoMove(XmlBoolean var1);

    public void unsetNoMove();

    public boolean getNoResize();

    public XmlBoolean xgetNoResize();

    public boolean isSetNoResize();

    public void setNoResize(boolean var1);

    public void xsetNoResize(XmlBoolean var1);

    public void unsetNoResize();
}

