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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

public interface CTPageSz
extends XmlObject {
    public static final DocumentFactory<CTPageSz> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagesz2d12type");
    public static final SchemaType type = Factory.getType();

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

    public STPageOrientation.Enum getOrient();

    public STPageOrientation xgetOrient();

    public boolean isSetOrient();

    public void setOrient(STPageOrientation.Enum var1);

    public void xsetOrient(STPageOrientation var1);

    public void unsetOrient();

    public BigInteger getCode();

    public STDecimalNumber xgetCode();

    public boolean isSetCode();

    public void setCode(BigInteger var1);

    public void xsetCode(STDecimalNumber var1);

    public void unsetCode();
}

