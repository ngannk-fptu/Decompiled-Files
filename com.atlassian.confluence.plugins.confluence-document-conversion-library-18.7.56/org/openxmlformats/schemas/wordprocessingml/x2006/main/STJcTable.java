/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STJcTable
extends XmlString {
    public static final SimpleTypeFactory<STJcTable> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stjctable2eadtype");
    public static final SchemaType type = Factory.getType();
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum END = Enum.forString("end");
    public static final Enum LEFT = Enum.forString("left");
    public static final Enum RIGHT = Enum.forString("right");
    public static final Enum START = Enum.forString("start");
    public static final int INT_CENTER = 1;
    public static final int INT_END = 2;
    public static final int INT_LEFT = 3;
    public static final int INT_RIGHT = 4;
    public static final int INT_START = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_CENTER = 1;
        static final int INT_END = 2;
        static final int INT_LEFT = 3;
        static final int INT_RIGHT = 4;
        static final int INT_START = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("center", 1), new Enum("end", 2), new Enum("left", 3), new Enum("right", 4), new Enum("start", 5)});
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

