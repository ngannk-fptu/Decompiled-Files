/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontScalePercentOrPercentString;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextSpacingPercentOrPercentString;

public interface CTTextNormalAutofit
extends XmlObject {
    public static final DocumentFactory<CTTextNormalAutofit> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextnormalautofitbbdftype");
    public static final SchemaType type = Factory.getType();

    public Object getFontScale();

    public STTextFontScalePercentOrPercentString xgetFontScale();

    public boolean isSetFontScale();

    public void setFontScale(Object var1);

    public void xsetFontScale(STTextFontScalePercentOrPercentString var1);

    public void unsetFontScale();

    public Object getLnSpcReduction();

    public STTextSpacingPercentOrPercentString xgetLnSpcReduction();

    public boolean isSetLnSpcReduction();

    public void setLnSpcReduction(Object var1);

    public void xsetLnSpcReduction(STTextSpacingPercentOrPercentString var1);

    public void unsetLnSpcReduction();
}

