/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STIconSetType
extends XmlString {
    public static final SimpleTypeFactory<STIconSetType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sticonsettype6112type");
    public static final SchemaType type = Factory.getType();
    public static final Enum X_3_ARROWS = Enum.forString("3Arrows");
    public static final Enum X_3_ARROWS_GRAY = Enum.forString("3ArrowsGray");
    public static final Enum X_3_FLAGS = Enum.forString("3Flags");
    public static final Enum X_3_TRAFFIC_LIGHTS_1 = Enum.forString("3TrafficLights1");
    public static final Enum X_3_TRAFFIC_LIGHTS_2 = Enum.forString("3TrafficLights2");
    public static final Enum X_3_SIGNS = Enum.forString("3Signs");
    public static final Enum X_3_SYMBOLS = Enum.forString("3Symbols");
    public static final Enum X_3_SYMBOLS_2 = Enum.forString("3Symbols2");
    public static final Enum X_4_ARROWS = Enum.forString("4Arrows");
    public static final Enum X_4_ARROWS_GRAY = Enum.forString("4ArrowsGray");
    public static final Enum X_4_RED_TO_BLACK = Enum.forString("4RedToBlack");
    public static final Enum X_4_RATING = Enum.forString("4Rating");
    public static final Enum X_4_TRAFFIC_LIGHTS = Enum.forString("4TrafficLights");
    public static final Enum X_5_ARROWS = Enum.forString("5Arrows");
    public static final Enum X_5_ARROWS_GRAY = Enum.forString("5ArrowsGray");
    public static final Enum X_5_RATING = Enum.forString("5Rating");
    public static final Enum X_5_QUARTERS = Enum.forString("5Quarters");
    public static final int INT_X_3_ARROWS = 1;
    public static final int INT_X_3_ARROWS_GRAY = 2;
    public static final int INT_X_3_FLAGS = 3;
    public static final int INT_X_3_TRAFFIC_LIGHTS_1 = 4;
    public static final int INT_X_3_TRAFFIC_LIGHTS_2 = 5;
    public static final int INT_X_3_SIGNS = 6;
    public static final int INT_X_3_SYMBOLS = 7;
    public static final int INT_X_3_SYMBOLS_2 = 8;
    public static final int INT_X_4_ARROWS = 9;
    public static final int INT_X_4_ARROWS_GRAY = 10;
    public static final int INT_X_4_RED_TO_BLACK = 11;
    public static final int INT_X_4_RATING = 12;
    public static final int INT_X_4_TRAFFIC_LIGHTS = 13;
    public static final int INT_X_5_ARROWS = 14;
    public static final int INT_X_5_ARROWS_GRAY = 15;
    public static final int INT_X_5_RATING = 16;
    public static final int INT_X_5_QUARTERS = 17;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_X_3_ARROWS = 1;
        static final int INT_X_3_ARROWS_GRAY = 2;
        static final int INT_X_3_FLAGS = 3;
        static final int INT_X_3_TRAFFIC_LIGHTS_1 = 4;
        static final int INT_X_3_TRAFFIC_LIGHTS_2 = 5;
        static final int INT_X_3_SIGNS = 6;
        static final int INT_X_3_SYMBOLS = 7;
        static final int INT_X_3_SYMBOLS_2 = 8;
        static final int INT_X_4_ARROWS = 9;
        static final int INT_X_4_ARROWS_GRAY = 10;
        static final int INT_X_4_RED_TO_BLACK = 11;
        static final int INT_X_4_RATING = 12;
        static final int INT_X_4_TRAFFIC_LIGHTS = 13;
        static final int INT_X_5_ARROWS = 14;
        static final int INT_X_5_ARROWS_GRAY = 15;
        static final int INT_X_5_RATING = 16;
        static final int INT_X_5_QUARTERS = 17;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("3Arrows", 1), new Enum("3ArrowsGray", 2), new Enum("3Flags", 3), new Enum("3TrafficLights1", 4), new Enum("3TrafficLights2", 5), new Enum("3Signs", 6), new Enum("3Symbols", 7), new Enum("3Symbols2", 8), new Enum("4Arrows", 9), new Enum("4ArrowsGray", 10), new Enum("4RedToBlack", 11), new Enum("4Rating", 12), new Enum("4TrafficLights", 13), new Enum("5Arrows", 14), new Enum("5ArrowsGray", 15), new Enum("5Rating", 16), new Enum("5Quarters", 17)});
        private static final long serialVersionUID = 1L;

        public static Enum forString(String s) {
            return (Enum)table.forString(s);
        }

        public static Enum forInt(int i) {
            return (Enum)table.forInt(i);
        }

        private Enum(String s, int i) {
            super(s, i);
        }

        private Object readResolve() {
            return Enum.forInt(this.intValue());
        }
    }
}

