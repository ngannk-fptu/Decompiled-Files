/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.sharedTypes;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STVerticalAlignRun
extends XmlString {
    public static final SimpleTypeFactory<STVerticalAlignRun> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stverticalalignrunc096type");
    public static final SchemaType type = Factory.getType();
    public static final Enum BASELINE = Enum.forString("baseline");
    public static final Enum SUPERSCRIPT = Enum.forString("superscript");
    public static final Enum SUBSCRIPT = Enum.forString("subscript");
    public static final int INT_BASELINE = 1;
    public static final int INT_SUPERSCRIPT = 2;
    public static final int INT_SUBSCRIPT = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_BASELINE = 1;
        static final int INT_SUPERSCRIPT = 2;
        static final int INT_SUBSCRIPT = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("baseline", 1), new Enum("superscript", 2), new Enum("subscript", 3)});
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

