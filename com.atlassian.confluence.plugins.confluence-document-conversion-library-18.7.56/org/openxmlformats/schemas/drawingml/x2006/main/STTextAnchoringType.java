/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTextAnchoringType
extends XmlToken {
    public static final SimpleTypeFactory<STTextAnchoringType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttextanchoringtyped99btype");
    public static final SchemaType type = Factory.getType();
    public static final Enum T = Enum.forString("t");
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum B = Enum.forString("b");
    public static final Enum JUST = Enum.forString("just");
    public static final Enum DIST = Enum.forString("dist");
    public static final int INT_T = 1;
    public static final int INT_CTR = 2;
    public static final int INT_B = 3;
    public static final int INT_JUST = 4;
    public static final int INT_DIST = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_T = 1;
        static final int INT_CTR = 2;
        static final int INT_B = 3;
        static final int INT_JUST = 4;
        static final int INT_DIST = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("t", 1), new Enum("ctr", 2), new Enum("b", 3), new Enum("just", 4), new Enum("dist", 5)});
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

