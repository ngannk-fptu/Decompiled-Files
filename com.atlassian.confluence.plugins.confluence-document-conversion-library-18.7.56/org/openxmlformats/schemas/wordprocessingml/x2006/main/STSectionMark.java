/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STSectionMark
extends XmlString {
    public static final SimpleTypeFactory<STSectionMark> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stsectionmark2010type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NEXT_PAGE = Enum.forString("nextPage");
    public static final Enum NEXT_COLUMN = Enum.forString("nextColumn");
    public static final Enum CONTINUOUS = Enum.forString("continuous");
    public static final Enum EVEN_PAGE = Enum.forString("evenPage");
    public static final Enum ODD_PAGE = Enum.forString("oddPage");
    public static final int INT_NEXT_PAGE = 1;
    public static final int INT_NEXT_COLUMN = 2;
    public static final int INT_CONTINUOUS = 3;
    public static final int INT_EVEN_PAGE = 4;
    public static final int INT_ODD_PAGE = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NEXT_PAGE = 1;
        static final int INT_NEXT_COLUMN = 2;
        static final int INT_CONTINUOUS = 3;
        static final int INT_EVEN_PAGE = 4;
        static final int INT_ODD_PAGE = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("nextPage", 1), new Enum("nextColumn", 2), new Enum("continuous", 3), new Enum("evenPage", 4), new Enum("oddPage", 5)});
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

