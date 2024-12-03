/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;

public interface CTTextLineBreak
extends XmlObject {
    public static final DocumentFactory<CTTextLineBreak> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextlinebreak932ftype");
    public static final SchemaType type = Factory.getType();

    public CTTextCharacterProperties getRPr();

    public boolean isSetRPr();

    public void setRPr(CTTextCharacterProperties var1);

    public CTTextCharacterProperties addNewRPr();

    public void unsetRPr();
}

