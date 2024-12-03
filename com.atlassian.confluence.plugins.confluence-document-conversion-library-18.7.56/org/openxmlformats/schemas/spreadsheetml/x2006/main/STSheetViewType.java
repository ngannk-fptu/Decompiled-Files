/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STSheetViewType
extends XmlString {
    public static final SimpleTypeFactory<STSheetViewType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stsheetviewtype988dtype");
    public static final SchemaType type = Factory.getType();
    public static final Enum NORMAL = Enum.forString("normal");
    public static final Enum PAGE_BREAK_PREVIEW = Enum.forString("pageBreakPreview");
    public static final Enum PAGE_LAYOUT = Enum.forString("pageLayout");
    public static final int INT_NORMAL = 1;
    public static final int INT_PAGE_BREAK_PREVIEW = 2;
    public static final int INT_PAGE_LAYOUT = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NORMAL = 1;
        static final int INT_PAGE_BREAK_PREVIEW = 2;
        static final int INT_PAGE_LAYOUT = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("normal", 1), new Enum("pageBreakPreview", 2), new Enum("pageLayout", 3)});
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

