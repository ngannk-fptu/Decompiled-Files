/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;

public interface CTRegularTextRun
extends XmlObject {
    public static final DocumentFactory<CTRegularTextRun> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctregulartextrun7e3dtype");
    public static final SchemaType type = Factory.getType();

    public CTTextCharacterProperties getRPr();

    public boolean isSetRPr();

    public void setRPr(CTTextCharacterProperties var1);

    public CTTextCharacterProperties addNewRPr();

    public void unsetRPr();

    public String getT();

    public XmlString xgetT();

    public void setT(String var1);

    public void xsetT(XmlString var1);
}

