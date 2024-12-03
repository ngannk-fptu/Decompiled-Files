/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLevelSuffix
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvlLegacy
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLevelSuffix;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLevelText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvlLegacy;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumFmt;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLongHexNumber;

public interface CTLvl
extends XmlObject {
    public static final DocumentFactory<CTLvl> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlvlf630type");
    public static final SchemaType type = Factory.getType();

    public CTDecimalNumber getStart();

    public boolean isSetStart();

    public void setStart(CTDecimalNumber var1);

    public CTDecimalNumber addNewStart();

    public void unsetStart();

    public CTNumFmt getNumFmt();

    public boolean isSetNumFmt();

    public void setNumFmt(CTNumFmt var1);

    public CTNumFmt addNewNumFmt();

    public void unsetNumFmt();

    public CTDecimalNumber getLvlRestart();

    public boolean isSetLvlRestart();

    public void setLvlRestart(CTDecimalNumber var1);

    public CTDecimalNumber addNewLvlRestart();

    public void unsetLvlRestart();

    public CTString getPStyle();

    public boolean isSetPStyle();

    public void setPStyle(CTString var1);

    public CTString addNewPStyle();

    public void unsetPStyle();

    public CTOnOff getIsLgl();

    public boolean isSetIsLgl();

    public void setIsLgl(CTOnOff var1);

    public CTOnOff addNewIsLgl();

    public void unsetIsLgl();

    public CTLevelSuffix getSuff();

    public boolean isSetSuff();

    public void setSuff(CTLevelSuffix var1);

    public CTLevelSuffix addNewSuff();

    public void unsetSuff();

    public CTLevelText getLvlText();

    public boolean isSetLvlText();

    public void setLvlText(CTLevelText var1);

    public CTLevelText addNewLvlText();

    public void unsetLvlText();

    public CTDecimalNumber getLvlPicBulletId();

    public boolean isSetLvlPicBulletId();

    public void setLvlPicBulletId(CTDecimalNumber var1);

    public CTDecimalNumber addNewLvlPicBulletId();

    public void unsetLvlPicBulletId();

    public CTLvlLegacy getLegacy();

    public boolean isSetLegacy();

    public void setLegacy(CTLvlLegacy var1);

    public CTLvlLegacy addNewLegacy();

    public void unsetLegacy();

    public CTJc getLvlJc();

    public boolean isSetLvlJc();

    public void setLvlJc(CTJc var1);

    public CTJc addNewLvlJc();

    public void unsetLvlJc();

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

    public BigInteger getIlvl();

    public STDecimalNumber xgetIlvl();

    public void setIlvl(BigInteger var1);

    public void xsetIlvl(STDecimalNumber var1);

    public byte[] getTplc();

    public STLongHexNumber xgetTplc();

    public boolean isSetTplc();

    public void setTplc(byte[] var1);

    public void xsetTplc(STLongHexNumber var1);

    public void unsetTplc();

    public Object getTentative();

    public STOnOff xgetTentative();

    public boolean isSetTentative();

    public void setTentative(Object var1);

    public void xsetTentative(STOnOff var1);

    public void unsetTentative();
}

