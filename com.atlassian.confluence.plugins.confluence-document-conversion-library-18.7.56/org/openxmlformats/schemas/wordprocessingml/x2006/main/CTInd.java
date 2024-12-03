/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;

public interface CTInd
extends XmlObject {
    public static final DocumentFactory<CTInd> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctind4b93type");
    public static final SchemaType type = Factory.getType();

    public Object getStart();

    public STSignedTwipsMeasure xgetStart();

    public boolean isSetStart();

    public void setStart(Object var1);

    public void xsetStart(STSignedTwipsMeasure var1);

    public void unsetStart();

    public BigInteger getStartChars();

    public STDecimalNumber xgetStartChars();

    public boolean isSetStartChars();

    public void setStartChars(BigInteger var1);

    public void xsetStartChars(STDecimalNumber var1);

    public void unsetStartChars();

    public Object getEnd();

    public STSignedTwipsMeasure xgetEnd();

    public boolean isSetEnd();

    public void setEnd(Object var1);

    public void xsetEnd(STSignedTwipsMeasure var1);

    public void unsetEnd();

    public BigInteger getEndChars();

    public STDecimalNumber xgetEndChars();

    public boolean isSetEndChars();

    public void setEndChars(BigInteger var1);

    public void xsetEndChars(STDecimalNumber var1);

    public void unsetEndChars();

    public Object getLeft();

    public STSignedTwipsMeasure xgetLeft();

    public boolean isSetLeft();

    public void setLeft(Object var1);

    public void xsetLeft(STSignedTwipsMeasure var1);

    public void unsetLeft();

    public BigInteger getLeftChars();

    public STDecimalNumber xgetLeftChars();

    public boolean isSetLeftChars();

    public void setLeftChars(BigInteger var1);

    public void xsetLeftChars(STDecimalNumber var1);

    public void unsetLeftChars();

    public Object getRight();

    public STSignedTwipsMeasure xgetRight();

    public boolean isSetRight();

    public void setRight(Object var1);

    public void xsetRight(STSignedTwipsMeasure var1);

    public void unsetRight();

    public BigInteger getRightChars();

    public STDecimalNumber xgetRightChars();

    public boolean isSetRightChars();

    public void setRightChars(BigInteger var1);

    public void xsetRightChars(STDecimalNumber var1);

    public void unsetRightChars();

    public Object getHanging();

    public STTwipsMeasure xgetHanging();

    public boolean isSetHanging();

    public void setHanging(Object var1);

    public void xsetHanging(STTwipsMeasure var1);

    public void unsetHanging();

    public BigInteger getHangingChars();

    public STDecimalNumber xgetHangingChars();

    public boolean isSetHangingChars();

    public void setHangingChars(BigInteger var1);

    public void xsetHangingChars(STDecimalNumber var1);

    public void unsetHangingChars();

    public Object getFirstLine();

    public STTwipsMeasure xgetFirstLine();

    public boolean isSetFirstLine();

    public void setFirstLine(Object var1);

    public void xsetFirstLine(STTwipsMeasure var1);

    public void unsetFirstLine();

    public BigInteger getFirstLineChars();

    public STDecimalNumber xgetFirstLineChars();

    public boolean isSetFirstLineChars();

    public void setFirstLineChars(BigInteger var1);

    public void xsetFirstLineChars(STDecimalNumber var1);

    public void unsetFirstLineChars();
}

