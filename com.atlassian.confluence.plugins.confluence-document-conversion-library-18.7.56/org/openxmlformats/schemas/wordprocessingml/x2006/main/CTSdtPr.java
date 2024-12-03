/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnsignedDecimalNumber
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDataBinding;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPlaceholder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtComboBox;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtDate;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtDocPart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtDropDownList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnsignedDecimalNumber;

public interface CTSdtPr
extends XmlObject {
    public static final DocumentFactory<CTSdtPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsdtpre24dtype");
    public static final SchemaType type = Factory.getType();

    public CTRPr getRPr();

    public boolean isSetRPr();

    public void setRPr(CTRPr var1);

    public CTRPr addNewRPr();

    public void unsetRPr();

    public CTString getAlias();

    public boolean isSetAlias();

    public void setAlias(CTString var1);

    public CTString addNewAlias();

    public void unsetAlias();

    public CTString getTag();

    public boolean isSetTag();

    public void setTag(CTString var1);

    public CTString addNewTag();

    public void unsetTag();

    public CTDecimalNumber getId();

    public boolean isSetId();

    public void setId(CTDecimalNumber var1);

    public CTDecimalNumber addNewId();

    public void unsetId();

    public CTLock getLock();

    public boolean isSetLock();

    public void setLock(CTLock var1);

    public CTLock addNewLock();

    public void unsetLock();

    public CTPlaceholder getPlaceholder();

    public boolean isSetPlaceholder();

    public void setPlaceholder(CTPlaceholder var1);

    public CTPlaceholder addNewPlaceholder();

    public void unsetPlaceholder();

    public CTOnOff getTemporary();

    public boolean isSetTemporary();

    public void setTemporary(CTOnOff var1);

    public CTOnOff addNewTemporary();

    public void unsetTemporary();

    public CTOnOff getShowingPlcHdr();

    public boolean isSetShowingPlcHdr();

    public void setShowingPlcHdr(CTOnOff var1);

    public CTOnOff addNewShowingPlcHdr();

    public void unsetShowingPlcHdr();

    public CTDataBinding getDataBinding();

    public boolean isSetDataBinding();

    public void setDataBinding(CTDataBinding var1);

    public CTDataBinding addNewDataBinding();

    public void unsetDataBinding();

    public CTDecimalNumber getLabel();

    public boolean isSetLabel();

    public void setLabel(CTDecimalNumber var1);

    public CTDecimalNumber addNewLabel();

    public void unsetLabel();

    public CTUnsignedDecimalNumber getTabIndex();

    public boolean isSetTabIndex();

    public void setTabIndex(CTUnsignedDecimalNumber var1);

    public CTUnsignedDecimalNumber addNewTabIndex();

    public void unsetTabIndex();

    public CTEmpty getEquation();

    public boolean isSetEquation();

    public void setEquation(CTEmpty var1);

    public CTEmpty addNewEquation();

    public void unsetEquation();

    public CTSdtComboBox getComboBox();

    public boolean isSetComboBox();

    public void setComboBox(CTSdtComboBox var1);

    public CTSdtComboBox addNewComboBox();

    public void unsetComboBox();

    public CTSdtDate getDate();

    public boolean isSetDate();

    public void setDate(CTSdtDate var1);

    public CTSdtDate addNewDate();

    public void unsetDate();

    public CTSdtDocPart getDocPartObj();

    public boolean isSetDocPartObj();

    public void setDocPartObj(CTSdtDocPart var1);

    public CTSdtDocPart addNewDocPartObj();

    public void unsetDocPartObj();

    public CTSdtDocPart getDocPartList();

    public boolean isSetDocPartList();

    public void setDocPartList(CTSdtDocPart var1);

    public CTSdtDocPart addNewDocPartList();

    public void unsetDocPartList();

    public CTSdtDropDownList getDropDownList();

    public boolean isSetDropDownList();

    public void setDropDownList(CTSdtDropDownList var1);

    public CTSdtDropDownList addNewDropDownList();

    public void unsetDropDownList();

    public CTEmpty getPicture();

    public boolean isSetPicture();

    public void setPicture(CTEmpty var1);

    public CTEmpty addNewPicture();

    public void unsetPicture();

    public CTEmpty getRichText();

    public boolean isSetRichText();

    public void setRichText(CTEmpty var1);

    public CTEmpty addNewRichText();

    public void unsetRichText();

    public CTSdtText getText();

    public boolean isSetText();

    public void setText(CTSdtText var1);

    public CTSdtText addNewText();

    public void unsetText();

    public CTEmpty getCitation();

    public boolean isSetCitation();

    public void setCitation(CTEmpty var1);

    public CTEmpty addNewCitation();

    public void unsetCitation();

    public CTEmpty getGroup();

    public boolean isSetGroup();

    public void setGroup(CTEmpty var1);

    public CTEmpty addNewGroup();

    public void unsetGroup();

    public CTEmpty getBibliography();

    public boolean isSetBibliography();

    public void setBibliography(CTEmpty var1);

    public CTEmpty addNewBibliography();

    public void unsetBibliography();
}

