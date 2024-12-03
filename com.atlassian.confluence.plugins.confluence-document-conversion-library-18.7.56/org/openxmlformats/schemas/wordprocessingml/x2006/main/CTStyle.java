/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLongHexNumber
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblStylePr
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLongHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblStylePr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;

public interface CTStyle
extends XmlObject {
    public static final DocumentFactory<CTStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctstyle41c1type");
    public static final SchemaType type = Factory.getType();

    public CTString getName();

    public boolean isSetName();

    public void setName(CTString var1);

    public CTString addNewName();

    public void unsetName();

    public CTString getAliases();

    public boolean isSetAliases();

    public void setAliases(CTString var1);

    public CTString addNewAliases();

    public void unsetAliases();

    public CTString getBasedOn();

    public boolean isSetBasedOn();

    public void setBasedOn(CTString var1);

    public CTString addNewBasedOn();

    public void unsetBasedOn();

    public CTString getNext();

    public boolean isSetNext();

    public void setNext(CTString var1);

    public CTString addNewNext();

    public void unsetNext();

    public CTString getLink();

    public boolean isSetLink();

    public void setLink(CTString var1);

    public CTString addNewLink();

    public void unsetLink();

    public CTOnOff getAutoRedefine();

    public boolean isSetAutoRedefine();

    public void setAutoRedefine(CTOnOff var1);

    public CTOnOff addNewAutoRedefine();

    public void unsetAutoRedefine();

    public CTOnOff getHidden();

    public boolean isSetHidden();

    public void setHidden(CTOnOff var1);

    public CTOnOff addNewHidden();

    public void unsetHidden();

    public CTDecimalNumber getUiPriority();

    public boolean isSetUiPriority();

    public void setUiPriority(CTDecimalNumber var1);

    public CTDecimalNumber addNewUiPriority();

    public void unsetUiPriority();

    public CTOnOff getSemiHidden();

    public boolean isSetSemiHidden();

    public void setSemiHidden(CTOnOff var1);

    public CTOnOff addNewSemiHidden();

    public void unsetSemiHidden();

    public CTOnOff getUnhideWhenUsed();

    public boolean isSetUnhideWhenUsed();

    public void setUnhideWhenUsed(CTOnOff var1);

    public CTOnOff addNewUnhideWhenUsed();

    public void unsetUnhideWhenUsed();

    public CTOnOff getQFormat();

    public boolean isSetQFormat();

    public void setQFormat(CTOnOff var1);

    public CTOnOff addNewQFormat();

    public void unsetQFormat();

    public CTOnOff getLocked();

    public boolean isSetLocked();

    public void setLocked(CTOnOff var1);

    public CTOnOff addNewLocked();

    public void unsetLocked();

    public CTOnOff getPersonal();

    public boolean isSetPersonal();

    public void setPersonal(CTOnOff var1);

    public CTOnOff addNewPersonal();

    public void unsetPersonal();

    public CTOnOff getPersonalCompose();

    public boolean isSetPersonalCompose();

    public void setPersonalCompose(CTOnOff var1);

    public CTOnOff addNewPersonalCompose();

    public void unsetPersonalCompose();

    public CTOnOff getPersonalReply();

    public boolean isSetPersonalReply();

    public void setPersonalReply(CTOnOff var1);

    public CTOnOff addNewPersonalReply();

    public void unsetPersonalReply();

    public CTLongHexNumber getRsid();

    public boolean isSetRsid();

    public void setRsid(CTLongHexNumber var1);

    public CTLongHexNumber addNewRsid();

    public void unsetRsid();

    public CTPPrGeneral getPPr();

    public boolean isSetPPr();

    public void setPPr(CTPPrGeneral var1);

    public CTPPrGeneral addNewPPr();

    public void unsetPPr();

    public CTRPr getRPr();

    public boolean isSetRPr();

    public void setRPr(CTRPr var1);

    public CTRPr addNewRPr();

    public void unsetRPr();

    public CTTblPrBase getTblPr();

    public boolean isSetTblPr();

    public void setTblPr(CTTblPrBase var1);

    public CTTblPrBase addNewTblPr();

    public void unsetTblPr();

    public CTTrPr getTrPr();

    public boolean isSetTrPr();

    public void setTrPr(CTTrPr var1);

    public CTTrPr addNewTrPr();

    public void unsetTrPr();

    public CTTcPr getTcPr();

    public boolean isSetTcPr();

    public void setTcPr(CTTcPr var1);

    public CTTcPr addNewTcPr();

    public void unsetTcPr();

    public List<CTTblStylePr> getTblStylePrList();

    public CTTblStylePr[] getTblStylePrArray();

    public CTTblStylePr getTblStylePrArray(int var1);

    public int sizeOfTblStylePrArray();

    public void setTblStylePrArray(CTTblStylePr[] var1);

    public void setTblStylePrArray(int var1, CTTblStylePr var2);

    public CTTblStylePr insertNewTblStylePr(int var1);

    public CTTblStylePr addNewTblStylePr();

    public void removeTblStylePr(int var1);

    public STStyleType.Enum getType();

    public STStyleType xgetType();

    public boolean isSetType();

    public void setType(STStyleType.Enum var1);

    public void xsetType(STStyleType var1);

    public void unsetType();

    public String getStyleId();

    public STString xgetStyleId();

    public boolean isSetStyleId();

    public void setStyleId(String var1);

    public void xsetStyleId(STString var1);

    public void unsetStyleId();

    public Object getDefault();

    public STOnOff xgetDefault();

    public boolean isSetDefault();

    public void setDefault(Object var1);

    public void xsetDefault(STOnOff var1);

    public void unsetDefault();

    public Object getCustomStyle();

    public STOnOff xgetCustomStyle();

    public boolean isSetCustomStyle();

    public void setCustomStyle(Object var1);

    public void xsetCustomStyle(STOnOff var1);

    public void unsetCustomStyle();
}

