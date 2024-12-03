/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAudioCD
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAudioFile
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTQuickTimeFile
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAudioCD;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAudioFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTQuickTimeFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTVideoFile;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerDataList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;

public interface CTApplicationNonVisualDrawingProps
extends XmlObject {
    public static final DocumentFactory<CTApplicationNonVisualDrawingProps> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctapplicationnonvisualdrawingprops2fb6type");
    public static final SchemaType type = Factory.getType();

    public CTPlaceholder getPh();

    public boolean isSetPh();

    public void setPh(CTPlaceholder var1);

    public CTPlaceholder addNewPh();

    public void unsetPh();

    public CTAudioCD getAudioCd();

    public boolean isSetAudioCd();

    public void setAudioCd(CTAudioCD var1);

    public CTAudioCD addNewAudioCd();

    public void unsetAudioCd();

    public CTEmbeddedWAVAudioFile getWavAudioFile();

    public boolean isSetWavAudioFile();

    public void setWavAudioFile(CTEmbeddedWAVAudioFile var1);

    public CTEmbeddedWAVAudioFile addNewWavAudioFile();

    public void unsetWavAudioFile();

    public CTAudioFile getAudioFile();

    public boolean isSetAudioFile();

    public void setAudioFile(CTAudioFile var1);

    public CTAudioFile addNewAudioFile();

    public void unsetAudioFile();

    public CTVideoFile getVideoFile();

    public boolean isSetVideoFile();

    public void setVideoFile(CTVideoFile var1);

    public CTVideoFile addNewVideoFile();

    public void unsetVideoFile();

    public CTQuickTimeFile getQuickTimeFile();

    public boolean isSetQuickTimeFile();

    public void setQuickTimeFile(CTQuickTimeFile var1);

    public CTQuickTimeFile addNewQuickTimeFile();

    public void unsetQuickTimeFile();

    public CTCustomerDataList getCustDataLst();

    public boolean isSetCustDataLst();

    public void setCustDataLst(CTCustomerDataList var1);

    public CTCustomerDataList addNewCustDataLst();

    public void unsetCustDataLst();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public boolean getIsPhoto();

    public XmlBoolean xgetIsPhoto();

    public boolean isSetIsPhoto();

    public void setIsPhoto(boolean var1);

    public void xsetIsPhoto(XmlBoolean var1);

    public void unsetIsPhoto();

    public boolean getUserDrawn();

    public XmlBoolean xgetUserDrawn();

    public boolean isSetUserDrawn();

    public void setUserDrawn(boolean var1);

    public void xsetUserDrawn(XmlBoolean var1);

    public void unsetUserDrawn();
}

