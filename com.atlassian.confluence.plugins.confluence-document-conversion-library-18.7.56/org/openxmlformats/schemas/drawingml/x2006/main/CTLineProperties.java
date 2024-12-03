/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStopList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineEndProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineJoinBevel;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineJoinMiterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineJoinRound;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetLineDashProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STCompoundLine;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineCap;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineWidth;
import org.openxmlformats.schemas.drawingml.x2006.main.STPenAlignment;

public interface CTLineProperties
extends XmlObject {
    public static final DocumentFactory<CTLineProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlinepropertiesd5e2type");
    public static final SchemaType type = Factory.getType();

    public CTNoFillProperties getNoFill();

    public boolean isSetNoFill();

    public void setNoFill(CTNoFillProperties var1);

    public CTNoFillProperties addNewNoFill();

    public void unsetNoFill();

    public CTSolidColorFillProperties getSolidFill();

    public boolean isSetSolidFill();

    public void setSolidFill(CTSolidColorFillProperties var1);

    public CTSolidColorFillProperties addNewSolidFill();

    public void unsetSolidFill();

    public CTGradientFillProperties getGradFill();

    public boolean isSetGradFill();

    public void setGradFill(CTGradientFillProperties var1);

    public CTGradientFillProperties addNewGradFill();

    public void unsetGradFill();

    public CTPatternFillProperties getPattFill();

    public boolean isSetPattFill();

    public void setPattFill(CTPatternFillProperties var1);

    public CTPatternFillProperties addNewPattFill();

    public void unsetPattFill();

    public CTPresetLineDashProperties getPrstDash();

    public boolean isSetPrstDash();

    public void setPrstDash(CTPresetLineDashProperties var1);

    public CTPresetLineDashProperties addNewPrstDash();

    public void unsetPrstDash();

    public CTDashStopList getCustDash();

    public boolean isSetCustDash();

    public void setCustDash(CTDashStopList var1);

    public CTDashStopList addNewCustDash();

    public void unsetCustDash();

    public CTLineJoinRound getRound();

    public boolean isSetRound();

    public void setRound(CTLineJoinRound var1);

    public CTLineJoinRound addNewRound();

    public void unsetRound();

    public CTLineJoinBevel getBevel();

    public boolean isSetBevel();

    public void setBevel(CTLineJoinBevel var1);

    public CTLineJoinBevel addNewBevel();

    public void unsetBevel();

    public CTLineJoinMiterProperties getMiter();

    public boolean isSetMiter();

    public void setMiter(CTLineJoinMiterProperties var1);

    public CTLineJoinMiterProperties addNewMiter();

    public void unsetMiter();

    public CTLineEndProperties getHeadEnd();

    public boolean isSetHeadEnd();

    public void setHeadEnd(CTLineEndProperties var1);

    public CTLineEndProperties addNewHeadEnd();

    public void unsetHeadEnd();

    public CTLineEndProperties getTailEnd();

    public boolean isSetTailEnd();

    public void setTailEnd(CTLineEndProperties var1);

    public CTLineEndProperties addNewTailEnd();

    public void unsetTailEnd();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public int getW();

    public STLineWidth xgetW();

    public boolean isSetW();

    public void setW(int var1);

    public void xsetW(STLineWidth var1);

    public void unsetW();

    public STLineCap.Enum getCap();

    public STLineCap xgetCap();

    public boolean isSetCap();

    public void setCap(STLineCap.Enum var1);

    public void xsetCap(STLineCap var1);

    public void unsetCap();

    public STCompoundLine.Enum getCmpd();

    public STCompoundLine xgetCmpd();

    public boolean isSetCmpd();

    public void setCmpd(STCompoundLine.Enum var1);

    public void xsetCmpd(STCompoundLine var1);

    public void unsetCmpd();

    public STPenAlignment.Enum getAlgn();

    public STPenAlignment xgetAlgn();

    public boolean isSetAlgn();

    public void setAlgn(STPenAlignment.Enum var1);

    public void xsetAlgn(STPenAlignment var1);

    public void unsetAlgn();
}

