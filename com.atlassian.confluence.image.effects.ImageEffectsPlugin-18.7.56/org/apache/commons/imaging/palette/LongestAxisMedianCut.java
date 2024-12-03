/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.palette.ColorComponent;
import org.apache.commons.imaging.palette.ColorCount;
import org.apache.commons.imaging.palette.ColorCountComparator;
import org.apache.commons.imaging.palette.ColorGroup;
import org.apache.commons.imaging.palette.ColorGroupCut;
import org.apache.commons.imaging.palette.MedianCut;

public class LongestAxisMedianCut
implements MedianCut {
    private static final Comparator<ColorGroup> COMPARATOR = (cg1, cg2) -> {
        if (cg1.maxDiff == cg2.maxDiff) {
            return cg2.diffTotal - cg1.diffTotal;
        }
        return cg2.maxDiff - cg1.maxDiff;
    };

    @Override
    public boolean performNextMedianCut(List<ColorGroup> colorGroups, boolean ignoreAlpha) throws ImageWriteException {
        Collections.sort(colorGroups, COMPARATOR);
        ColorGroup colorGroup = colorGroups.get(0);
        if (colorGroup.maxDiff == 0) {
            return false;
        }
        if (!ignoreAlpha && colorGroup.alphaDiff > colorGroup.redDiff && colorGroup.alphaDiff > colorGroup.greenDiff && colorGroup.alphaDiff > colorGroup.blueDiff) {
            this.doCut(colorGroup, ColorComponent.ALPHA, colorGroups, ignoreAlpha);
        } else if (colorGroup.redDiff > colorGroup.greenDiff && colorGroup.redDiff > colorGroup.blueDiff) {
            this.doCut(colorGroup, ColorComponent.RED, colorGroups, ignoreAlpha);
        } else if (colorGroup.greenDiff > colorGroup.blueDiff) {
            this.doCut(colorGroup, ColorComponent.GREEN, colorGroups, ignoreAlpha);
        } else {
            this.doCut(colorGroup, ColorComponent.BLUE, colorGroups, ignoreAlpha);
        }
        return true;
    }

    private void doCut(ColorGroup colorGroup, ColorComponent mode, List<ColorGroup> colorGroups, boolean ignoreAlpha) throws ImageWriteException {
        int limit;
        int medianIndex;
        List<ColorCount> colorCounts = colorGroup.getColorCounts();
        Collections.sort(colorCounts, new ColorCountComparator(mode));
        int countHalf = (int)Math.round((double)colorGroup.totalPoints / 2.0);
        int oldCount = 0;
        int newCount = 0;
        for (medianIndex = 0; medianIndex < colorCounts.size(); ++medianIndex) {
            ColorCount colorCount = colorCounts.get(medianIndex);
            if ((newCount += colorCount.count) >= countHalf) break;
            oldCount = newCount;
        }
        if (medianIndex == colorCounts.size() - 1) {
            --medianIndex;
        } else if (medianIndex > 0) {
            int newDiff = Math.abs(newCount - countHalf);
            int oldDiff = Math.abs(countHalf - oldCount);
            if (oldDiff < newDiff) {
                --medianIndex;
            }
        }
        colorGroups.remove(colorGroup);
        ArrayList<ColorCount> colorCounts1 = new ArrayList<ColorCount>(colorCounts.subList(0, medianIndex + 1));
        ArrayList<ColorCount> colorCounts2 = new ArrayList<ColorCount>(colorCounts.subList(medianIndex + 1, colorCounts.size()));
        ColorGroup less = new ColorGroup(new ArrayList<ColorCount>(colorCounts1), ignoreAlpha);
        colorGroups.add(less);
        ColorGroup more = new ColorGroup(new ArrayList<ColorCount>(colorCounts2), ignoreAlpha);
        colorGroups.add(more);
        ColorCount medianValue = colorCounts.get(medianIndex);
        switch (mode) {
            case ALPHA: {
                limit = medianValue.alpha;
                break;
            }
            case RED: {
                limit = medianValue.red;
                break;
            }
            case GREEN: {
                limit = medianValue.green;
                break;
            }
            case BLUE: {
                limit = medianValue.blue;
                break;
            }
            default: {
                throw new Error("Bad mode.");
            }
        }
        colorGroup.cut = new ColorGroupCut(less, more, mode, limit);
    }
}

