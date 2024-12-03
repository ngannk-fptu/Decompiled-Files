/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STHorizontalAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTextRotation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVerticalAlignment;

public interface CTCellAlignment
extends XmlObject {
    public static final DocumentFactory<CTCellAlignment> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcellalignmentb580type");
    public static final SchemaType type = Factory.getType();

    public STHorizontalAlignment.Enum getHorizontal();

    public STHorizontalAlignment xgetHorizontal();

    public boolean isSetHorizontal();

    public void setHorizontal(STHorizontalAlignment.Enum var1);

    public void xsetHorizontal(STHorizontalAlignment var1);

    public void unsetHorizontal();

    public STVerticalAlignment.Enum getVertical();

    public STVerticalAlignment xgetVertical();

    public boolean isSetVertical();

    public void setVertical(STVerticalAlignment.Enum var1);

    public void xsetVertical(STVerticalAlignment var1);

    public void unsetVertical();

    public BigInteger getTextRotation();

    public STTextRotation xgetTextRotation();

    public boolean isSetTextRotation();

    public void setTextRotation(BigInteger var1);

    public void xsetTextRotation(STTextRotation var1);

    public void unsetTextRotation();

    public boolean getWrapText();

    public XmlBoolean xgetWrapText();

    public boolean isSetWrapText();

    public void setWrapText(boolean var1);

    public void xsetWrapText(XmlBoolean var1);

    public void unsetWrapText();

    public long getIndent();

    public XmlUnsignedInt xgetIndent();

    public boolean isSetIndent();

    public void setIndent(long var1);

    public void xsetIndent(XmlUnsignedInt var1);

    public void unsetIndent();

    public int getRelativeIndent();

    public XmlInt xgetRelativeIndent();

    public boolean isSetRelativeIndent();

    public void setRelativeIndent(int var1);

    public void xsetRelativeIndent(XmlInt var1);

    public void unsetRelativeIndent();

    public boolean getJustifyLastLine();

    public XmlBoolean xgetJustifyLastLine();

    public boolean isSetJustifyLastLine();

    public void setJustifyLastLine(boolean var1);

    public void xsetJustifyLastLine(XmlBoolean var1);

    public void unsetJustifyLastLine();

    public boolean getShrinkToFit();

    public XmlBoolean xgetShrinkToFit();

    public boolean isSetShrinkToFit();

    public void setShrinkToFit(boolean var1);

    public void xsetShrinkToFit(XmlBoolean var1);

    public void unsetShrinkToFit();

    public long getReadingOrder();

    public XmlUnsignedInt xgetReadingOrder();

    public boolean isSetReadingOrder();

    public void setReadingOrder(long var1);

    public void xsetReadingOrder(XmlUnsignedInt var1);

    public void unsetReadingOrder();
}

