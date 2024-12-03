/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;

public interface CTHyperlink
extends XmlObject {
    public static final DocumentFactory<CTHyperlink> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cthyperlink4457type");
    public static final SchemaType type = Factory.getType();

    public CTEmbeddedWAVAudioFile getSnd();

    public boolean isSetSnd();

    public void setSnd(CTEmbeddedWAVAudioFile var1);

    public CTEmbeddedWAVAudioFile addNewSnd();

    public void unsetSnd();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getId();

    public STRelationshipId xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);

    public void unsetId();

    public String getInvalidUrl();

    public XmlString xgetInvalidUrl();

    public boolean isSetInvalidUrl();

    public void setInvalidUrl(String var1);

    public void xsetInvalidUrl(XmlString var1);

    public void unsetInvalidUrl();

    public String getAction();

    public XmlString xgetAction();

    public boolean isSetAction();

    public void setAction(String var1);

    public void xsetAction(XmlString var1);

    public void unsetAction();

    public String getTgtFrame();

    public XmlString xgetTgtFrame();

    public boolean isSetTgtFrame();

    public void setTgtFrame(String var1);

    public void xsetTgtFrame(XmlString var1);

    public void unsetTgtFrame();

    public String getTooltip();

    public XmlString xgetTooltip();

    public boolean isSetTooltip();

    public void setTooltip(String var1);

    public void xsetTooltip(XmlString var1);

    public void unsetTooltip();

    public boolean getHistory();

    public XmlBoolean xgetHistory();

    public boolean isSetHistory();

    public void setHistory(boolean var1);

    public void xsetHistory(XmlBoolean var1);

    public void unsetHistory();

    public boolean getHighlightClick();

    public XmlBoolean xgetHighlightClick();

    public boolean isSetHighlightClick();

    public void setHighlightClick(boolean var1);

    public void xsetHighlightClick(XmlBoolean var1);

    public void unsetHighlightClick();

    public boolean getEndSnd();

    public XmlBoolean xgetEndSnd();

    public boolean isSetEndSnd();

    public void setEndSnd(boolean var1);

    public void xsetEndSnd(XmlBoolean var1);

    public void unsetEndSnd();
}

