/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTimePeriod
extends XmlString {
    public static final SimpleTypeFactory<STTimePeriod> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttimeperiodef07type");
    public static final SchemaType type = Factory.getType();
    public static final Enum TODAY = Enum.forString("today");
    public static final Enum YESTERDAY = Enum.forString("yesterday");
    public static final Enum TOMORROW = Enum.forString("tomorrow");
    public static final Enum LAST_7_DAYS = Enum.forString("last7Days");
    public static final Enum THIS_MONTH = Enum.forString("thisMonth");
    public static final Enum LAST_MONTH = Enum.forString("lastMonth");
    public static final Enum NEXT_MONTH = Enum.forString("nextMonth");
    public static final Enum THIS_WEEK = Enum.forString("thisWeek");
    public static final Enum LAST_WEEK = Enum.forString("lastWeek");
    public static final Enum NEXT_WEEK = Enum.forString("nextWeek");
    public static final int INT_TODAY = 1;
    public static final int INT_YESTERDAY = 2;
    public static final int INT_TOMORROW = 3;
    public static final int INT_LAST_7_DAYS = 4;
    public static final int INT_THIS_MONTH = 5;
    public static final int INT_LAST_MONTH = 6;
    public static final int INT_NEXT_MONTH = 7;
    public static final int INT_THIS_WEEK = 8;
    public static final int INT_LAST_WEEK = 9;
    public static final int INT_NEXT_WEEK = 10;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_TODAY = 1;
        static final int INT_YESTERDAY = 2;
        static final int INT_TOMORROW = 3;
        static final int INT_LAST_7_DAYS = 4;
        static final int INT_THIS_MONTH = 5;
        static final int INT_LAST_MONTH = 6;
        static final int INT_NEXT_MONTH = 7;
        static final int INT_THIS_WEEK = 8;
        static final int INT_LAST_WEEK = 9;
        static final int INT_NEXT_WEEK = 10;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("today", 1), new Enum("yesterday", 2), new Enum("tomorrow", 3), new Enum("last7Days", 4), new Enum("thisMonth", 5), new Enum("lastMonth", 6), new Enum("nextMonth", 7), new Enum("thisWeek", 8), new Enum("lastWeek", 9), new Enum("nextWeek", 10)});
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

