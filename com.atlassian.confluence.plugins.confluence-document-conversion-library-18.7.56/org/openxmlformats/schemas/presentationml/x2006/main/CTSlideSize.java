/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.STSlideSizeCoordinate;
import org.openxmlformats.schemas.presentationml.x2006.main.STSlideSizeType;

public interface CTSlideSize
extends XmlObject {
    public static final DocumentFactory<CTSlideSize> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctslidesizeb0fdtype");
    public static final SchemaType type = Factory.getType();

    public int getCx();

    public STSlideSizeCoordinate xgetCx();

    public void setCx(int var1);

    public void xsetCx(STSlideSizeCoordinate var1);

    public int getCy();

    public STSlideSizeCoordinate xgetCy();

    public void setCy(int var1);

    public void xsetCy(STSlideSizeCoordinate var1);

    public STSlideSizeType.Enum getType();

    public STSlideSizeType xgetType();

    public boolean isSetType();

    public void setType(STSlideSizeType.Enum var1);

    public void xsetType(STSlideSizeType var1);

    public void unsetType();
}

