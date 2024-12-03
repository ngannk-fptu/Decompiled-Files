/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTSlideLayoutIdList
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTransition
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideLayoutIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterTextStyles;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTiming;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTransition;

public interface CTSlideMaster
extends XmlObject {
    public static final DocumentFactory<CTSlideMaster> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctslidemasterd8fctype");
    public static final SchemaType type = Factory.getType();

    public CTCommonSlideData getCSld();

    public void setCSld(CTCommonSlideData var1);

    public CTCommonSlideData addNewCSld();

    public CTColorMapping getClrMap();

    public void setClrMap(CTColorMapping var1);

    public CTColorMapping addNewClrMap();

    public CTSlideLayoutIdList getSldLayoutIdLst();

    public boolean isSetSldLayoutIdLst();

    public void setSldLayoutIdLst(CTSlideLayoutIdList var1);

    public CTSlideLayoutIdList addNewSldLayoutIdLst();

    public void unsetSldLayoutIdLst();

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

    public CTSlideMasterTextStyles getTxStyles();

    public boolean isSetTxStyles();

    public void setTxStyles(CTSlideMasterTextStyles var1);

    public CTSlideMasterTextStyles addNewTxStyles();

    public void unsetTxStyles();

    public CTExtensionListModify getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionListModify var1);

    public CTExtensionListModify addNewExtLst();

    public void unsetExtLst();

    public boolean getPreserve();

    public XmlBoolean xgetPreserve();

    public boolean isSetPreserve();

    public void setPreserve(boolean var1);

    public void xsetPreserve(XmlBoolean var1);

    public void unsetPreserve();
}

