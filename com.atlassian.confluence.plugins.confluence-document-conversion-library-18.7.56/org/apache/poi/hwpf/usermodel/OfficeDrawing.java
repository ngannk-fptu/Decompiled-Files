/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.ddf.EscherContainerRecord;

public interface OfficeDrawing {
    public HorizontalPositioning getHorizontalPositioning();

    public HorizontalRelativeElement getHorizontalRelative();

    public EscherContainerRecord getOfficeArtSpContainer();

    public byte[] getPictureData();

    public int getRectangleBottom();

    public int getRectangleLeft();

    public int getRectangleRight();

    public int getRectangleTop();

    public int getShapeId();

    public VerticalPositioning getVerticalPositioning();

    public VerticalRelativeElement getVerticalRelativeElement();

    public static enum VerticalRelativeElement {
        LINE,
        MARGIN,
        PAGE,
        TEXT;

    }

    public static enum VerticalPositioning {
        ABSOLUTE,
        BOTTOM,
        CENTER,
        INSIDE,
        OUTSIDE,
        TOP;

    }

    public static enum HorizontalRelativeElement {
        CHAR,
        MARGIN,
        PAGE,
        TEXT;

    }

    public static enum HorizontalPositioning {
        ABSOLUTE,
        CENTER,
        INSIDE,
        LEFT,
        OUTSIDE,
        RIGHT;

    }
}

