/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.CalendarFormatter;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.CalendarParser;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.IDateTimeValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.PreciseCalendarFormatter;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.PreciseCalendarParser;
import java.util.Calendar;

abstract class DateTimeBaseType
extends BuiltinAtomicType
implements Comparator {
    private static final long serialVersionUID = 1465669066779112677L;

    protected DateTimeBaseType(String typeName) {
        super(typeName);
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    protected final boolean checkFormat(String content, ValidationContext context) {
        try {
            CalendarParser.parse(this.getFormat(), content);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public final Object _createValue(String content, ValidationContext context) {
        try {
            return PreciseCalendarParser.parse(this.getFormat(), content);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public final String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof IDateTimeValueType)) {
            throw new IllegalArgumentException();
        }
        return PreciseCalendarFormatter.format(this.getFormat(), (IDateTimeValueType)value);
    }

    public final Object _createJavaObject(String literal, ValidationContext context) {
        return CalendarParser.parse(this.getFormat(), literal);
    }

    public final String serializeJavaObject(Object value, SerializationContext context) {
        if (!(value instanceof Calendar)) {
            throw new IllegalArgumentException();
        }
        return CalendarFormatter.format(this.getFormat(), (Calendar)value);
    }

    public Class getJavaObjectType() {
        return Calendar.class;
    }

    protected abstract String getFormat();

    public int compare(Object lhs, Object rhs) {
        return ((IDateTimeValueType)lhs).compare((IDateTimeValueType)rhs);
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("pattern") || facetName.equals("enumeration") || facetName.equals("whiteSpace") || facetName.equals("maxInclusive") || facetName.equals("maxExclusive") || facetName.equals("minInclusive") || facetName.equals("minExclusive")) {
            return 0;
        }
        return -2;
    }
}

