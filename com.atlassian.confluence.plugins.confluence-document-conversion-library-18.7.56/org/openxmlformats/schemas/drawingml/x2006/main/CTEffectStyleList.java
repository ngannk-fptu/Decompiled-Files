/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectStyleItem;

public interface CTEffectStyleList
extends XmlObject {
    public static final DocumentFactory<CTEffectStyleList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cteffectstylelistc50ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTEffectStyleItem> getEffectStyleList();

    public CTEffectStyleItem[] getEffectStyleArray();

    public CTEffectStyleItem getEffectStyleArray(int var1);

    public int sizeOfEffectStyleArray();

    public void setEffectStyleArray(CTEffectStyleItem[] var1);

    public void setEffectStyleArray(int var1, CTEffectStyleItem var2);

    public CTEffectStyleItem insertNewEffectStyle(int var1);

    public CTEffectStyleItem addNewEffectStyle();

    public void removeEffectStyle(int var1);
}

