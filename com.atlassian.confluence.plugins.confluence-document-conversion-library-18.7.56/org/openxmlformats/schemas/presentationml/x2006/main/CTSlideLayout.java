/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTransition
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMappingOverride;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTiming;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTransition;
import org.openxmlformats.schemas.presentationml.x2006.main.STSlideLayoutType;

public interface CTSlideLayout
extends XmlObject {
    public static final DocumentFactory<CTSlideLayout> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctslidelayouteb34type");
    public static final SchemaType type = Factory.getType();

    public CTCommonSlideData getCSld();

    public void setCSld(CTCommonSlideData var1);

    public CTCommonSlideData addNewCSld();

    public CTColorMappingOverride getClrMapOvr();

    public boolean isSetClrMapOvr();

    public void setClrMapOvr(CTColorMappingOverride var1);

    public CTColorMappingOverride addNewClrMapOvr();

    public void unsetClrMapOvr();

    public CTSlideTransition getTransition();

    public boolean isSetTransition();

    public void setTransition(CTSlideTransition var1);

    public CTSlideTransition addNewTransition();

    public void unsetTransition();

    public CTSlideTiming getTiming();

    public boolean isSetTiming();

    public void setTiming(CTSlideTiming var1);

    public CTSlideTiming addNewTiming();

    public void unsetTiming();

    public CTHeaderFooter getHf();

    public boolean isSetHf();

    public void setHf(CTHeaderFooter var1);

    public CTHeaderFooter addNewHf();

    public void unsetHf();

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

    public String getMatchingName();

    public XmlString xgetMatchingName();

    public boolean isSetMatchingName();

    public void setMatchingName(String var1);

    public void xsetMatchingName(XmlString var1);

    public void unsetMatchingName();

    public STSlideLayoutType.Enum getType();

    public STSlideLayoutType xgetType();

    public boolean isSetType();

    public void setType(STSlideLayoutType.Enum var1);

    public void xsetType(STSlideLayoutType var1);

    public void unsetType();

    public boolean getPreserve();

    public XmlBoolean xgetPreserve();

    public boolean isSetPreserve();

    public void setPreserve(boolean var1);

    public void xsetPreserve(XmlBoolean var1);

    public void unsetPreserve();

    public boolean getUserDrawn();

    public XmlBoolean xgetUserDrawn();

    public boolean isSetUserDrawn();

    public void setUserDrawn(boolean var1);

    public void xsetUserDrawn(XmlBoolean var1);

    public void unsetUserDrawn();
}

