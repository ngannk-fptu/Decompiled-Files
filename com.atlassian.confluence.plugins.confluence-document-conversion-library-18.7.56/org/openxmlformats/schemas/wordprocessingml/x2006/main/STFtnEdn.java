/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STFtnEdn
extends XmlString {
    public static final SimpleTypeFactory<STFtnEdn> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stftnednd4c9type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NORMAL = Enum.forString("normal");
    public static final Enum SEPARATOR = Enum.forString("separator");
    public static final Enum CONTINUATION_SEPARATOR = Enum.forString("continuationSeparator");
    public static final Enum CONTINUATION_NOTICE = Enum.forString("continuationNotice");
    public static final int INT_NORMAL = 1;
    public static final int INT_SEPARATOR = 2;
    public static final int INT_CONTINUATION_SEPARATOR = 3;
    public static final int INT_CONTINUATION_NOTICE = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NORMAL = 1;
        static final int INT_SEPARATOR = 2;
        static final int INT_CONTINUATION_SEPARATOR = 3;
        static final int INT_CONTINUATION_NOTICE = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("normal", 1), new Enum("separator", 2), new Enum("continuationSeparator", 3), new Enum("continuationNotice", 4)});
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

