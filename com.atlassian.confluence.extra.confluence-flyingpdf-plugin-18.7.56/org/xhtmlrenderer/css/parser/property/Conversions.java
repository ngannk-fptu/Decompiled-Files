/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.HashMap;
import java.util.Map;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;

public class Conversions {
    private static final Map COLORS = new HashMap();
    private static final Map NUMERIC_FONT_WEIGHTS = new HashMap();
    private static final Map BORDER_WIDTHS = new HashMap();

    public static FSRGBColor getColor(String ident) {
        return (FSRGBColor)COLORS.get(ident);
    }

    public static IdentValue getNumericFontWeight(float weight) {
        return (IdentValue)NUMERIC_FONT_WEIGHTS.get(new Float(weight));
    }

    public static PropertyValue getBorderWidth(String ident) {
        return (PropertyValue)BORDER_WIDTHS.get(ident);
    }

    static {
        COLORS.put("cyan", new FSRGBColor(65535));
        COLORS.put("magenta", new FSRGBColor(0xFF00FF));
        COLORS.put("black", new FSRGBColor(0));
        COLORS.put("gray", new FSRGBColor(0x808080));
        COLORS.put("grey", new FSRGBColor(0x808080));
        COLORS.put("maroon", new FSRGBColor(0x800000));
        COLORS.put("red", new FSRGBColor(0xFF0000));
        COLORS.put("green", new FSRGBColor(32768));
        COLORS.put("lime", new FSRGBColor(65280));
        COLORS.put("olive", new FSRGBColor(0x808000));
        COLORS.put("yellow", new FSRGBColor(0xFFFF00));
        COLORS.put("navy", new FSRGBColor(128));
        COLORS.put("blue", new FSRGBColor(255));
        COLORS.put("purple", new FSRGBColor(0x800080));
        COLORS.put("fuchsia", new FSRGBColor(0xFF00FF));
        COLORS.put("teal", new FSRGBColor(32896));
        COLORS.put("aqua", new FSRGBColor(65535));
        COLORS.put("silver", new FSRGBColor(0xC0C0C0));
        COLORS.put("white", new FSRGBColor(0xFFFFFF));
        COLORS.put("aliceblue", new FSRGBColor(0xF0F8FF));
        COLORS.put("antiquewhite", new FSRGBColor(16444375));
        COLORS.put("aquamarine", new FSRGBColor(8388564));
        COLORS.put("azure", new FSRGBColor(0xF0FFFF));
        COLORS.put("beige", new FSRGBColor(16119260));
        COLORS.put("blueviolet", new FSRGBColor(9055202));
        COLORS.put("brown", new FSRGBColor(0xA52A2A));
        COLORS.put("burlywood", new FSRGBColor(14596231));
        COLORS.put("cadetblue", new FSRGBColor(6266528));
        COLORS.put("chartreuse", new FSRGBColor(0x7FFF00));
        COLORS.put("chocolate", new FSRGBColor(13789470));
        COLORS.put("coral", new FSRGBColor(16744272));
        COLORS.put("cornflowerblue", new FSRGBColor(6591981));
        COLORS.put("cornsilk", new FSRGBColor(16775388));
        COLORS.put("crimson", new FSRGBColor(14423100));
        COLORS.put("darkblue", new FSRGBColor(139));
        COLORS.put("darkcyan", new FSRGBColor(35723));
        COLORS.put("darkgoldenrod", new FSRGBColor(12092939));
        COLORS.put("darkgray", new FSRGBColor(0xA9A9A9));
        COLORS.put("darkgreen", new FSRGBColor(25600));
        COLORS.put("darkkhaki", new FSRGBColor(12433259));
        COLORS.put("darkmagenta", new FSRGBColor(0x8B008B));
        COLORS.put("darkolivegreen", new FSRGBColor(5597999));
        COLORS.put("darkorange", new FSRGBColor(16747520));
        COLORS.put("darkorchid", new FSRGBColor(10040012));
        COLORS.put("darkred", new FSRGBColor(0x8B0000));
        COLORS.put("darksalmon", new FSRGBColor(15308410));
        COLORS.put("darkseagreen", new FSRGBColor(9419919));
        COLORS.put("darkslateblue", new FSRGBColor(4734347));
        COLORS.put("darkslategray", new FSRGBColor(0x2F4F4F));
        COLORS.put("darkturquoise", new FSRGBColor(52945));
        COLORS.put("darkviolet", new FSRGBColor(9699539));
        COLORS.put("deeppink", new FSRGBColor(16716947));
        COLORS.put("deepskyblue", new FSRGBColor(49151));
        COLORS.put("dimgray", new FSRGBColor(0x696969));
        COLORS.put("dodgerblue", new FSRGBColor(2003199));
        COLORS.put("firebrick", new FSRGBColor(0xB22222));
        COLORS.put("floralwhite", new FSRGBColor(0xFFFAF0));
        COLORS.put("forestgreen", new FSRGBColor(0x228B22));
        COLORS.put("gainsboro", new FSRGBColor(0xDCDCDC));
        COLORS.put("ghostwhite", new FSRGBColor(0xF8F8FF));
        COLORS.put("gold", new FSRGBColor(16766720));
        COLORS.put("goldenrod", new FSRGBColor(14329120));
        COLORS.put("greenyellow", new FSRGBColor(11403055));
        COLORS.put("honeydew", new FSRGBColor(0xF0FFF0));
        COLORS.put("hotpink", new FSRGBColor(16738740));
        COLORS.put("indianred", new FSRGBColor(0xCD5C5C));
        COLORS.put("indigo", new FSRGBColor(4915330));
        COLORS.put("ivory", new FSRGBColor(0xFFFFF0));
        COLORS.put("khaki", new FSRGBColor(15787660));
        COLORS.put("lavender", new FSRGBColor(15132410));
        COLORS.put("lavenderblush", new FSRGBColor(0xFFF0F5));
        COLORS.put("lawngreen", new FSRGBColor(8190976));
        COLORS.put("lemonchiffon", new FSRGBColor(16775885));
        COLORS.put("lightblue", new FSRGBColor(11393254));
        COLORS.put("lightcoral", new FSRGBColor(0xF08080));
        COLORS.put("lightcyan", new FSRGBColor(0xE0FFFF));
        COLORS.put("lightgoldenrodyellow", new FSRGBColor(16448210));
        COLORS.put("lightgreen", new FSRGBColor(0x90EE90));
        COLORS.put("lightgrey", new FSRGBColor(0xD3D3D3));
        COLORS.put("lightpink", new FSRGBColor(16758465));
        COLORS.put("lightsalmon", new FSRGBColor(16752762));
        COLORS.put("lightseagreen", new FSRGBColor(2142890));
        COLORS.put("lightskyblue", new FSRGBColor(8900346));
        COLORS.put("lightslategray", new FSRGBColor(0x778899));
        COLORS.put("lightsteelblue", new FSRGBColor(11584734));
        COLORS.put("lightyellow", new FSRGBColor(0xFFFFE0));
        COLORS.put("limegreen", new FSRGBColor(3329330));
        COLORS.put("linen", new FSRGBColor(16445670));
        COLORS.put("mediumaquamarine", new FSRGBColor(6737322));
        COLORS.put("mediumblue", new FSRGBColor(205));
        COLORS.put("mediumorchid", new FSRGBColor(12211667));
        COLORS.put("mediumpurple", new FSRGBColor(9662683));
        COLORS.put("mediumseagreen", new FSRGBColor(3978097));
        COLORS.put("mediumslateblue", new FSRGBColor(8087790));
        COLORS.put("mediumspringgreen", new FSRGBColor(64154));
        COLORS.put("mediumturquoise", new FSRGBColor(4772300));
        COLORS.put("mediumvioletred", new FSRGBColor(13047173));
        COLORS.put("midnightblue", new FSRGBColor(1644912));
        COLORS.put("mintcream", new FSRGBColor(0xF5FFFA));
        COLORS.put("mistyrose", new FSRGBColor(16770273));
        COLORS.put("moccasin", new FSRGBColor(16770229));
        COLORS.put("navajowhite", new FSRGBColor(16768685));
        COLORS.put("oldlace", new FSRGBColor(16643558));
        COLORS.put("olivedrab", new FSRGBColor(7048739));
        COLORS.put("orange", new FSRGBColor(16753920));
        COLORS.put("orangered", new FSRGBColor(16729344));
        COLORS.put("orchid", new FSRGBColor(14315734));
        COLORS.put("palegoldenrod", new FSRGBColor(0xEEE8AA));
        COLORS.put("palegreen", new FSRGBColor(10025880));
        COLORS.put("paleturquoise", new FSRGBColor(0xAFEEEE));
        COLORS.put("palevioletred", new FSRGBColor(14381203));
        COLORS.put("papayawhip", new FSRGBColor(16773077));
        COLORS.put("peachpuff", new FSRGBColor(16767673));
        COLORS.put("peru", new FSRGBColor(13468991));
        COLORS.put("pink", new FSRGBColor(16761035));
        COLORS.put("plum", new FSRGBColor(0xDDA0DD));
        COLORS.put("powderblue", new FSRGBColor(11591910));
        COLORS.put("rosybrown", new FSRGBColor(12357519));
        COLORS.put("royalblue", new FSRGBColor(4286945));
        COLORS.put("saddlebrown", new FSRGBColor(9127187));
        COLORS.put("salmon", new FSRGBColor(16416882));
        COLORS.put("sandybrown", new FSRGBColor(16032864));
        COLORS.put("seagreen", new FSRGBColor(3050327));
        COLORS.put("seashell", new FSRGBColor(0xFFF5EE));
        COLORS.put("sienna", new FSRGBColor(10506797));
        COLORS.put("skyblue", new FSRGBColor(8900331));
        COLORS.put("slateblue", new FSRGBColor(6970061));
        COLORS.put("slategray", new FSRGBColor(7372944));
        COLORS.put("snow", new FSRGBColor(0xFFFAFA));
        COLORS.put("springgreen", new FSRGBColor(65407));
        COLORS.put("steelblue", new FSRGBColor(4620980));
        COLORS.put("tan", new FSRGBColor(13808780));
        COLORS.put("thistle", new FSRGBColor(14204888));
        COLORS.put("tomato", new FSRGBColor(16737095));
        COLORS.put("turquoise", new FSRGBColor(4251856));
        COLORS.put("violet", new FSRGBColor(976942));
        COLORS.put("wheat", new FSRGBColor(16113331));
        COLORS.put("whitesmoke", new FSRGBColor(0xF5F5F5));
        COLORS.put("yellowgreen", new FSRGBColor(10145074));
        NUMERIC_FONT_WEIGHTS.put(new Float(100.0f), IdentValue.FONT_WEIGHT_100);
        NUMERIC_FONT_WEIGHTS.put(new Float(200.0f), IdentValue.FONT_WEIGHT_200);
        NUMERIC_FONT_WEIGHTS.put(new Float(300.0f), IdentValue.FONT_WEIGHT_300);
        NUMERIC_FONT_WEIGHTS.put(new Float(400.0f), IdentValue.FONT_WEIGHT_400);
        NUMERIC_FONT_WEIGHTS.put(new Float(500.0f), IdentValue.FONT_WEIGHT_500);
        NUMERIC_FONT_WEIGHTS.put(new Float(600.0f), IdentValue.FONT_WEIGHT_600);
        NUMERIC_FONT_WEIGHTS.put(new Float(700.0f), IdentValue.FONT_WEIGHT_700);
        NUMERIC_FONT_WEIGHTS.put(new Float(800.0f), IdentValue.FONT_WEIGHT_800);
        NUMERIC_FONT_WEIGHTS.put(new Float(900.0f), IdentValue.FONT_WEIGHT_900);
        BORDER_WIDTHS.put("thin", new PropertyValue(5, 1.0f, "1px"));
        BORDER_WIDTHS.put("medium", new PropertyValue(5, 2.0f, "2px"));
        BORDER_WIDTHS.put("thick", new PropertyValue(5, 3.0f, "3px"));
    }
}

