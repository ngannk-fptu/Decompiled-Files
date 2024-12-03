/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;

public interface CTDrawing
extends XmlObject {
    public static final DocumentFactory<CTDrawing> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdrawing8d34type");
    public static final SchemaType type = Factory.getType();

    public List<CTAnchor> getAnchorList();

    public CTAnchor[] getAnchorArray();

    public CTAnchor getAnchorArray(int var1);

    public int sizeOfAnchorArray();

    public void setAnchorArray(CTAnchor[] var1);

    public void setAnchorArray(int var1, CTAnchor var2);

    public CTAnchor insertNewAnchor(int var1);

    public CTAnchor addNewAnchor();

    public void removeAnchor(int var1);

    public List<CTInline> getInlineList();

    public CTInline[] getInlineArray();

    public CTInline getInlineArray(int var1);

    public int sizeOfInlineArray();

    public void setInlineArray(CTInline[] var1);

    public void setInlineArray(int var1, CTInline var2);

    public CTInline insertNewInline(int var1);

    public CTInline addNewInline();

    public void removeInline(int var1);
}

