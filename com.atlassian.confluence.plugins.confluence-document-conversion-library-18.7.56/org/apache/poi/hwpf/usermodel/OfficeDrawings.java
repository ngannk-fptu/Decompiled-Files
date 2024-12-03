/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.Collection;
import org.apache.poi.hwpf.usermodel.OfficeDrawing;

public interface OfficeDrawings {
    public OfficeDrawing getOfficeDrawingAt(int var1);

    public Collection<OfficeDrawing> getOfficeDrawings();
}

