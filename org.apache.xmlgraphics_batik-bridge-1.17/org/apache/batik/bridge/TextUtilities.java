/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.value.Value
 *  org.apache.batik.parser.UnitProcessor$Context
 *  org.apache.batik.util.CSSConstants
 */
package org.apache.batik.bridge;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.TextNode;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.parser.UnitProcessor;
import org.apache.batik.util.CSSConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class TextUtilities
implements CSSConstants,
ErrorConstants {
    public static String getElementContent(Element e) {
        StringBuffer result = new StringBuffer();
        block4: for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            switch (n.getNodeType()) {
                case 1: {
                    result.append(TextUtilities.getElementContent((Element)n));
                    continue block4;
                }
                case 3: 
                case 4: {
                    result.append(n.getNodeValue());
                }
            }
        }
        return result.toString();
    }

    public static ArrayList svgHorizontalCoordinateArrayToUserSpace(Element element, String attrName, String valueStr, BridgeContext ctx) {
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        ArrayList<Float> values = new ArrayList<Float>();
        StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        while (st.hasMoreTokens()) {
            values.add(Float.valueOf(UnitProcessor.svgHorizontalCoordinateToUserSpace(st.nextToken(), attrName, uctx)));
        }
        return values;
    }

    public static ArrayList svgVerticalCoordinateArrayToUserSpace(Element element, String attrName, String valueStr, BridgeContext ctx) {
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        ArrayList<Float> values = new ArrayList<Float>();
        StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        while (st.hasMoreTokens()) {
            values.add(Float.valueOf(UnitProcessor.svgVerticalCoordinateToUserSpace(st.nextToken(), attrName, uctx)));
        }
        return values;
    }

    public static ArrayList svgRotateArrayToFloats(Element element, String attrName, String valueStr, BridgeContext ctx) {
        StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        ArrayList<Float> values = new ArrayList<Float>();
        while (st.hasMoreTokens()) {
            try {
                String s = st.nextToken();
                values.add(Float.valueOf((float)Math.toRadians(SVGUtilities.convertSVGNumber(s))));
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, element, nfEx, "attribute.malformed", new Object[]{attrName, valueStr});
            }
        }
        return values;
    }

    public static Float convertFontSize(Element e) {
        Value v = CSSUtilities.getComputedStyle(e, 22);
        return Float.valueOf(v.getFloatValue());
    }

    public static Float convertFontStyle(Element e) {
        Value v = CSSUtilities.getComputedStyle(e, 25);
        switch (v.getStringValue().charAt(0)) {
            case 'n': {
                return TextAttribute.POSTURE_REGULAR;
            }
        }
        return TextAttribute.POSTURE_OBLIQUE;
    }

    public static Float convertFontStretch(Element e) {
        Value v = CSSUtilities.getComputedStyle(e, 24);
        String s = v.getStringValue();
        switch (s.charAt(0)) {
            case 'u': {
                if (s.charAt(6) == 'c') {
                    return TextAttribute.WIDTH_CONDENSED;
                }
                return TextAttribute.WIDTH_EXTENDED;
            }
            case 'e': {
                if (s.charAt(6) == 'c') {
                    return TextAttribute.WIDTH_CONDENSED;
                }
                if (s.length() == 8) {
                    return TextAttribute.WIDTH_SEMI_EXTENDED;
                }
                return TextAttribute.WIDTH_EXTENDED;
            }
            case 's': {
                if (s.charAt(6) == 'c') {
                    return TextAttribute.WIDTH_SEMI_CONDENSED;
                }
                return TextAttribute.WIDTH_SEMI_EXTENDED;
            }
        }
        return TextAttribute.WIDTH_REGULAR;
    }

    public static Float convertFontWeight(Element e) {
        float javaVersion;
        Value v = CSSUtilities.getComputedStyle(e, 27);
        int weight = (int)v.getFloatValue();
        switch (weight) {
            case 100: {
                return TextAttribute.WEIGHT_EXTRA_LIGHT;
            }
            case 200: {
                return TextAttribute.WEIGHT_LIGHT;
            }
            case 300: {
                return TextAttribute.WEIGHT_DEMILIGHT;
            }
            case 400: {
                return TextAttribute.WEIGHT_REGULAR;
            }
            case 500: {
                return TextAttribute.WEIGHT_SEMIBOLD;
            }
        }
        String javaVersionString = System.getProperty("java.specification.version");
        float f = javaVersion = javaVersionString != null ? Float.parseFloat(javaVersionString) : 1.5f;
        if ((double)javaVersion < 1.5) {
            return TextAttribute.WEIGHT_BOLD;
        }
        switch (weight) {
            case 600: {
                return TextAttribute.WEIGHT_MEDIUM;
            }
            case 700: {
                return TextAttribute.WEIGHT_BOLD;
            }
            case 800: {
                return TextAttribute.WEIGHT_HEAVY;
            }
            case 900: {
                return TextAttribute.WEIGHT_ULTRABOLD;
            }
        }
        return TextAttribute.WEIGHT_REGULAR;
    }

    public static TextNode.Anchor convertTextAnchor(Element e) {
        Value v = CSSUtilities.getComputedStyle(e, 53);
        switch (v.getStringValue().charAt(0)) {
            case 's': {
                return TextNode.Anchor.START;
            }
            case 'm': {
                return TextNode.Anchor.MIDDLE;
            }
        }
        return TextNode.Anchor.END;
    }

    public static Object convertBaselineShift(Element e) {
        Value v = CSSUtilities.getComputedStyle(e, 1);
        if (v.getPrimitiveType() == 21) {
            String s = v.getStringValue();
            switch (s.charAt(2)) {
                case 'p': {
                    return TextAttribute.SUPERSCRIPT_SUPER;
                }
                case 'b': {
                    return TextAttribute.SUPERSCRIPT_SUB;
                }
            }
            return null;
        }
        return Float.valueOf(v.getFloatValue());
    }

    public static Float convertKerning(Element e) {
        Value v = CSSUtilities.getComputedStyle(e, 31);
        if (v.getPrimitiveType() == 21) {
            return null;
        }
        return Float.valueOf(v.getFloatValue());
    }

    public static Float convertLetterSpacing(Element e) {
        Value v = CSSUtilities.getComputedStyle(e, 32);
        if (v.getPrimitiveType() == 21) {
            return null;
        }
        return Float.valueOf(v.getFloatValue());
    }

    public static Float convertWordSpacing(Element e) {
        Value v = CSSUtilities.getComputedStyle(e, 58);
        if (v.getPrimitiveType() == 21) {
            return null;
        }
        return Float.valueOf(v.getFloatValue());
    }
}

