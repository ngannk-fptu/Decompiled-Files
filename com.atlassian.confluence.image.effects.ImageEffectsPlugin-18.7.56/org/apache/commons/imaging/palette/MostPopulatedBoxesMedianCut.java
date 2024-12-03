/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.palette.ColorComponent;
import org.apache.commons.imaging.palette.ColorCount;
import org.apache.commons.imaging.palette.ColorCountComparator;
import org.apache.commons.imaging.palette.ColorGroup;
import org.apache.commons.imaging.palette.ColorGroupCut;
import org.apache.commons.imaging.palette.MedianCut;

public class MostPopulatedBoxesMedianCut
implements MedianCut {
    @Override
    public boolean performNextMedianCut(List<ColorGroup> colorGroups, boolean ignoreAlpha) throws ImageWriteException {
        int limit;
        int maxPoints = 0;
        ColorGroup colorGroup = null;
        for (ColorGroup group : colorGroups) {
            if (group.maxDiff <= 0 || group.totalPoints <= maxPoints) continue;
            colorGroup = group;
            maxPoints = group.totalPoints;
        }
        if (colorGroup == null) {
            return false;
        }
        List<ColorCount> colorCounts = colorGroup.getColorCounts();
        double bestScore = Double.MAX_VALUE;
        Enum bestColorComponent = null;
        int bestMedianIndex = -1;
        for (ColorComponent colorComponent : ColorComponent.values()) {
            int medianIndex;
            if (ignoreAlpha && colorComponent == ColorComponent.ALPHA) continue;
            Collections.sort(colorCounts, new ColorCountComparator(colorComponent));
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
            ArrayList<ColorCount> lowerColors = new ArrayList<ColorCount>(colorCounts.subList(0, medianIndex + 1));
            ArrayList<ColorCount> upperColors = new ArrayList<ColorCount>(colorCounts.subList(medianIndex + 1, colorCounts.size()));
            if (lowerColors.isEmpty() || upperColors.isEmpty()) continue;
            ColorGroup lowerGroup = new ColorGroup(lowerColors, ignoreAlpha);
            ColorGroup upperGroup = new ColorGroup(upperColors, ignoreAlpha);
            int diff = Math.abs(lowerGroup.totalPoints - upperGroup.totalPoints);
            double score = (double)diff / (double)Math.max(lowerGroup.totalPoints, upperGroup.totalPoints);
            if (!(score < bestScore)) continue;
            bestScore = score;
            bestColorComponent = colorComponent;
            bestMedianIndex = medianIndex;
        }
        if (bestColorComponent == null) {
            return false;
        }
        Collections.sort(colorCounts, new ColorCountComparator((ColorComponent)bestColorComponent));
        ArrayList<ColorCount> lowerColors = new ArrayList<ColorCount>(colorCounts.subList(0, bestMedianIndex + 1));
        ArrayList<ColorCount> upperColors = new ArrayList<ColorCount>(colorCounts.subList(bestMedianIndex + 1, colorCounts.size()));
        ColorGroup lowerGroup = new ColorGroup(lowerColors, ignoreAlpha);
        ColorGroup upperGroup = new ColorGroup(upperColors, ignoreAlpha);
        colorGroups.remove(colorGroup);
        colorGroups.add(lowerGroup);
        colorGroups.add(upperGroup);
        ColorCount medianValue = colorCounts.get(bestMedianIndex);
        switch (1.$SwitchMap$org$apache$commons$imaging$palette$ColorComponent[bestColorComponent.ordinal()]) {
            case 1: {
                limit = medianValue.alpha;
                break;
            }
            case 2: {
                limit = medianValue.red;
                break;
            }
            case 3: {
                limit = medianValue.green;
                break;
            }
            case 4: {
                limit = medianValue.blue;
                break;
            }
            default: {
                throw new Error("Bad mode.");
            }
        }
        colorGroup.cut = new ColorGroupCut(lowerGroup, upperGroup, (ColorComponent)bestColorComponent, limit);
        return true;
    }
}

