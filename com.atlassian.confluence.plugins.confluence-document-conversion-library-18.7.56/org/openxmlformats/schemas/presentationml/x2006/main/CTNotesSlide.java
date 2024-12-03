/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMappingOverride;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;

public interface CTNotesSlide
extends XmlObject {
    public static final DocumentFactory<CTNotesSlide> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnotesslideab75type");
    public static final SchemaType type = Factory.getType();

    public CTCommonSlideData getCSld();

    public void setCSld(CTCommonSlideData var1);

    public CTCommonSlideData addNewCSld();

    public CTColorMappingOverride getClrMapOvr();

    public boolean isSetClrMapOvr();

    public void setClrMapOvr(CTColorMappingOverride var1);

    public CTColorMappingOverride addNewClrMapOvr();

    public void unsetClrMapOvr();

    public CTExtensionListModify getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionListModify var1);

    public CTExtensionListModify addNewExtLst();

    public void unsetExtLst();

    public boolean getShowMasterSp();

    public XmlBoolean xgetShowMasterSp();

    public boolean isSetShowMasterSp();

    public void setShowMasterSp(boolean var1);

    public void xsetShowMasterSp(XmlBoolean var1);

    public void unsetShowMasterSp();

    public boolean getShowMasterPhAnim();

    public XmlBoolean xgetShowMasterPhAnim();

    public boolean isSetShowMasterPhAnim();

    public void setShowMasterPhAnim(boolean var1);

    public void xsetShowMasterPhAnim(XmlBoolean var1);

    public void unsetShowMasterPhAnim();
}

