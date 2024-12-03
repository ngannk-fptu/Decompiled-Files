/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STSheetState
extends XmlString {
    public static final SimpleTypeFactory<STSheetState> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stsheetstate158btype");
    public static final SchemaType type = Factory.getType();
    public static final Enum VISIBLE = Enum.forString("visible");
    public static final Enum HIDDEN = Enum.forString("hidden");
    public static final Enum VERY_HIDDEN = Enum.forString("veryHidden");
    public static final int INT_VISIBLE = 1;
    public static final int INT_HIDDEN = 2;
    public static final int INT_VERY_HIDDEN = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_VISIBLE = 1;
        static final int INT_HIDDEN = 2;
        static final int INT_VERY_HIDDEN = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("visible", 1), new Enum("hidden", 2), new Enum("veryHidden", 3)});
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

