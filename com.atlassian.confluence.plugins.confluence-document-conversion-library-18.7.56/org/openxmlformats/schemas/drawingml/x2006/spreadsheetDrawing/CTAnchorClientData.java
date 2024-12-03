/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTAnchorClientData
extends XmlObject {
    public static final DocumentFactory<CTAnchorClientData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctanchorclientdata02betype");
    public static final SchemaType type = Factory.getType();

    public boolean getFLocksWithSheet();

    public XmlBoolean xgetFLocksWithSheet();

    public boolean isSetFLocksWithSheet();

    public void setFLocksWithSheet(boolean var1);

    public void xsetFLocksWithSheet(XmlBoolean var1);

    public void unsetFLocksWithSheet();

    public boolean getFPrintsWithSheet();

    public XmlBoolean xgetFPrintsWithSheet();

    public boolean isSetFPrintsWithSheet();

    public void setFPrintsWithSheet(boolean var1);

    public void xsetFPrintsWithSheet(XmlBoolean var1);

    public void unsetFPrintsWithSheet();
}

