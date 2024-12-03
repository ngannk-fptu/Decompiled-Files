/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.sharedTypes;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STXAlign
extends XmlString {
    public static final SimpleTypeFactory<STXAlign> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stxalign8127type");
    public static final SchemaType type = Factory.getType();
    public static final Enum LEFT = Enum.forString("left");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum RIGHT = Enum.forString("right");
    public static final Enum INSIDE = Enum.forString("inside");
    public static final Enum OUTSIDE = Enum.forString("outside");
    public static final int INT_LEFT = 1;
    public static final int INT_CENTER = 2;
    public static final int INT_RIGHT = 3;
    public static final int INT_INSIDE = 4;
    public static final int INT_OUTSIDE = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_LEFT = 1;
        static final int INT_CENTER = 2;
        static final int INT_RIGHT = 3;
        static final int INT_INSIDE = 4;
        static final int INT_OUTSIDE = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("left", 1), new Enum("center", 2), new Enum("right", 3), new Enum("inside", 4), new Enum("outside", 5)});
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

