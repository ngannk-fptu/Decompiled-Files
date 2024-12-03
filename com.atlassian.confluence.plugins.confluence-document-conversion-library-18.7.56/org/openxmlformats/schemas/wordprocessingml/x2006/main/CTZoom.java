/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STZoom
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumberOrPercent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STZoom;

public interface CTZoom
extends XmlObject {
    public static final DocumentFactory<CTZoom> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctzoomc275type");
    public static final SchemaType type = Factory.getType();

    public STZoom.Enum getVal();

    public STZoom xgetVal();

    public boolean isSetVal();

    public void setVal(STZoom.Enum var1);

    public void xsetVal(STZoom var1);

    public void unsetVal();

    public Object getPercent();

    public STDecimalNumberOrPercent xgetPercent();

    public void setPercent(Object var1);

    public void xsetPercent(STDecimalNumberOrPercent var1);
}

