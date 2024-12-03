/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 */
package org.apache.batik.svggen;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.AbstractSVGConverter;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGPaintDescriptor;

public class SVGColor
extends AbstractSVGConverter {
    public static final Color aqua = Color.cyan;
    public static final Color black = Color.black;
    public static final Color blue = Color.blue;
    public static final Color fuchsia = Color.magenta;
    public static final Color gray = Color.gray;
    public static final Color green = new Color(0, 128, 0);
    public static final Color lime = Color.green;
    public static final Color maroon = new Color(128, 0, 0);
    public static final Color navy = new Color(0, 0, 128);
    public static final Color olive = new Color(128, 128, 0);
    public static final Color purple = new Color(128, 0, 128);
    public static final Color red = Color.red;
    public static final Color silver = new Color(192, 192, 192);
    public static final Color teal = new Color(0, 128, 128);
    public static final Color white = Color.white;
    public static final Color yellow = Color.yellow;
    private static Map colorMap = new HashMap();

    public SVGColor(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    @Override
    public SVGDescriptor toSVG(GraphicContext gc) {
        Paint paint = gc.getPaint();
        return SVGColor.toSVG((Color)paint, this.generatorContext);
    }

    public static SVGPaintDescriptor toSVG(Color color, SVGGeneratorContext gc) {
        String cssColor = (String)colorMap.get(color);
        if (cssColor == null) {
            StringBuffer cssColorBuffer = new StringBuffer("rgb(");
            cssColorBuffer.append(color.getRed());
            cssColorBuffer.append(",");
            cssColorBuffer.append(color.getGreen());
            cssColorBuffer.append(",");
            cssColorBuffer.append(color.getBlue());
            cssColorBuffer.append(")");
            cssColor = cssColorBuffer.toString();
        }
        float alpha = (float)color.getAlpha() / 255.0f;
        String alphaString = gc.doubleString(alpha);
        return new SVGPaintDescriptor(cssColor, alphaString);
    }

    static {
        colorMap.put(black, "black");
        colorMap.put(silver, "silver");
        colorMap.put(gray, "gray");
        colorMap.put(white, "white");
        colorMap.put(maroon, "maroon");
        colorMap.put(red, "red");
        colorMap.put(purple, "purple");
        colorMap.put(fuchsia, "fuchsia");
        colorMap.put(green, "green");
        colorMap.put(lime, "lime");
        colorMap.put(olive, "olive");
        colorMap.put(yellow, "yellow");
        colorMap.put(navy, "navy");
        colorMap.put(blue, "blue");
        colorMap.put(teal, "teal");
        colorMap.put(aqua, "aqua");
    }
}

