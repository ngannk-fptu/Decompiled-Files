/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import java.util.List;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.palette.ColorGroup;

public interface MedianCut {
    public boolean performNextMedianCut(List<ColorGroup> var1, boolean var2) throws ImageWriteException;
}

