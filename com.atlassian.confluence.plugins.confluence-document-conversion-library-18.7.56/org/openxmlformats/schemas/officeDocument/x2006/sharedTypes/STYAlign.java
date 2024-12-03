/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.sharedTypes;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STYAlign
extends XmlString {
    public static final SimpleTypeFactory<STYAlign> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "styalign3606type");
    public static final SchemaType type = Factory.getType();
    public static final Enum INLINE = Enum.forString("inline");
    public static final Enum TOP = Enum.forString("top");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum BOTTOM = Enum.forString("bottom");
    public static final Enum INSIDE = Enum.forString("inside");
    public static final Enum OUTSIDE = Enum.forString("outside");
    public static final int INT_INLINE = 1;
    public static final int INT_TOP = 2;
    public static final int INT_CENTER = 3;
    public static final int INT_BOTTOM = 4;
    public static final int INT_INSIDE = 5;
    public static final int INT_OUTSIDE = 6;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_INLINE = 1;
        static final int INT_TOP = 2;
        static final int INT_CENTER = 3;
        static final int INT_BOTTOM = 4;
        static final int INT_INSIDE = 5;
        static final int INT_OUTSIDE = 6;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("inline", 1), new Enum("top", 2), new Enum("center", 3), new Enum("bottom", 4), new Enum("inside", 5), new Enum("outside", 6)});
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

