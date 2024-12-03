/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.css.engine.FontFaceRule
 *  org.apache.batik.gvt.font.GVTFontFace
 *  org.apache.batik.gvt.font.GVTFontFamily
 *  org.apache.batik.gvt.font.UnresolvedFontFamily
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.bridge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.CSSFontFace;
import org.apache.batik.bridge.FontFace;
import org.apache.batik.bridge.SVGFontFaceElementBridge;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.FontFaceRule;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.UnresolvedFontFamily;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class SVGFontUtilities
implements SVGConstants {
    public static List getFontFaces(Document doc, BridgeContext ctx) {
        Map fontFamilyMap = ctx.getFontFamilyMap();
        LinkedList<FontFace> ret = (LinkedList<FontFace>)fontFamilyMap.get(doc);
        if (ret != null) {
            return ret;
        }
        ret = new LinkedList<FontFace>();
        NodeList fontFaceElements = doc.getElementsByTagNameNS("http://www.w3.org/2000/svg", "font-face");
        SVGFontFaceElementBridge fontFaceBridge = (SVGFontFaceElementBridge)ctx.getBridge("http://www.w3.org/2000/svg", "font-face");
        for (int i = 0; i < fontFaceElements.getLength(); ++i) {
            Element fontFaceElement = (Element)fontFaceElements.item(i);
            ret.add(fontFaceBridge.createFontFace(ctx, fontFaceElement));
        }
        CSSEngine engine = ((SVGOMDocument)doc).getCSSEngine();
        List sms = engine.getFontFaces();
        for (Object sm : sms) {
            FontFaceRule ffr = (FontFaceRule)sm;
            ret.add(CSSFontFace.createCSSFontFace(engine, ffr));
        }
        return ret;
    }

    /*
     * WARNING - void declaration
     */
    public static GVTFontFamily getFontFamily(Element textElement, BridgeContext ctx, String fontFamilyName, String fontWeight, String fontStyle) {
        String fontKeyName = fontFamilyName.toLowerCase() + " " + fontWeight + " " + fontStyle;
        Map fontFamilyMap = ctx.getFontFamilyMap();
        GVTFontFamily fontFamily = (GVTFontFamily)fontFamilyMap.get(fontKeyName);
        if (fontFamily != null) {
            return fontFamily;
        }
        Document doc = textElement.getOwnerDocument();
        List fontFaces = (List)fontFamilyMap.get(doc);
        if (fontFaces == null) {
            fontFaces = SVGFontUtilities.getFontFaces(doc, ctx);
            fontFamilyMap.put(doc, fontFaces);
        }
        Iterator iter = fontFaces.iterator();
        LinkedList<Object> svgFontFamilies = new LinkedList<Object>();
        while (iter.hasNext()) {
            Object ffam;
            String fontFaceStyle;
            FontFace fontFace = (FontFace)iter.next();
            if (!fontFace.hasFamilyName(fontFamilyName) || !(fontFaceStyle = fontFace.getFontStyle()).equals("all") && fontFaceStyle.indexOf(fontStyle) == -1 || (ffam = fontFace.getFontFamily(ctx)) == null) continue;
            svgFontFamilies.add(ffam);
        }
        if (svgFontFamilies.size() == 1) {
            fontFamilyMap.put(fontKeyName, svgFontFamilies.get(0));
            return (GVTFontFamily)svgFontFamilies.get(0);
        }
        if (svgFontFamilies.size() > 1) {
            void var15_19;
            void var15_17;
            String fontWeightNumber = SVGFontUtilities.getFontWeightNumberString(fontWeight);
            ArrayList<String> fontFamilyWeights = new ArrayList<String>(svgFontFamilies.size());
            for (Object e : svgFontFamilies) {
                GVTFontFace fontFace = ((GVTFontFamily)e).getFontFace();
                String fontFaceWeight = fontFace.getFontWeight();
                fontFaceWeight = SVGFontUtilities.getFontWeightNumberString(fontFaceWeight);
                fontFamilyWeights.add(fontFaceWeight);
            }
            ArrayList<String> newFontFamilyWeights = new ArrayList<String>(fontFamilyWeights);
            int n = 100;
            while (var15_17 <= 900) {
                String weightString = String.valueOf((int)var15_17);
                boolean matched = false;
                int minDifference = 1000;
                int minDifferenceIndex = 0;
                for (int j = 0; j < fontFamilyWeights.size(); ++j) {
                    String fontFamilyWeight = (String)fontFamilyWeights.get(j);
                    if (fontFamilyWeight.indexOf(weightString) > -1) {
                        matched = true;
                        break;
                    }
                    StringTokenizer st = new StringTokenizer(fontFamilyWeight, " ,");
                    while (st.hasMoreTokens()) {
                        int weightNum = Integer.parseInt(st.nextToken());
                        int difference = Math.abs(weightNum - var15_17);
                        if (difference >= minDifference) continue;
                        minDifference = difference;
                        minDifferenceIndex = j;
                    }
                }
                if (!matched) {
                    String newFontFamilyWeight = newFontFamilyWeights.get(minDifferenceIndex) + ", " + weightString;
                    newFontFamilyWeights.set(minDifferenceIndex, newFontFamilyWeight);
                }
                var15_17 += 100;
            }
            boolean bl = false;
            while (var15_19 < svgFontFamilies.size()) {
                String fontFaceWeight = (String)newFontFamilyWeights.get((int)var15_19);
                if (fontFaceWeight.indexOf(fontWeightNumber) > -1) {
                    fontFamilyMap.put(fontKeyName, svgFontFamilies.get((int)var15_19));
                    return (GVTFontFamily)svgFontFamilies.get((int)var15_19);
                }
                ++var15_19;
            }
            fontFamilyMap.put(fontKeyName, svgFontFamilies.get(0));
            return (GVTFontFamily)svgFontFamilies.get(0);
        }
        UnresolvedFontFamily gvtFontFamily = new UnresolvedFontFamily(fontFamilyName);
        fontFamilyMap.put(fontKeyName, gvtFontFamily);
        return gvtFontFamily;
    }

    protected static String getFontWeightNumberString(String fontWeight) {
        if (fontWeight.equals("normal")) {
            return "400";
        }
        if (fontWeight.equals("bold")) {
            return "700";
        }
        if (fontWeight.equals("all")) {
            return "100, 200, 300, 400, 500, 600, 700, 800, 900";
        }
        return fontWeight;
    }
}

