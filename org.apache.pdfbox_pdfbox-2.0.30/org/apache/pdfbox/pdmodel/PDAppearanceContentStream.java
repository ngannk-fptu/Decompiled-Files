/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDAbstractContentStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

public final class PDAppearanceContentStream
extends PDAbstractContentStream
implements Closeable {
    public PDAppearanceContentStream(PDAppearanceStream appearance) throws IOException {
        this(appearance, appearance.getStream().createOutputStream());
    }

    public PDAppearanceContentStream(PDAppearanceStream appearance, boolean compress) throws IOException {
        this(appearance, appearance.getStream().createOutputStream(compress ? COSName.FLATE_DECODE : null));
    }

    public PDAppearanceContentStream(PDAppearanceStream appearance, OutputStream outputStream) {
        super(null, outputStream, appearance.getResources());
    }

    public boolean setStrokingColorOnDemand(PDColor color) throws IOException {
        float[] components;
        if (color != null && (components = color.getComponents()).length > 0) {
            this.setStrokingColor(components);
            return true;
        }
        return false;
    }

    public void setStrokingColor(float[] components) throws IOException {
        for (float value : components) {
            this.writeOperand(value);
        }
        int numComponents = components.length;
        switch (numComponents) {
            case 1: {
                this.writeOperator("G");
                break;
            }
            case 3: {
                this.writeOperator("RG");
                break;
            }
            case 4: {
                this.writeOperator("K");
                break;
            }
        }
    }

    public boolean setNonStrokingColorOnDemand(PDColor color) throws IOException {
        float[] components;
        if (color != null && (components = color.getComponents()).length > 0) {
            this.setNonStrokingColor(components);
            return true;
        }
        return false;
    }

    public void setNonStrokingColor(float[] components) throws IOException {
        for (float value : components) {
            this.writeOperand(value);
        }
        int numComponents = components.length;
        switch (numComponents) {
            case 1: {
                this.writeOperator("g");
                break;
            }
            case 3: {
                this.writeOperator("rg");
                break;
            }
            case 4: {
                this.writeOperator("k");
                break;
            }
        }
    }

    public void setBorderLine(float lineWidth, PDBorderStyleDictionary bs, COSArray border) throws IOException {
        if (bs != null && bs.getCOSObject().containsKey(COSName.D) && bs.getStyle().equals("D")) {
            this.setLineDashPattern(bs.getDashStyle().getDashArray(), 0.0f);
        } else if (bs == null && border.size() > 3) {
            if (border.getObject(3) instanceof COSArray) {
                this.setLineDashPattern(((COSArray)border.getObject(3)).toFloatArray(), 0.0f);
            } else {
                this.setLineDashPattern(new float[1], 0.0f);
            }
        }
        this.setLineWidthOnDemand(lineWidth);
    }

    public void setLineWidthOnDemand(float lineWidth) throws IOException {
        if (!((double)Math.abs(lineWidth - 1.0f) < 1.0E-6)) {
            this.setLineWidth(lineWidth);
        }
    }

    public void drawShape(float lineWidth, boolean hasStroke, boolean hasFill) throws IOException {
        boolean resolvedHasStroke = hasStroke;
        if ((double)lineWidth < 1.0E-6) {
            resolvedHasStroke = false;
        }
        if (hasFill && resolvedHasStroke) {
            this.fillAndStroke();
        } else if (resolvedHasStroke) {
            this.stroke();
        } else if (hasFill) {
            this.fill();
        } else {
            this.writeOperator("n");
        }
    }
}

