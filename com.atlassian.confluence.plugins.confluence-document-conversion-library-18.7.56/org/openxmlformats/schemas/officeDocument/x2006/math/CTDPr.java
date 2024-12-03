/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTChar
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTOnOff
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTShp
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTChar;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTCtrlPr;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOnOff;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTShp;

public interface CTDPr
extends XmlObject {
    public static final DocumentFactory<CTDPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdpr2596type");
    public static final SchemaType type = Factory.getType();

    public CTChar getBegChr();

    public boolean isSetBegChr();

    public void setBegChr(CTChar var1);

    public CTChar addNewBegChr();

    public void unsetBegChr();

    public CTChar getSepChr();

    public boolean isSetSepChr();

    public void setSepChr(CTChar var1);

    public CTChar addNewSepChr();

    public void unsetSepChr();

    public CTChar getEndChr();

    public boolean isSetEndChr();

    public void setEndChr(CTChar var1);

    public CTChar addNewEndChr();

    public void unsetEndChr();

    public CTOnOff getGrow();

    public boolean isSetGrow();

    public void setGrow(CTOnOff var1);

    public CTOnOff addNewGrow();

    public void unsetGrow();

    public CTShp getShp();

    public boolean isSetShp();

    public void setShp(CTShp var1);

    public CTShp addNewShp();

    public void unsetShp();

    public CTCtrlPr getCtrlPr();

    public boolean isSetCtrlPr();

    public void setCtrlPr(CTCtrlPr var1);

    public CTCtrlPr addNewCtrlPr();

    public void unsetCtrlPr();
}

