/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;

public interface CTSpacing
extends XmlObject {
    public static final DocumentFactory<CTSpacing> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctspacingff2ftype");
    public static final SchemaType type = Factory.getType();

    public Object getBefore();

    public STTwipsMeasure xgetBefore();

    public boolean isSetBefore();

    public void setBefore(Object var1);

    public void xsetBefore(STTwipsMeasure var1);

    public void unsetBefore();

    public BigInteger getBeforeLines();

    public STDecimalNumber xgetBeforeLines();

    public boolean isSetBeforeLines();

    public void setBeforeLines(BigInteger var1);

    public void xsetBeforeLines(STDecimalNumber var1);

    public void unsetBeforeLines();

    public Object getBeforeAutospacing();

    public STOnOff xgetBeforeAutospacing();

    public boolean isSetBeforeAutospacing();

    public void setBeforeAutospacing(Object var1);

    public void xsetBeforeAutospacing(STOnOff var1);

    public void unsetBeforeAutospacing();

    public Object getAfter();

    public STTwipsMeasure xgetAfter();

    public boolean isSetAfter();

    public void setAfter(Object var1);

    public void xsetAfter(STTwipsMeasure var1);

    public void unsetAfter();

    public BigInteger getAfterLines();

    public STDecimalNumber xgetAfterLines();

    public boolean isSetAfterLines();

    public void setAfterLines(BigInteger var1);

    public void xsetAfterLines(STDecimalNumber var1);

    public void unsetAfterLines();

    public Object getAfterAutospacing();

    public STOnOff xgetAfterAutospacing();

    public boolean isSetAfterAutospacing();

    public void setAfterAutospacing(Object var1);

    public void xsetAfterAutospacing(STOnOff var1);

    public void unsetAfterAutospacing();

    public Object getLine();

    public STSignedTwipsMeasure xgetLine();

    public boolean isSetLine();

    public void setLine(Object var1);

    public void xsetLine(STSignedTwipsMeasure var1);

    public void unsetLine();

    public STLineSpacingRule.Enum getLineRule();

    public STLineSpacingRule xgetLineRule();

    public boolean isSetLineRule();

    public void setLineRule(STLineSpacingRule.Enum var1);

    public void xsetLineRule(STLineSpacingRule var1);

    public void unsetLineRule();
}

