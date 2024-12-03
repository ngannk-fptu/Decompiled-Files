/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STConformanceClass
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTCustomShowList
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTKinsoku
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTModifyVerifier
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTPhotoAlbum
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTSmartTags
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STConformanceClass;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomShowList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerDataList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHandoutMasterIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTKinsoku;
import org.openxmlformats.schemas.presentationml.x2006.main.CTModifyVerifier;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPhotoAlbum;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideSize;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSmartTags;
import org.openxmlformats.schemas.presentationml.x2006.main.STBookmarkIdSeed;

public interface CTPresentation
extends XmlObject {
    public static final DocumentFactory<CTPresentation> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpresentation56cbtype");
    public static final SchemaType type = Factory.getType();

    public CTSlideMasterIdList getSldMasterIdLst();

    public boolean isSetSldMasterIdLst();

    public void setSldMasterIdLst(CTSlideMasterIdList var1);

    public CTSlideMasterIdList addNewSldMasterIdLst();

    public void unsetSldMasterIdLst();

    public CTNotesMasterIdList getNotesMasterIdLst();

    public boolean isSetNotesMasterIdLst();

    public void setNotesMasterIdLst(CTNotesMasterIdList var1);

    public CTNotesMasterIdList addNewNotesMasterIdLst();

    public void unsetNotesMasterIdLst();

    public CTHandoutMasterIdList getHandoutMasterIdLst();

    public boolean isSetHandoutMasterIdLst();

    public void setHandoutMasterIdLst(CTHandoutMasterIdList var1);

    public CTHandoutMasterIdList addNewHandoutMasterIdLst();

    public void unsetHandoutMasterIdLst();

    public CTSlideIdList getSldIdLst();

    public boolean isSetSldIdLst();

    public void setSldIdLst(CTSlideIdList var1);

    public CTSlideIdList addNewSldIdLst();

    public void unsetSldIdLst();

    public CTSlideSize getSldSz();

    public boolean isSetSldSz();

    public void setSldSz(CTSlideSize var1);

    public CTSlideSize addNewSldSz();

    public void unsetSldSz();

    public CTPositiveSize2D getNotesSz();

    public void setNotesSz(CTPositiveSize2D var1);

    public CTPositiveSize2D addNewNotesSz();

    public CTSmartTags getSmartTags();

    public boolean isSetSmartTags();

    public void setSmartTags(CTSmartTags var1);

    public CTSmartTags addNewSmartTags();

    public void unsetSmartTags();

    public CTEmbeddedFontList getEmbeddedFontLst();

    public boolean isSetEmbeddedFontLst();

    public void setEmbeddedFontLst(CTEmbeddedFontList var1);

    public CTEmbeddedFontList addNewEmbeddedFontLst();

    public void unsetEmbeddedFontLst();

    public CTCustomShowList getCustShowLst();

    public boolean isSetCustShowLst();

    public void setCustShowLst(CTCustomShowList var1);

    public CTCustomShowList addNewCustShowLst();

    public void unsetCustShowLst();

    public CTPhotoAlbum getPhotoAlbum();

    public boolean isSetPhotoAlbum();

    public void setPhotoAlbum(CTPhotoAlbum var1);

    public CTPhotoAlbum addNewPhotoAlbum();

    public void unsetPhotoAlbum();

    public CTCustomerDataList getCustDataLst();

    public boolean isSetCustDataLst();

    public void setCustDataLst(CTCustomerDataList var1);

    public CTCustomerDataList addNewCustDataLst();

    public void unsetCustDataLst();

    public CTKinsoku getKinsoku();

    public boolean isSetKinsoku();

    public void setKinsoku(CTKinsoku var1);

    public CTKinsoku addNewKinsoku();

    public void unsetKinsoku();

    public CTTextListStyle getDefaultTextStyle();

    public boolean isSetDefaultTextStyle();

    public void setDefaultTextStyle(CTTextListStyle var1);

    public CTTextListStyle addNewDefaultTextStyle();

    public void unsetDefaultTextStyle();

    public CTModifyVerifier getModifyVerifier();

    public boolean isSetModifyVerifier();

    public void setModifyVerifier(CTModifyVerifier var1);

    public CTModifyVerifier addNewModifyVerifier();

    public void unsetModifyVerifier();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public Object getServerZoom();

    public STPercentage xgetServerZoom();

    public boolean isSetServerZoom();

    public void setServerZoom(Object var1);

    public void xsetServerZoom(STPercentage var1);

    public void unsetServerZoom();

    public int getFirstSlideNum();

    public XmlInt xgetFirstSlideNum();

    public boolean isSetFirstSlideNum();

    public void setFirstSlideNum(int var1);

    public void xsetFirstSlideNum(XmlInt var1);

    public void unsetFirstSlideNum();

    public boolean getShowSpecialPlsOnTitleSld();

    public XmlBoolean xgetShowSpecialPlsOnTitleSld();

    public boolean isSetShowSpecialPlsOnTitleSld();

    public void setShowSpecialPlsOnTitleSld(boolean var1);

    public void xsetShowSpecialPlsOnTitleSld(XmlBoolean var1);

    public void unsetShowSpecialPlsOnTitleSld();

    public boolean getRtl();

    public XmlBoolean xgetRtl();

    public boolean isSetRtl();

    public void setRtl(boolean var1);

    public void xsetRtl(XmlBoolean var1);

    public void unsetRtl();

    public boolean getRemovePersonalInfoOnSave();

    public XmlBoolean xgetRemovePersonalInfoOnSave();

    public boolean isSetRemovePersonalInfoOnSave();

    public void setRemovePersonalInfoOnSave(boolean var1);

    public void xsetRemovePersonalInfoOnSave(XmlBoolean var1);

    public void unsetRemovePersonalInfoOnSave();

    public boolean getCompatMode();

    public XmlBoolean xgetCompatMode();

    public boolean isSetCompatMode();

    public void setCompatMode(boolean var1);

    public void xsetCompatMode(XmlBoolean var1);

    public void unsetCompatMode();

    public boolean getStrictFirstAndLastChars();

    public XmlBoolean xgetStrictFirstAndLastChars();

    public boolean isSetStrictFirstAndLastChars();

    public void setStrictFirstAndLastChars(boolean var1);

    public void xsetStrictFirstAndLastChars(XmlBoolean var1);

    public void unsetStrictFirstAndLastChars();

    public boolean getEmbedTrueTypeFonts();

    public XmlBoolean xgetEmbedTrueTypeFonts();

    public boolean isSetEmbedTrueTypeFonts();

    public void setEmbedTrueTypeFonts(boolean var1);

    public void xsetEmbedTrueTypeFonts(XmlBoolean var1);

    public void unsetEmbedTrueTypeFonts();

    public boolean getSaveSubsetFonts();

    public XmlBoolean xgetSaveSubsetFonts();

    public boolean isSetSaveSubsetFonts();

    public void setSaveSubsetFonts(boolean var1);

    public void xsetSaveSubsetFonts(XmlBoolean var1);

    public void unsetSaveSubsetFonts();

    public boolean getAutoCompressPictures();

    public XmlBoolean xgetAutoCompressPictures();

    public boolean isSetAutoCompressPictures();

    public void setAutoCompressPictures(boolean var1);

    public void xsetAutoCompressPictures(XmlBoolean var1);

    public void unsetAutoCompressPictures();

    public long getBookmarkIdSeed();

    public STBookmarkIdSeed xgetBookmarkIdSeed();

    public boolean isSetBookmarkIdSeed();

    public void setBookmarkIdSeed(long var1);

    public void xsetBookmarkIdSeed(STBookmarkIdSeed var1);

    public void unsetBookmarkIdSeed();

    public STConformanceClass.Enum getConformance();

    public STConformanceClass xgetConformance();

    public boolean isSetConformance();

    public void setConformance(STConformanceClass.Enum var1);

    public void xsetConformance(STConformanceClass var1);

    public void unsetConformance();
}

