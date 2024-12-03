/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTwipsMeasure;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXAlign;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STYAlign;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVAnchor;

public interface CTTblPPr
extends XmlObject {
    public static final DocumentFactory<CTTblPPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttblppra134type");
    public static final SchemaType type = Factory.getType();

    public Object getLeftFromText();

    public STTwipsMeasure xgetLeftFromText();

    public boolean isSetLeftFromText();

    public void setLeftFromText(Object var1);

    public void xsetLeftFromText(STTwipsMeasure var1);

    public void unsetLeftFromText();

    public Object getRightFromText();

    public STTwipsMeasure xgetRightFromText();

    public boolean isSetRightFromText();

    public void setRightFromText(Object var1);

    public void xsetRightFromText(STTwipsMeasure var1);

    public void unsetRightFromText();

    public Object getTopFromText();

    public STTwipsMeasure xgetTopFromText();

    public boolean isSetTopFromText();

    public void setTopFromText(Object var1);

    public void xsetTopFromText(STTwipsMeasure var1);

    public void unsetTopFromText();

    public Object getBottomFromText();

    public STTwipsMeasure xgetBottomFromText();

    public boolean isSetBottomFromText();

    public void setBottomFromText(Object var1);

    public void xsetBottomFromText(STTwipsMeasure var1);

    public void unsetBottomFromText();

    public STVAnchor.Enum getVertAnchor();

    public STVAnchor xgetVertAnchor();

    public boolean isSetVertAnchor();

    public void setVertAnchor(STVAnchor.Enum var1);

    public void xsetVertAnchor(STVAnchor var1);

    public void unsetVertAnchor();

    public STHAnchor.Enum getHorzAnchor();

    public STHAnchor xgetHorzAnchor();

    public boolean isSetHorzAnchor();

    public void setHorzAnchor(STHAnchor.Enum var1);

    public void xsetHorzAnchor(STHAnchor var1);

    public void unsetHorzAnchor();

    public STXAlign.Enum getTblpXSpec();

    public STXAlign xgetTblpXSpec();

    public boolean isSetTblpXSpec();

    public void setTblpXSpec(STXAlign.Enum var1);

    public void xsetTblpXSpec(STXAlign var1);

    public void unsetTblpXSpec();

    public Object getTblpX();

    public STSignedTwipsMeasure xgetTblpX();

    public boolean isSetTblpX();

    public void setTblpX(Object var1);

    public void xsetTblpX(STSignedTwipsMeasure var1);

    public void unsetTblpX();

    public STYAlign.Enum getTblpYSpec();

    public STYAlign xgetTblpYSpec();

    public boolean isSetTblpYSpec();

    public void setTblpYSpec(STYAlign.Enum var1);

    public void xsetTblpYSpec(STYAlign var1);

    public void unsetTblpYSpec();

    public Object getTblpY();

    public STSignedTwipsMeasure xgetTblpY();

    public boolean isSetTblpY();

    public void setTblpY(Object var1);

    public void xsetTblpY(STSignedTwipsMeasure var1);

    public void unsetTblpY();
}

