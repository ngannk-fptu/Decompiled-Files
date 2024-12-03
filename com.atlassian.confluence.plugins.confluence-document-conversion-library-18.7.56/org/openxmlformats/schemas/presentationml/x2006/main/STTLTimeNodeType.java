/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTLTimeNodeType
extends XmlToken {
    public static final SimpleTypeFactory<STTLTimeNodeType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttltimenodetypebbf4type");
    public static final SchemaType type = Factory.getType();
    public static final Enum CLICK_EFFECT = Enum.forString("clickEffect");
    public static final Enum WITH_EFFECT = Enum.forString("withEffect");
    public static final Enum AFTER_EFFECT = Enum.forString("afterEffect");
    public static final Enum MAIN_SEQ = Enum.forString("mainSeq");
    public static final Enum INTERACTIVE_SEQ = Enum.forString("interactiveSeq");
    public static final Enum CLICK_PAR = Enum.forString("clickPar");
    public static final Enum WITH_GROUP = Enum.forString("withGroup");
    public static final Enum AFTER_GROUP = Enum.forString("afterGroup");
    public static final Enum TM_ROOT = Enum.forString("tmRoot");
    public static final int INT_CLICK_EFFECT = 1;
    public static final int INT_WITH_EFFECT = 2;
    public static final int INT_AFTER_EFFECT = 3;
    public static final int INT_MAIN_SEQ = 4;
    public static final int INT_INTERACTIVE_SEQ = 5;
    public static final int INT_CLICK_PAR = 6;
    public static final int INT_WITH_GROUP = 7;
    public static final int INT_AFTER_GROUP = 8;
    public static final int INT_TM_ROOT = 9;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_CLICK_EFFECT = 1;
        static final int INT_WITH_EFFECT = 2;
        static final int INT_AFTER_EFFECT = 3;
        static final int INT_MAIN_SEQ = 4;
        static final int INT_INTERACTIVE_SEQ = 5;
        static final int INT_CLICK_PAR = 6;
        static final int INT_WITH_GROUP = 7;
        static final int INT_AFTER_GROUP = 8;
        static final int INT_TM_ROOT = 9;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("clickEffect", 1), new Enum("withEffect", 2), new Enum("afterEffect", 3), new Enum("mainSeq", 4), new Enum("interactiveSeq", 5), new Enum("clickPar", 6), new Enum("withGroup", 7), new Enum("afterGroup", 8), new Enum("tmRoot", 9)});
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

