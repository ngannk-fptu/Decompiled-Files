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

public interface CTFitText
extends XmlObject {
    public static final DocumentFactory<CTFitText> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfittexta474type");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STTwipsMeasure xgetVal();

    public void setVal(Object var1);

    public void xsetVal(STTwipsMeasure var1);

    public BigInteger getId();

    public STDecimalNumber xgetId();

    public boolean isSetId();

    public void setId(BigInteger var1);

    public void xsetId(STDecimalNumber var1);

    public void unsetId();
}

