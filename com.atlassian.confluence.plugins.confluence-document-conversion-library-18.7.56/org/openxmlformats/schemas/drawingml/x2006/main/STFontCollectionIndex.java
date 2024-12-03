/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STFontCollectionIndex
extends XmlToken {
    public static final SimpleTypeFactory<STFontCollectionIndex> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stfontcollectionindex6766type");
    public static final SchemaType type = Factory.getType();
    public static final Enum MAJOR = Enum.forString("major");
    public static final Enum MINOR = Enum.forString("minor");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_MAJOR = 1;
    public static final int INT_MINOR = 2;
    public static final int INT_NONE = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_MAJOR = 1;
        static final int INT_MINOR = 2;
        static final int INT_NONE = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("major", 1), new Enum("minor", 2), new Enum("none", 3)});
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

