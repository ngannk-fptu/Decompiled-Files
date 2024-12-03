/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import org.apache.poi.hslf.record.ColorSchemeAtom;
import org.apache.poi.hslf.record.PPDrawing;
import org.apache.poi.hslf.record.PositionDependentRecordContainer;

public abstract class SheetContainer
extends PositionDependentRecordContainer {
    public abstract PPDrawing getPPDrawing();

    public abstract ColorSchemeAtom getColorScheme();
}

