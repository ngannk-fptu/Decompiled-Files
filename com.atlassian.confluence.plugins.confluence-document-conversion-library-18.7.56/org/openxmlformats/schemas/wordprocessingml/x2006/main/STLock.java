/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STLock
extends XmlString {
    public static final SimpleTypeFactory<STLock> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stlocke60dtype");
    public static final SchemaType type = Factory.getType();
    public static final Enum SDT_LOCKED = Enum.forString("sdtLocked");
    public static final Enum CONTENT_LOCKED = Enum.forString("contentLocked");
    public static final Enum UNLOCKED = Enum.forString("unlocked");
    public static final Enum SDT_CONTENT_LOCKED = Enum.forString("sdtContentLocked");
    public static final int INT_SDT_LOCKED = 1;
    public static final int INT_CONTENT_LOCKED = 2;
    public static final int INT_UNLOCKED = 3;
    public static final int INT_SDT_CONTENT_LOCKED = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_SDT_LOCKED = 1;
        static final int INT_CONTENT_LOCKED = 2;
        static final int INT_UNLOCKED = 3;
        static final int INT_SDT_CONTENT_LOCKED = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("sdtLocked", 1), new Enum("contentLocked", 2), new Enum("unlocked", 3), new Enum("sdtContentLocked", 4)});
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

