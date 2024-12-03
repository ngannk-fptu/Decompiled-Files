/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTextAlignment
extends XmlString {
    public static final SimpleTypeFactory<STTextAlignment> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttextalignment316ctype");
    public static final SchemaType type = Factory.getType();
    public static final Enum TOP = Enum.forString("top");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum BASELINE = Enum.forString("baseline");
    public static final Enum BOTTOM = Enum.forString("bottom");
    public static final Enum AUTO = Enum.forString("auto");
    public static final int INT_TOP = 1;
    public static final int INT_CENTER = 2;
    public static final int INT_BASELINE = 3;
    public static final int INT_BOTTOM = 4;
    public static final int INT_AUTO = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_TOP = 1;
        static final int INT_CENTER = 2;
        static final int INT_BASELINE = 3;
        static final int INT_BOTTOM = 4;
        static final int INT_AUTO = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("top", 1), new Enum("center", 2), new Enum("baseline", 3), new Enum("bottom", 4), new Enum("auto", 5)});
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

