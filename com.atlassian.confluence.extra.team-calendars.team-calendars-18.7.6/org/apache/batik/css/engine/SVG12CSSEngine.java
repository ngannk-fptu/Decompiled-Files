/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine;

import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.engine.value.svg.OpacityManager;
import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.css.engine.value.svg12.LineHeightManager;
import org.apache.batik.css.engine.value.svg12.MarginLengthManager;
import org.apache.batik.css.engine.value.svg12.MarginShorthandManager;
import org.apache.batik.css.engine.value.svg12.TextAlignManager;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;

public class SVG12CSSEngine
extends SVGCSSEngine {
    public static final ValueManager[] SVG_VALUE_MANAGERS = new ValueManager[]{new LineHeightManager(), new MarginLengthManager("indent"), new MarginLengthManager("margin-bottom"), new MarginLengthManager("margin-left"), new MarginLengthManager("margin-right"), new MarginLengthManager("margin-top"), new SVGColorManager("solid-color"), new OpacityManager("solid-opacity", true), new TextAlignManager()};
    public static final ShorthandManager[] SVG_SHORTHAND_MANAGERS = new ShorthandManager[]{new MarginShorthandManager()};
    public static final int LINE_HEIGHT_INDEX = 60;
    public static final int INDENT_INDEX = 61;
    public static final int MARGIN_BOTTOM_INDEX = 62;
    public static final int MARGIN_LEFT_INDEX = 63;
    public static final int MARGIN_RIGHT_INDEX = 64;
    public static final int MARGIN_TOP_INDEX = 65;
    public static final int SOLID_COLOR_INDEX = 66;
    public static final int SOLID_OPACITY_INDEX = 67;
    public static final int TEXT_ALIGN_INDEX = 68;
    public static final int FINAL_INDEX = 68;

    public SVG12CSSEngine(Document doc, ParsedURL uri, ExtendedParser p, CSSContext ctx) {
        super(doc, uri, p, SVG_VALUE_MANAGERS, SVG_SHORTHAND_MANAGERS, ctx);
        this.lineHeightIndex = 60;
    }

    public SVG12CSSEngine(Document doc, ParsedURL uri, ExtendedParser p, ValueManager[] vms, ShorthandManager[] sms, CSSContext ctx) {
        super(doc, uri, p, SVG12CSSEngine.mergeArrays(SVG_VALUE_MANAGERS, vms), SVG12CSSEngine.mergeArrays(SVG_SHORTHAND_MANAGERS, sms), ctx);
        this.lineHeightIndex = 60;
    }
}

