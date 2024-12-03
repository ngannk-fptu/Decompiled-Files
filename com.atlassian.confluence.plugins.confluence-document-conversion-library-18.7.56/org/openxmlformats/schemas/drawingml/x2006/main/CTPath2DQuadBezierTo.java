/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;

public interface CTPath2DQuadBezierTo
extends XmlObject {
    public static final DocumentFactory<CTPath2DQuadBezierTo> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpath2dquadbezierto3f53type");
    public static final SchemaType type = Factory.getType();

    public List<CTAdjPoint2D> getPtList();

    public CTAdjPoint2D[] getPtArray();

    public CTAdjPoint2D getPtArray(int var1);

    public int sizeOfPtArray();

    public void setPtArray(CTAdjPoint2D[] var1);

    public void setPtArray(int var1, CTAdjPoint2D var2);

    public CTAdjPoint2D insertNewPt(int var1);

    public CTAdjPoint2D addNewPt();

    public void removePt(int var1);
}

