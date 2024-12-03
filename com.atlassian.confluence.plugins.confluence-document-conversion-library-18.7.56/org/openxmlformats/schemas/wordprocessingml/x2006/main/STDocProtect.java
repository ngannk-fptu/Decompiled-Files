/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STDocProtect
extends XmlString {
    public static final SimpleTypeFactory<STDocProtect> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stdocprotect5801type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum READ_ONLY = Enum.forString("readOnly");
    public static final Enum COMMENTS = Enum.forString("comments");
    public static final Enum TRACKED_CHANGES = Enum.forString("trackedChanges");
    public static final Enum FORMS = Enum.forString("forms");
    public static final int INT_NONE = 1;
    public static final int INT_READ_ONLY = 2;
    public static final int INT_COMMENTS = 3;
    public static final int INT_TRACKED_CHANGES = 4;
    public static final int INT_FORMS = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_READ_ONLY = 2;
        static final int INT_COMMENTS = 3;
        static final int INT_TRACKED_CHANGES = 4;
        static final int INT_FORMS = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("readOnly", 2), new Enum("comments", 3), new Enum("trackedChanges", 4), new Enum("forms", 5)});
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

