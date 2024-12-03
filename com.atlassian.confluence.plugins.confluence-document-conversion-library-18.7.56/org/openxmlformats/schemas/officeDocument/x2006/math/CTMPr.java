/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTOnOff
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTSpacingRule
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTUnSignedInteger
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTYAlign
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTCtrlPr;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMCS;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOnOff;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSpacingRule;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTUnSignedInteger;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTYAlign;

public interface CTMPr
extends XmlObject {
    public static final DocumentFactory<CTMPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmpr122dtype");
    public static final SchemaType type = Factory.getType();

    public CTYAlign getBaseJc();

    public boolean isSetBaseJc();

    public void setBaseJc(CTYAlign var1);

    public CTYAlign addNewBaseJc();

    public void unsetBaseJc();

    public CTOnOff getPlcHide();

    public boolean isSetPlcHide();

    public void setPlcHide(CTOnOff var1);

    public CTOnOff addNewPlcHide();

    public void unsetPlcHide();

    public CTSpacingRule getRSpRule();

    public boolean isSetRSpRule();

    public void setRSpRule(CTSpacingRule var1);

    public CTSpacingRule addNewRSpRule();

    public void unsetRSpRule();

    public CTSpacingRule getCGpRule();

    public boolean isSetCGpRule();

    public void setCGpRule(CTSpacingRule var1);

    public CTSpacingRule addNewCGpRule();

    public void unsetCGpRule();

    public CTUnSignedInteger getRSp();

    public boolean isSetRSp();

    public void setRSp(CTUnSignedInteger var1);

    public CTUnSignedInteger addNewRSp();

    public void unsetRSp();

    public CTUnSignedInteger getCSp();

    public boolean isSetCSp();

    public void setCSp(CTUnSignedInteger var1);

    public CTUnSignedInteger addNewCSp();

    public void unsetCSp();

    public CTUnSignedInteger getCGp();

    public boolean isSetCGp();

    public void setCGp(CTUnSignedInteger var1);

    public CTUnSignedInteger addNewCGp();

    public void unsetCGp();

    public CTMCS getMcs();

    public boolean isSetMcs();

    public void setMcs(CTMCS var1);

    public CTMCS addNewMcs();

    public void unsetMcs();

    public CTCtrlPr getCtrlPr();

    public boolean isSetCtrlPr();

    public void setCtrlPr(CTCtrlPr var1);

    public CTCtrlPr addNewCtrlPr();

    public void unsetCtrlPr();
}

