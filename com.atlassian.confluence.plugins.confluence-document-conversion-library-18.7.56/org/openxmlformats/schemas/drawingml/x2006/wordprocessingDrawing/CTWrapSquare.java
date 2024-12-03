/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTEffectExtent;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STWrapDistance;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STWrapText;

public interface CTWrapSquare
extends XmlObject {
    public static final DocumentFactory<CTWrapSquare> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctwrapsquare2678type");
    public static final SchemaType type = Factory.getType();

    public CTEffectExtent getEffectExtent();

    public boolean isSetEffectExtent();

    public void setEffectExtent(CTEffectExtent var1);

    public CTEffectExtent addNewEffectExtent();

    public void unsetEffectExtent();

    public STWrapText.Enum getWrapText();

    public STWrapText xgetWrapText();

    public void setWrapText(STWrapText.Enum var1);

    public void xsetWrapText(STWrapText var1);

    public long getDistT();

    public STWrapDistance xgetDistT();

    public boolean isSetDistT();

    public void setDistT(long var1);

    public void xsetDistT(STWrapDistance var1);

    public void unsetDistT();

    public long getDistB();

    public STWrapDistance xgetDistB();

    public boolean isSetDistB();

    public void setDistB(long var1);

    public void xsetDistB(STWrapDistance var1);

    public void unsetDistB();

    public long getDistL();

    public STWrapDistance xgetDistL();

    public boolean isSetDistL();

    public void setDistL(long var1);

    public void xsetDistL(STWrapDistance var1);

    public void unsetDistL();

    public long getDistR();

    public STWrapDistance xgetDistR();

    public boolean isSetDistR();

    public void setDistR(long var1);

    public void xsetDistR(STWrapDistance var1);

    public void unsetDistR();
}

