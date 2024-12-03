/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STDocGrid
extends XmlString {
    public static final SimpleTypeFactory<STDocGrid> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stdocgrid1cc4type");
    public static final SchemaType type = Factory.getType();
    public static final Enum DEFAULT = Enum.forString("default");
    public static final Enum LINES = Enum.forString("lines");
    public static final Enum LINES_AND_CHARS = Enum.forString("linesAndChars");
    public static final Enum SNAP_TO_CHARS = Enum.forString("snapToChars");
    public static final int INT_DEFAULT = 1;
    public static final int INT_LINES = 2;
    public static final int INT_LINES_AND_CHARS = 3;
    public static final int INT_SNAP_TO_CHARS = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_DEFAULT = 1;
        static final int INT_LINES = 2;
        static final int INT_LINES_AND_CHARS = 3;
        static final int INT_SNAP_TO_CHARS = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("default", 1), new Enum("lines", 2), new Enum("linesAndChars", 3), new Enum("snapToChars", 4)});
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

