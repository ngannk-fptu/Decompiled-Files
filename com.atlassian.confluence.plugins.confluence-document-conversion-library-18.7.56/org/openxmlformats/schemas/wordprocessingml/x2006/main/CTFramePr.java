/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STDropCap
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTwipsMeasure;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXAlign;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STYAlign;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDropCap;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STWrap;

public interface CTFramePr
extends XmlObject {
    public static final DocumentFactory<CTFramePr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctframepr12a3type");
    public static final SchemaType type = Factory.getType();

    public STDropCap.Enum getDropCap();

    public STDropCap xgetDropCap();

    public boolean isSetDropCap();

    public void setDropCap(STDropCap.Enum var1);

    public void xsetDropCap(STDropCap var1);

    public void unsetDropCap();

    public BigInteger getLines();

    public STDecimalNumber xgetLines();

    public boolean isSetLines();

    public void setLines(BigInteger var1);

    public void xsetLines(STDecimalNumber var1);

    public void unsetLines();

    public Object getW();

    public STTwipsMeasure xgetW();

    public boolean isSetW();

    public void setW(Object var1);

    public void xsetW(STTwipsMeasure var1);

    public void unsetW();

    public Object getH();

    public STTwipsMeasure xgetH();

    public boolean isSetH();

    public void setH(Object var1);

    public void xsetH(STTwipsMeasure var1);

    public void unsetH();

    public Object getVSpace();

    public STTwipsMeasure xgetVSpace();

    public boolean isSetVSpace();

    public void setVSpace(Object var1);

    public void xsetVSpace(STTwipsMeasure var1);

    public void unsetVSpace();

    public Object getHSpace();

    public STTwipsMeasure xgetHSpace();

    public boolean isSetHSpace();

    public void setHSpace(Object var1);

    public void xsetHSpace(STTwipsMeasure var1);

    public void unsetHSpace();

    public STWrap.Enum getWrap();

    public STWrap xgetWrap();

    public boolean isSetWrap();

    public void setWrap(STWrap.Enum var1);

    public void xsetWrap(STWrap var1);

    public void unsetWrap();

    public STHAnchor.Enum getHAnchor();

    public STHAnchor xgetHAnchor();

    public boolean isSetHAnchor();

    public void setHAnchor(STHAnchor.Enum var1);

    public void xsetHAnchor(STHAnchor var1);

    public void unsetHAnchor();

    public STVAnchor.Enum getVAnchor();

    public STVAnchor xgetVAnchor();

    public boolean isSetVAnchor();

    public void setVAnchor(STVAnchor.Enum var1);

    public void xsetVAnchor(STVAnchor var1);

    public void unsetVAnchor();

    public Object getX();

    public STSignedTwipsMeasure xgetX();

    public boolean isSetX();

    public void setX(Object var1);

    public void xsetX(STSignedTwipsMeasure var1);

    public void unsetX();

    public STXAlign.Enum getXAlign();

    public STXAlign xgetXAlign();

    public boolean isSetXAlign();

    public void setXAlign(STXAlign.Enum var1);

    public void xsetXAlign(STXAlign var1);

    public void unsetXAlign();

    public Object getY();

    public STSignedTwipsMeasure xgetY();

    public boolean isSetY();

    public void setY(Object var1);

    public void xsetY(STSignedTwipsMeasure var1);

    public void unsetY();

    public STYAlign.Enum getYAlign();

    public STYAlign xgetYAlign();

    public boolean isSetYAlign();

    public void setYAlign(STYAlign.Enum var1);

    public void xsetYAlign(STYAlign var1);

    public void unsetYAlign();

    public STHeightRule.Enum getHRule();

    public STHeightRule xgetHRule();

    public boolean isSetHRule();

    public void setHRule(STHeightRule.Enum var1);

    public void xsetHRule(STHeightRule var1);

    public void unsetHRule();

    public Object getAnchorLock();

    public STOnOff xgetAnchorLock();

    public boolean isSetAnchorLock();

    public void setAnchorLock(Object var1);

    public void xsetAnchorLock(STOnOff var1);

    public void unsetAnchorLock();
}

