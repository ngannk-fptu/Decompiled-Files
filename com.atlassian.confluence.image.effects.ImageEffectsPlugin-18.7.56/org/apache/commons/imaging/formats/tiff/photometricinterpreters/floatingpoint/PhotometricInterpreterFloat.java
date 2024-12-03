/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters.floatingpoint;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.floatingpoint.PaletteEntry;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.floatingpoint.PaletteEntryForRange;

public class PhotometricInterpreterFloat
extends PhotometricInterpreter {
    ArrayList<PaletteEntry> rangePaletteEntries = new ArrayList();
    ArrayList<PaletteEntry> singleValuePaletteEntries = new ArrayList();
    float minFound = Float.POSITIVE_INFINITY;
    float maxFound = Float.NEGATIVE_INFINITY;
    int xMin;
    int yMin;
    int xMax;
    int yMax;
    double sumFound;
    int nFound;

    public PhotometricInterpreterFloat(float valueBlack, float valueWhite) {
        super(1, new int[]{32}, 0, 32, 32);
        if (valueWhite > valueBlack) {
            PaletteEntryForRange entry = new PaletteEntryForRange(valueBlack, valueWhite, Color.black, Color.white);
            this.rangePaletteEntries.add(entry);
        } else {
            PaletteEntryForRange entry = new PaletteEntryForRange(valueWhite, valueBlack, Color.white, Color.black);
            this.rangePaletteEntries.add(entry);
        }
    }

    public PhotometricInterpreterFloat(List<PaletteEntry> paletteEntries) {
        super(1, new int[]{32}, 0, 32, 32);
        if (paletteEntries == null || paletteEntries.isEmpty()) {
            throw new IllegalArgumentException("Palette entries list must be non-null and non-empty");
        }
        for (PaletteEntry entry : paletteEntries) {
            if (entry.coversSingleEntry()) {
                this.singleValuePaletteEntries.add(entry);
                continue;
            }
            this.rangePaletteEntries.add(entry);
        }
        Comparator comparator = (o1, o2) -> {
            if (o1.getLowerBound() == o2.getLowerBound()) {
                return Double.compare(o1.getUpperBound(), o2.getUpperBound());
            }
            return Double.compare(o1.getLowerBound(), o2.getLowerBound());
        };
        Collections.sort(this.rangePaletteEntries, comparator);
        Collections.sort(this.singleValuePaletteEntries, comparator);
    }

    @Override
    public void interpretPixel(ImageBuilder imageBuilder, int[] samples, int x, int y) throws ImageReadException, IOException {
        float f = Float.intBitsToFloat(samples[0]);
        for (PaletteEntry entry : this.singleValuePaletteEntries) {
            if (!entry.isCovered(f)) continue;
            int p = entry.getARGB(f);
            imageBuilder.setRGB(x, y, p);
            return;
        }
        if (Float.isNaN(f)) {
            return;
        }
        if (f < this.minFound) {
            this.minFound = f;
            this.xMin = x;
            this.yMin = y;
        }
        if (f > this.maxFound) {
            this.maxFound = f;
            this.xMax = x;
            this.yMax = y;
        }
        ++this.nFound;
        this.sumFound += (double)f;
        for (PaletteEntry entry : this.singleValuePaletteEntries) {
            if (!entry.isCovered(f)) continue;
            int p = entry.getARGB(f);
            imageBuilder.setRGB(x, y, p);
            return;
        }
        for (PaletteEntry entry : this.rangePaletteEntries) {
            if (!entry.isCovered(f)) continue;
            int p = entry.getARGB(f);
            imageBuilder.setRGB(x, y, p);
            break;
        }
    }

    public float getMinFound() {
        return this.minFound;
    }

    public int[] getMaxXY() {
        return new int[]{this.xMax, this.yMax};
    }

    public float getMaxFound() {
        return this.maxFound;
    }

    public int[] getMinXY() {
        return new int[]{this.xMin, this.yMin};
    }

    public float getMeanFound() {
        if (this.nFound == 0) {
            return 0.0f;
        }
        return (float)(this.sumFound / (double)this.nFound);
    }

    public int mapValueToARGB(float f) {
        for (PaletteEntry entry : this.singleValuePaletteEntries) {
            if (!entry.isCovered(f)) continue;
            return entry.getARGB(f);
        }
        if (Float.isNaN(f)) {
            return 0;
        }
        for (PaletteEntry entry : this.rangePaletteEntries) {
            if (!entry.isCovered(f)) continue;
            return entry.getARGB(f);
        }
        return 0;
    }
}

