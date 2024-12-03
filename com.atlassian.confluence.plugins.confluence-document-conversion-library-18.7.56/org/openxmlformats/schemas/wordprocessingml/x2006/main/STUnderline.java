/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STUnderline
extends XmlString {
    public static final SimpleTypeFactory<STUnderline> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stunderlinef416type");
    public static final SchemaType type = Factory.getType();
    public static final Enum SINGLE = Enum.forString("single");
    public static final Enum WORDS = Enum.forString("words");
    public static final Enum DOUBLE = Enum.forString("double");
    public static final Enum THICK = Enum.forString("thick");
    public static final Enum DOTTED = Enum.forString("dotted");
    public static final Enum DOTTED_HEAVY = Enum.forString("dottedHeavy");
    public static final Enum DASH = Enum.forString("dash");
    public static final Enum DASHED_HEAVY = Enum.forString("dashedHeavy");
    public static final Enum DASH_LONG = Enum.forString("dashLong");
    public static final Enum DASH_LONG_HEAVY = Enum.forString("dashLongHeavy");
    public static final Enum DOT_DASH = Enum.forString("dotDash");
    public static final Enum DASH_DOT_HEAVY = Enum.forString("dashDotHeavy");
    public static final Enum DOT_DOT_DASH = Enum.forString("dotDotDash");
    public static final Enum DASH_DOT_DOT_HEAVY = Enum.forString("dashDotDotHeavy");
    public static final Enum WAVE = Enum.forString("wave");
    public static final Enum WAVY_HEAVY = Enum.forString("wavyHeavy");
    public static final Enum WAVY_DOUBLE = Enum.forString("wavyDouble");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_SINGLE = 1;
    public static final int INT_WORDS = 2;
    public static final int INT_DOUBLE = 3;
    public static final int INT_THICK = 4;
    public static final int INT_DOTTED = 5;
    public static final int INT_DOTTED_HEAVY = 6;
    public static final int INT_DASH = 7;
    public static final int INT_DASHED_HEAVY = 8;
    public static final int INT_DASH_LONG = 9;
    public static final int INT_DASH_LONG_HEAVY = 10;
    public static final int INT_DOT_DASH = 11;
    public static final int INT_DASH_DOT_HEAVY = 12;
    public static final int INT_DOT_DOT_DASH = 13;
    public static final int INT_DASH_DOT_DOT_HEAVY = 14;
    public static final int INT_WAVE = 15;
    public static final int INT_WAVY_HEAVY = 16;
    public static final int INT_WAVY_DOUBLE = 17;
    public static final int INT_NONE = 18;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_SINGLE = 1;
        static final int INT_WORDS = 2;
        static final int INT_DOUBLE = 3;
        static final int INT_THICK = 4;
        static final int INT_DOTTED = 5;
        static final int INT_DOTTED_HEAVY = 6;
        static final int INT_DASH = 7;
        static final int INT_DASHED_HEAVY = 8;
        static final int INT_DASH_LONG = 9;
        static final int INT_DASH_LONG_HEAVY = 10;
        static final int INT_DOT_DASH = 11;
        static final int INT_DASH_DOT_HEAVY = 12;
        static final int INT_DOT_DOT_DASH = 13;
        static final int INT_DASH_DOT_DOT_HEAVY = 14;
        static final int INT_WAVE = 15;
        static final int INT_WAVY_HEAVY = 16;
        static final int INT_WAVY_DOUBLE = 17;
        static final int INT_NONE = 18;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("single", 1), new Enum("words", 2), new Enum("double", 3), new Enum("thick", 4), new Enum("dotted", 5), new Enum("dottedHeavy", 6), new Enum("dash", 7), new Enum("dashedHeavy", 8), new Enum("dashLong", 9), new Enum("dashLongHeavy", 10), new Enum("dotDash", 11), new Enum("dashDotHeavy", 12), new Enum("dotDotDash", 13), new Enum("dashDotDotHeavy", 14), new Enum("wave", 15), new Enum("wavyHeavy", 16), new Enum("wavyDouble", 17), new Enum("none", 18)});
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

