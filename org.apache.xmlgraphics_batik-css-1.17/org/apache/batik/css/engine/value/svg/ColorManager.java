/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.AbstractColorManager;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGValueConstants;

public class ColorManager
extends AbstractColorManager {
    protected static final Value DEFAULT_VALUE = SVGValueConstants.BLACK_RGB_VALUE;

    @Override
    public boolean isInheritedProperty() {
        return true;
    }

    @Override
    public boolean isAnimatableProperty() {
        return true;
    }

    @Override
    public boolean isAdditiveProperty() {
        return true;
    }

    @Override
    public int getPropertyType() {
        return 6;
    }

    @Override
    public String getPropertyName() {
        return "color";
    }

    @Override
    public Value getDefaultValue() {
        return DEFAULT_VALUE;
    }

    static {
        values.put("aliceblue", SVGValueConstants.ALICEBLUE_VALUE);
        values.put("antiquewhite", SVGValueConstants.ANTIQUEWHITE_VALUE);
        values.put("aquamarine", SVGValueConstants.AQUAMARINE_VALUE);
        values.put("azure", SVGValueConstants.AZURE_VALUE);
        values.put("beige", SVGValueConstants.BEIGE_VALUE);
        values.put("bisque", SVGValueConstants.BISQUE_VALUE);
        values.put("blanchedalmond", SVGValueConstants.BLANCHEDALMOND_VALUE);
        values.put("blueviolet", SVGValueConstants.BLUEVIOLET_VALUE);
        values.put("brown", SVGValueConstants.BROWN_VALUE);
        values.put("burlywood", SVGValueConstants.BURLYWOOD_VALUE);
        values.put("cadetblue", SVGValueConstants.CADETBLUE_VALUE);
        values.put("chartreuse", SVGValueConstants.CHARTREUSE_VALUE);
        values.put("chocolate", SVGValueConstants.CHOCOLATE_VALUE);
        values.put("coral", SVGValueConstants.CORAL_VALUE);
        values.put("cornflowerblue", SVGValueConstants.CORNFLOWERBLUE_VALUE);
        values.put("cornsilk", SVGValueConstants.CORNSILK_VALUE);
        values.put("crimson", SVGValueConstants.CRIMSON_VALUE);
        values.put("cyan", SVGValueConstants.CYAN_VALUE);
        values.put("darkblue", SVGValueConstants.DARKBLUE_VALUE);
        values.put("darkcyan", SVGValueConstants.DARKCYAN_VALUE);
        values.put("darkgoldenrod", SVGValueConstants.DARKGOLDENROD_VALUE);
        values.put("darkgray", SVGValueConstants.DARKGRAY_VALUE);
        values.put("darkgreen", SVGValueConstants.DARKGREEN_VALUE);
        values.put("darkgrey", SVGValueConstants.DARKGREY_VALUE);
        values.put("darkkhaki", SVGValueConstants.DARKKHAKI_VALUE);
        values.put("darkmagenta", SVGValueConstants.DARKMAGENTA_VALUE);
        values.put("darkolivegreen", SVGValueConstants.DARKOLIVEGREEN_VALUE);
        values.put("darkorange", SVGValueConstants.DARKORANGE_VALUE);
        values.put("darkorchid", SVGValueConstants.DARKORCHID_VALUE);
        values.put("darkred", SVGValueConstants.DARKRED_VALUE);
        values.put("darksalmon", SVGValueConstants.DARKSALMON_VALUE);
        values.put("darkseagreen", SVGValueConstants.DARKSEAGREEN_VALUE);
        values.put("darkslateblue", SVGValueConstants.DARKSLATEBLUE_VALUE);
        values.put("darkslategray", SVGValueConstants.DARKSLATEGRAY_VALUE);
        values.put("darkslategrey", SVGValueConstants.DARKSLATEGREY_VALUE);
        values.put("darkturquoise", SVGValueConstants.DARKTURQUOISE_VALUE);
        values.put("darkviolet", SVGValueConstants.DARKVIOLET_VALUE);
        values.put("deeppink", SVGValueConstants.DEEPPINK_VALUE);
        values.put("deepskyblue", SVGValueConstants.DEEPSKYBLUE_VALUE);
        values.put("dimgray", SVGValueConstants.DIMGRAY_VALUE);
        values.put("dimgrey", SVGValueConstants.DIMGREY_VALUE);
        values.put("dodgerblue", SVGValueConstants.DODGERBLUE_VALUE);
        values.put("firebrick", SVGValueConstants.FIREBRICK_VALUE);
        values.put("floralwhite", SVGValueConstants.FLORALWHITE_VALUE);
        values.put("forestgreen", SVGValueConstants.FORESTGREEN_VALUE);
        values.put("gainsboro", SVGValueConstants.GAINSBORO_VALUE);
        values.put("ghostwhite", SVGValueConstants.GHOSTWHITE_VALUE);
        values.put("gold", SVGValueConstants.GOLD_VALUE);
        values.put("goldenrod", SVGValueConstants.GOLDENROD_VALUE);
        values.put("greenyellow", SVGValueConstants.GREENYELLOW_VALUE);
        values.put("grey", SVGValueConstants.GREY_VALUE);
        values.put("honeydew", SVGValueConstants.HONEYDEW_VALUE);
        values.put("hotpink", SVGValueConstants.HOTPINK_VALUE);
        values.put("indianred", SVGValueConstants.INDIANRED_VALUE);
        values.put("indigo", SVGValueConstants.INDIGO_VALUE);
        values.put("ivory", SVGValueConstants.IVORY_VALUE);
        values.put("khaki", SVGValueConstants.KHAKI_VALUE);
        values.put("lavender", SVGValueConstants.LAVENDER_VALUE);
        values.put("lavenderblush", SVGValueConstants.LAVENDERBLUSH_VALUE);
        values.put("lawngreen", SVGValueConstants.LAWNGREEN_VALUE);
        values.put("lemonchiffon", SVGValueConstants.LEMONCHIFFON_VALUE);
        values.put("lightblue", SVGValueConstants.LIGHTBLUE_VALUE);
        values.put("lightcoral", SVGValueConstants.LIGHTCORAL_VALUE);
        values.put("lightcyan", SVGValueConstants.LIGHTCYAN_VALUE);
        values.put("lightgoldenrodyellow", SVGValueConstants.LIGHTGOLDENRODYELLOW_VALUE);
        values.put("lightgray", SVGValueConstants.LIGHTGRAY_VALUE);
        values.put("lightgreen", SVGValueConstants.LIGHTGREEN_VALUE);
        values.put("lightgrey", SVGValueConstants.LIGHTGREY_VALUE);
        values.put("lightpink", SVGValueConstants.LIGHTPINK_VALUE);
        values.put("lightsalmon", SVGValueConstants.LIGHTSALMON_VALUE);
        values.put("lightseagreen", SVGValueConstants.LIGHTSEAGREEN_VALUE);
        values.put("lightskyblue", SVGValueConstants.LIGHTSKYBLUE_VALUE);
        values.put("lightslategray", SVGValueConstants.LIGHTSLATEGRAY_VALUE);
        values.put("lightslategrey", SVGValueConstants.LIGHTSLATEGREY_VALUE);
        values.put("lightsteelblue", SVGValueConstants.LIGHTSTEELBLUE_VALUE);
        values.put("lightyellow", SVGValueConstants.LIGHTYELLOW_VALUE);
        values.put("limegreen", SVGValueConstants.LIMEGREEN_VALUE);
        values.put("linen", SVGValueConstants.LINEN_VALUE);
        values.put("magenta", SVGValueConstants.MAGENTA_VALUE);
        values.put("mediumaquamarine", SVGValueConstants.MEDIUMAQUAMARINE_VALUE);
        values.put("mediumblue", SVGValueConstants.MEDIUMBLUE_VALUE);
        values.put("mediumorchid", SVGValueConstants.MEDIUMORCHID_VALUE);
        values.put("mediumpurple", SVGValueConstants.MEDIUMPURPLE_VALUE);
        values.put("mediumseagreen", SVGValueConstants.MEDIUMSEAGREEN_VALUE);
        values.put("mediumslateblue", SVGValueConstants.MEDIUMSLATEBLUE_VALUE);
        values.put("mediumspringgreen", SVGValueConstants.MEDIUMSPRINGGREEN_VALUE);
        values.put("mediumturquoise", SVGValueConstants.MEDIUMTURQUOISE_VALUE);
        values.put("mediumvioletred", SVGValueConstants.MEDIUMVIOLETRED_VALUE);
        values.put("midnightblue", SVGValueConstants.MIDNIGHTBLUE_VALUE);
        values.put("mintcream", SVGValueConstants.MINTCREAM_VALUE);
        values.put("mistyrose", SVGValueConstants.MISTYROSE_VALUE);
        values.put("moccasin", SVGValueConstants.MOCCASIN_VALUE);
        values.put("navajowhite", SVGValueConstants.NAVAJOWHITE_VALUE);
        values.put("oldlace", SVGValueConstants.OLDLACE_VALUE);
        values.put("olivedrab", SVGValueConstants.OLIVEDRAB_VALUE);
        values.put("orange", SVGValueConstants.ORANGE_VALUE);
        values.put("orangered", SVGValueConstants.ORANGERED_VALUE);
        values.put("orchid", SVGValueConstants.ORCHID_VALUE);
        values.put("palegoldenrod", SVGValueConstants.PALEGOLDENROD_VALUE);
        values.put("palegreen", SVGValueConstants.PALEGREEN_VALUE);
        values.put("paleturquoise", SVGValueConstants.PALETURQUOISE_VALUE);
        values.put("palevioletred", SVGValueConstants.PALEVIOLETRED_VALUE);
        values.put("papayawhip", SVGValueConstants.PAPAYAWHIP_VALUE);
        values.put("peachpuff", SVGValueConstants.PEACHPUFF_VALUE);
        values.put("peru", SVGValueConstants.PERU_VALUE);
        values.put("pink", SVGValueConstants.PINK_VALUE);
        values.put("plum", SVGValueConstants.PLUM_VALUE);
        values.put("powderblue", SVGValueConstants.POWDERBLUE_VALUE);
        values.put("purple", SVGValueConstants.PURPLE_VALUE);
        values.put("rosybrown", SVGValueConstants.ROSYBROWN_VALUE);
        values.put("royalblue", SVGValueConstants.ROYALBLUE_VALUE);
        values.put("saddlebrown", SVGValueConstants.SADDLEBROWN_VALUE);
        values.put("salmon", SVGValueConstants.SALMON_VALUE);
        values.put("sandybrown", SVGValueConstants.SANDYBROWN_VALUE);
        values.put("seagreen", SVGValueConstants.SEAGREEN_VALUE);
        values.put("seashell", SVGValueConstants.SEASHELL_VALUE);
        values.put("sienna", SVGValueConstants.SIENNA_VALUE);
        values.put("skyblue", SVGValueConstants.SKYBLUE_VALUE);
        values.put("slateblue", SVGValueConstants.SLATEBLUE_VALUE);
        values.put("slategray", SVGValueConstants.SLATEGRAY_VALUE);
        values.put("slategrey", SVGValueConstants.SLATEGREY_VALUE);
        values.put("snow", SVGValueConstants.SNOW_VALUE);
        values.put("springgreen", SVGValueConstants.SPRINGGREEN_VALUE);
        values.put("steelblue", SVGValueConstants.STEELBLUE_VALUE);
        values.put("tan", SVGValueConstants.TAN_VALUE);
        values.put("thistle", SVGValueConstants.THISTLE_VALUE);
        values.put("tomato", SVGValueConstants.TOMATO_VALUE);
        values.put("turquoise", SVGValueConstants.TURQUOISE_VALUE);
        values.put("violet", SVGValueConstants.VIOLET_VALUE);
        values.put("wheat", SVGValueConstants.WHEAT_VALUE);
        values.put("whitesmoke", SVGValueConstants.WHITESMOKE_VALUE);
        values.put("yellowgreen", SVGValueConstants.YELLOWGREEN_VALUE);
        computedValues.put("black", SVGValueConstants.BLACK_RGB_VALUE);
        computedValues.put("silver", SVGValueConstants.SILVER_RGB_VALUE);
        computedValues.put("gray", SVGValueConstants.GRAY_RGB_VALUE);
        computedValues.put("white", SVGValueConstants.WHITE_RGB_VALUE);
        computedValues.put("maroon", SVGValueConstants.MAROON_RGB_VALUE);
        computedValues.put("red", SVGValueConstants.RED_RGB_VALUE);
        computedValues.put("purple", SVGValueConstants.PURPLE_RGB_VALUE);
        computedValues.put("fuchsia", SVGValueConstants.FUCHSIA_RGB_VALUE);
        computedValues.put("green", SVGValueConstants.GREEN_RGB_VALUE);
        computedValues.put("lime", SVGValueConstants.LIME_RGB_VALUE);
        computedValues.put("olive", SVGValueConstants.OLIVE_RGB_VALUE);
        computedValues.put("yellow", SVGValueConstants.YELLOW_RGB_VALUE);
        computedValues.put("navy", SVGValueConstants.NAVY_RGB_VALUE);
        computedValues.put("blue", SVGValueConstants.BLUE_RGB_VALUE);
        computedValues.put("teal", SVGValueConstants.TEAL_RGB_VALUE);
        computedValues.put("aqua", SVGValueConstants.AQUA_RGB_VALUE);
        computedValues.put("aliceblue", SVGValueConstants.ALICEBLUE_RGB_VALUE);
        computedValues.put("antiquewhite", SVGValueConstants.ANTIQUEWHITE_RGB_VALUE);
        computedValues.put("aquamarine", SVGValueConstants.AQUAMARINE_RGB_VALUE);
        computedValues.put("azure", SVGValueConstants.AZURE_RGB_VALUE);
        computedValues.put("beige", SVGValueConstants.BEIGE_RGB_VALUE);
        computedValues.put("bisque", SVGValueConstants.BISQUE_RGB_VALUE);
        computedValues.put("blanchedalmond", SVGValueConstants.BLANCHEDALMOND_RGB_VALUE);
        computedValues.put("blueviolet", SVGValueConstants.BLUEVIOLET_RGB_VALUE);
        computedValues.put("brown", SVGValueConstants.BROWN_RGB_VALUE);
        computedValues.put("burlywood", SVGValueConstants.BURLYWOOD_RGB_VALUE);
        computedValues.put("cadetblue", SVGValueConstants.CADETBLUE_RGB_VALUE);
        computedValues.put("chartreuse", SVGValueConstants.CHARTREUSE_RGB_VALUE);
        computedValues.put("chocolate", SVGValueConstants.CHOCOLATE_RGB_VALUE);
        computedValues.put("coral", SVGValueConstants.CORAL_RGB_VALUE);
        computedValues.put("cornflowerblue", SVGValueConstants.CORNFLOWERBLUE_RGB_VALUE);
        computedValues.put("cornsilk", SVGValueConstants.CORNSILK_RGB_VALUE);
        computedValues.put("crimson", SVGValueConstants.CRIMSON_RGB_VALUE);
        computedValues.put("cyan", SVGValueConstants.CYAN_RGB_VALUE);
        computedValues.put("darkblue", SVGValueConstants.DARKBLUE_RGB_VALUE);
        computedValues.put("darkcyan", SVGValueConstants.DARKCYAN_RGB_VALUE);
        computedValues.put("darkgoldenrod", SVGValueConstants.DARKGOLDENROD_RGB_VALUE);
        computedValues.put("darkgray", SVGValueConstants.DARKGRAY_RGB_VALUE);
        computedValues.put("darkgreen", SVGValueConstants.DARKGREEN_RGB_VALUE);
        computedValues.put("darkgrey", SVGValueConstants.DARKGREY_RGB_VALUE);
        computedValues.put("darkkhaki", SVGValueConstants.DARKKHAKI_RGB_VALUE);
        computedValues.put("darkmagenta", SVGValueConstants.DARKMAGENTA_RGB_VALUE);
        computedValues.put("darkolivegreen", SVGValueConstants.DARKOLIVEGREEN_RGB_VALUE);
        computedValues.put("darkorange", SVGValueConstants.DARKORANGE_RGB_VALUE);
        computedValues.put("darkorchid", SVGValueConstants.DARKORCHID_RGB_VALUE);
        computedValues.put("darkred", SVGValueConstants.DARKRED_RGB_VALUE);
        computedValues.put("darksalmon", SVGValueConstants.DARKSALMON_RGB_VALUE);
        computedValues.put("darkseagreen", SVGValueConstants.DARKSEAGREEN_RGB_VALUE);
        computedValues.put("darkslateblue", SVGValueConstants.DARKSLATEBLUE_RGB_VALUE);
        computedValues.put("darkslategray", SVGValueConstants.DARKSLATEGRAY_RGB_VALUE);
        computedValues.put("darkslategrey", SVGValueConstants.DARKSLATEGREY_RGB_VALUE);
        computedValues.put("darkturquoise", SVGValueConstants.DARKTURQUOISE_RGB_VALUE);
        computedValues.put("darkviolet", SVGValueConstants.DARKVIOLET_RGB_VALUE);
        computedValues.put("deeppink", SVGValueConstants.DEEPPINK_RGB_VALUE);
        computedValues.put("deepskyblue", SVGValueConstants.DEEPSKYBLUE_RGB_VALUE);
        computedValues.put("dimgray", SVGValueConstants.DIMGRAY_RGB_VALUE);
        computedValues.put("dimgrey", SVGValueConstants.DIMGREY_RGB_VALUE);
        computedValues.put("dodgerblue", SVGValueConstants.DODGERBLUE_RGB_VALUE);
        computedValues.put("firebrick", SVGValueConstants.FIREBRICK_RGB_VALUE);
        computedValues.put("floralwhite", SVGValueConstants.FLORALWHITE_RGB_VALUE);
        computedValues.put("forestgreen", SVGValueConstants.FORESTGREEN_RGB_VALUE);
        computedValues.put("gainsboro", SVGValueConstants.GAINSBORO_RGB_VALUE);
        computedValues.put("ghostwhite", SVGValueConstants.GHOSTWHITE_RGB_VALUE);
        computedValues.put("gold", SVGValueConstants.GOLD_RGB_VALUE);
        computedValues.put("goldenrod", SVGValueConstants.GOLDENROD_RGB_VALUE);
        computedValues.put("grey", SVGValueConstants.GREY_RGB_VALUE);
        computedValues.put("greenyellow", SVGValueConstants.GREENYELLOW_RGB_VALUE);
        computedValues.put("honeydew", SVGValueConstants.HONEYDEW_RGB_VALUE);
        computedValues.put("hotpink", SVGValueConstants.HOTPINK_RGB_VALUE);
        computedValues.put("indianred", SVGValueConstants.INDIANRED_RGB_VALUE);
        computedValues.put("indigo", SVGValueConstants.INDIGO_RGB_VALUE);
        computedValues.put("ivory", SVGValueConstants.IVORY_RGB_VALUE);
        computedValues.put("khaki", SVGValueConstants.KHAKI_RGB_VALUE);
        computedValues.put("lavender", SVGValueConstants.LAVENDER_RGB_VALUE);
        computedValues.put("lavenderblush", SVGValueConstants.LAVENDERBLUSH_RGB_VALUE);
        computedValues.put("lawngreen", SVGValueConstants.LAWNGREEN_RGB_VALUE);
        computedValues.put("lemonchiffon", SVGValueConstants.LEMONCHIFFON_RGB_VALUE);
        computedValues.put("lightblue", SVGValueConstants.LIGHTBLUE_RGB_VALUE);
        computedValues.put("lightcoral", SVGValueConstants.LIGHTCORAL_RGB_VALUE);
        computedValues.put("lightcyan", SVGValueConstants.LIGHTCYAN_RGB_VALUE);
        computedValues.put("lightgoldenrodyellow", SVGValueConstants.LIGHTGOLDENRODYELLOW_RGB_VALUE);
        computedValues.put("lightgray", SVGValueConstants.LIGHTGRAY_RGB_VALUE);
        computedValues.put("lightgreen", SVGValueConstants.LIGHTGREEN_RGB_VALUE);
        computedValues.put("lightgrey", SVGValueConstants.LIGHTGREY_RGB_VALUE);
        computedValues.put("lightpink", SVGValueConstants.LIGHTPINK_RGB_VALUE);
        computedValues.put("lightsalmon", SVGValueConstants.LIGHTSALMON_RGB_VALUE);
        computedValues.put("lightseagreen", SVGValueConstants.LIGHTSEAGREEN_RGB_VALUE);
        computedValues.put("lightskyblue", SVGValueConstants.LIGHTSKYBLUE_RGB_VALUE);
        computedValues.put("lightslategray", SVGValueConstants.LIGHTSLATEGRAY_RGB_VALUE);
        computedValues.put("lightslategrey", SVGValueConstants.LIGHTSLATEGREY_RGB_VALUE);
        computedValues.put("lightsteelblue", SVGValueConstants.LIGHTSTEELBLUE_RGB_VALUE);
        computedValues.put("lightyellow", SVGValueConstants.LIGHTYELLOW_RGB_VALUE);
        computedValues.put("limegreen", SVGValueConstants.LIMEGREEN_RGB_VALUE);
        computedValues.put("linen", SVGValueConstants.LINEN_RGB_VALUE);
        computedValues.put("magenta", SVGValueConstants.MAGENTA_RGB_VALUE);
        computedValues.put("mediumaquamarine", SVGValueConstants.MEDIUMAQUAMARINE_RGB_VALUE);
        computedValues.put("mediumblue", SVGValueConstants.MEDIUMBLUE_RGB_VALUE);
        computedValues.put("mediumorchid", SVGValueConstants.MEDIUMORCHID_RGB_VALUE);
        computedValues.put("mediumpurple", SVGValueConstants.MEDIUMPURPLE_RGB_VALUE);
        computedValues.put("mediumseagreen", SVGValueConstants.MEDIUMSEAGREEN_RGB_VALUE);
        computedValues.put("mediumslateblue", SVGValueConstants.MEDIUMSLATEBLUE_RGB_VALUE);
        computedValues.put("mediumspringgreen", SVGValueConstants.MEDIUMSPRINGGREEN_RGB_VALUE);
        computedValues.put("mediumturquoise", SVGValueConstants.MEDIUMTURQUOISE_RGB_VALUE);
        computedValues.put("mediumvioletred", SVGValueConstants.MEDIUMVIOLETRED_RGB_VALUE);
        computedValues.put("midnightblue", SVGValueConstants.MIDNIGHTBLUE_RGB_VALUE);
        computedValues.put("mintcream", SVGValueConstants.MINTCREAM_RGB_VALUE);
        computedValues.put("mistyrose", SVGValueConstants.MISTYROSE_RGB_VALUE);
        computedValues.put("moccasin", SVGValueConstants.MOCCASIN_RGB_VALUE);
        computedValues.put("navajowhite", SVGValueConstants.NAVAJOWHITE_RGB_VALUE);
        computedValues.put("oldlace", SVGValueConstants.OLDLACE_RGB_VALUE);
        computedValues.put("olivedrab", SVGValueConstants.OLIVEDRAB_RGB_VALUE);
        computedValues.put("orange", SVGValueConstants.ORANGE_RGB_VALUE);
        computedValues.put("orangered", SVGValueConstants.ORANGERED_RGB_VALUE);
        computedValues.put("orchid", SVGValueConstants.ORCHID_RGB_VALUE);
        computedValues.put("palegoldenrod", SVGValueConstants.PALEGOLDENROD_RGB_VALUE);
        computedValues.put("palegreen", SVGValueConstants.PALEGREEN_RGB_VALUE);
        computedValues.put("paleturquoise", SVGValueConstants.PALETURQUOISE_RGB_VALUE);
        computedValues.put("palevioletred", SVGValueConstants.PALEVIOLETRED_RGB_VALUE);
        computedValues.put("papayawhip", SVGValueConstants.PAPAYAWHIP_RGB_VALUE);
        computedValues.put("peachpuff", SVGValueConstants.PEACHPUFF_RGB_VALUE);
        computedValues.put("peru", SVGValueConstants.PERU_RGB_VALUE);
        computedValues.put("pink", SVGValueConstants.PINK_RGB_VALUE);
        computedValues.put("plum", SVGValueConstants.PLUM_RGB_VALUE);
        computedValues.put("powderblue", SVGValueConstants.POWDERBLUE_RGB_VALUE);
        computedValues.put("purple", SVGValueConstants.PURPLE_RGB_VALUE);
        computedValues.put("rosybrown", SVGValueConstants.ROSYBROWN_RGB_VALUE);
        computedValues.put("royalblue", SVGValueConstants.ROYALBLUE_RGB_VALUE);
        computedValues.put("saddlebrown", SVGValueConstants.SADDLEBROWN_RGB_VALUE);
        computedValues.put("salmon", SVGValueConstants.SALMON_RGB_VALUE);
        computedValues.put("sandybrown", SVGValueConstants.SANDYBROWN_RGB_VALUE);
        computedValues.put("seagreen", SVGValueConstants.SEAGREEN_RGB_VALUE);
        computedValues.put("seashell", SVGValueConstants.SEASHELL_RGB_VALUE);
        computedValues.put("sienna", SVGValueConstants.SIENNA_RGB_VALUE);
        computedValues.put("skyblue", SVGValueConstants.SKYBLUE_RGB_VALUE);
        computedValues.put("slateblue", SVGValueConstants.SLATEBLUE_RGB_VALUE);
        computedValues.put("slategray", SVGValueConstants.SLATEGRAY_RGB_VALUE);
        computedValues.put("slategrey", SVGValueConstants.SLATEGREY_RGB_VALUE);
        computedValues.put("snow", SVGValueConstants.SNOW_RGB_VALUE);
        computedValues.put("springgreen", SVGValueConstants.SPRINGGREEN_RGB_VALUE);
        computedValues.put("steelblue", SVGValueConstants.STEELBLUE_RGB_VALUE);
        computedValues.put("tan", SVGValueConstants.TAN_RGB_VALUE);
        computedValues.put("thistle", SVGValueConstants.THISTLE_RGB_VALUE);
        computedValues.put("tomato", SVGValueConstants.TOMATO_RGB_VALUE);
        computedValues.put("turquoise", SVGValueConstants.TURQUOISE_RGB_VALUE);
        computedValues.put("violet", SVGValueConstants.VIOLET_RGB_VALUE);
        computedValues.put("wheat", SVGValueConstants.WHEAT_RGB_VALUE);
        computedValues.put("whitesmoke", SVGValueConstants.WHITESMOKE_RGB_VALUE);
        computedValues.put("yellowgreen", SVGValueConstants.YELLOWGREEN_RGB_VALUE);
    }
}

