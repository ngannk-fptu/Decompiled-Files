/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;

public interface CTLevelText
extends XmlObject {
    public static final DocumentFactory<CTLevelText> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctleveltext0621type");
    public static final SchemaType type = Factory.getType();

    public String getVal();

    public STString xgetVal();

    public boolean isSetVal();

    public void setVal(String var1);

    public void xsetVal(STString var1);

    public void unsetVal();

    public Object getNull();

    public STOnOff xgetNull();

    public boolean isSetNull();

    public void setNull(Object var1);

    public void xsetNull(STOnOff var1);

    public void unsetNull();
}

