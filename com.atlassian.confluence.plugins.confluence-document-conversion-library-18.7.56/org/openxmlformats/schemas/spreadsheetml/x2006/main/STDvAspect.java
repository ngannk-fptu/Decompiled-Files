/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STDvAspect
extends XmlString {
    public static final SimpleTypeFactory<STDvAspect> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stdvaspect8de3type");
    public static final SchemaType type = Factory.getType();
    public static final Enum DVASPECT_CONTENT = Enum.forString("DVASPECT_CONTENT");
    public static final Enum DVASPECT_ICON = Enum.forString("DVASPECT_ICON");
    public static final int INT_DVASPECT_CONTENT = 1;
    public static final int INT_DVASPECT_ICON = 2;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_DVASPECT_CONTENT = 1;
        static final int INT_DVASPECT_ICON = 2;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("DVASPECT_CONTENT", 1), new Enum("DVASPECT_ICON", 2)});
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

