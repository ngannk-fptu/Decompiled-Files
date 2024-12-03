/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;

public interface CTHeight
extends XmlObject {
    public static final DocumentFactory<CTHeight> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctheighta2e1type");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STTwipsMeasure xgetVal();

    public boolean isSetVal();

    public void setVal(Object var1);

    public void xsetVal(STTwipsMeasure var1);

    public void unsetVal();

    public STHeightRule.Enum getHRule();

    public STHeightRule xgetHRule();

    public boolean isSetHRule();

    public void setHRule(STHeightRule.Enum var1);

    public void xsetHRule(STHeightRule var1);

    public void unsetHRule();
}

