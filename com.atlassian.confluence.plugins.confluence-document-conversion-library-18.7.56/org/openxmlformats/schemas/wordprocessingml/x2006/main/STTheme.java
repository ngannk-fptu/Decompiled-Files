/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTheme
extends XmlString {
    public static final SimpleTypeFactory<STTheme> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttheme58b9type");
    public static final SchemaType type = Factory.getType();
    public static final Enum MAJOR_EAST_ASIA = Enum.forString("majorEastAsia");
    public static final Enum MAJOR_BIDI = Enum.forString("majorBidi");
    public static final Enum MAJOR_ASCII = Enum.forString("majorAscii");
    public static final Enum MAJOR_H_ANSI = Enum.forString("majorHAnsi");
    public static final Enum MINOR_EAST_ASIA = Enum.forString("minorEastAsia");
    public static final Enum MINOR_BIDI = Enum.forString("minorBidi");
    public static final Enum MINOR_ASCII = Enum.forString("minorAscii");
    public static final Enum MINOR_H_ANSI = Enum.forString("minorHAnsi");
    public static final int INT_MAJOR_EAST_ASIA = 1;
    public static final int INT_MAJOR_BIDI = 2;
    public static final int INT_MAJOR_ASCII = 3;
    public static final int INT_MAJOR_H_ANSI = 4;
    public static final int INT_MINOR_EAST_ASIA = 5;
    public static final int INT_MINOR_BIDI = 6;
    public static final int INT_MINOR_ASCII = 7;
    public static final int INT_MINOR_H_ANSI = 8;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_MAJOR_EAST_ASIA = 1;
        static final int INT_MAJOR_BIDI = 2;
        static final int INT_MAJOR_ASCII = 3;
        static final int INT_MAJOR_H_ANSI = 4;
        static final int INT_MINOR_EAST_ASIA = 5;
        static final int INT_MINOR_BIDI = 6;
        static final int INT_MINOR_ASCII = 7;
        static final int INT_MINOR_H_ANSI = 8;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("majorEastAsia", 1), new Enum("majorBidi", 2), new Enum("majorAscii", 3), new Enum("majorHAnsi", 4), new Enum("minorEastAsia", 5), new Enum("minorBidi", 6), new Enum("minorAscii", 7), new Enum("minorHAnsi", 8)});
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

