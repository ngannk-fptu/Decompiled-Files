/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.conversion.TypeConversionException;

public class NumberConverter
extends DefaultTypeConverter {
    private static final Logger LOG = LogManager.getLogger(NumberConverter.class);

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        Object[] objArray;
        if (value instanceof String) {
            String stringValue = String.valueOf(value);
            if (toType == BigDecimal.class) {
                return this.convertToBigDecimal(context, stringValue);
            }
            if (toType == BigInteger.class) {
                return new BigInteger(stringValue);
            }
            if (toType == Double.class || toType == Double.TYPE) {
                return this.convertToDouble(context, stringValue);
            }
            if (toType == Float.class || toType == Float.TYPE) {
                return this.convertToFloat(context, stringValue);
            }
            if (toType.isPrimitive()) {
                Object convertedValue = super.convertValue(context, value, toType);
                if (!this.isInRange((Number)convertedValue, stringValue, toType)) {
                    throw new TypeConversionException("Overflow or underflow casting: \"" + stringValue + "\" into class " + convertedValue.getClass().getName());
                }
                return convertedValue;
            }
            if (!toType.isPrimitive() && stringValue.isEmpty()) {
                return null;
            }
            NumberFormat numFormat = NumberFormat.getInstance(this.getLocale(context));
            ParsePosition parsePos = new ParsePosition(0);
            if (this.isIntegerType(toType)) {
                numFormat.setParseIntegerOnly(true);
            }
            numFormat.setGroupingUsed(true);
            Number number = numFormat.parse(stringValue, parsePos);
            if (parsePos.getIndex() != stringValue.length()) {
                throw new TypeConversionException("Unparseable number: \"" + stringValue + "\" at position " + parsePos.getIndex());
            }
            if (!this.isInRange(number, stringValue, toType)) {
                throw new TypeConversionException("Overflow or underflow casting: \"" + stringValue + "\" into class " + number.getClass().getName());
            }
            value = super.convertValue(context, number, toType);
        } else if (value instanceof Object[] && (objArray = (Object[])value).length == 1) {
            return this.convertValue(context, null, null, null, objArray[0], toType);
        }
        return super.convertValue(context, value, toType);
    }

    protected Object convertToBigDecimal(Map<String, Object> context, String stringValue) {
        Locale locale = this.getLocale(context);
        NumberFormat format = this.getNumberFormat(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat)format).setParseBigDecimal(true);
            char separator = ((DecimalFormat)format).getDecimalFormatSymbols().getGroupingSeparator();
            stringValue = this.normalize(stringValue, separator);
        }
        LOG.debug("Trying to convert a value {} with locale {} to BigDecimal", (Object)stringValue, (Object)locale);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = format.parse(stringValue, parsePosition);
        if (parsePosition.getIndex() != stringValue.length()) {
            throw new TypeConversionException("Unparseable number: \"" + stringValue + "\" at position " + parsePosition.getIndex());
        }
        return number;
    }

    protected Object convertToDouble(Map<String, Object> context, String stringValue) {
        Locale locale = this.getLocale(context);
        NumberFormat format = this.getNumberFormat(locale);
        if (format instanceof DecimalFormat) {
            char separator = ((DecimalFormat)format).getDecimalFormatSymbols().getGroupingSeparator();
            stringValue = this.normalize(stringValue, separator);
        }
        LOG.debug("Trying to convert a value {} with locale {} to Double", (Object)stringValue, (Object)locale);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = format.parse(stringValue, parsePosition);
        if (parsePosition.getIndex() != stringValue.length()) {
            throw new TypeConversionException("Unparseable number: \"" + stringValue + "\" at position " + parsePosition.getIndex());
        }
        if (!this.isInRange(number, stringValue, Double.class)) {
            throw new TypeConversionException("Overflow or underflow converting: \"" + stringValue + "\" into class " + number.getClass().getName());
        }
        if (number != null) {
            return number.doubleValue();
        }
        return null;
    }

    protected Object convertToFloat(Map<String, Object> context, String stringValue) {
        Locale locale = this.getLocale(context);
        NumberFormat format = this.getNumberFormat(locale);
        if (format instanceof DecimalFormat) {
            char separator = ((DecimalFormat)format).getDecimalFormatSymbols().getGroupingSeparator();
            stringValue = this.normalize(stringValue, separator);
        }
        LOG.debug("Trying to convert a value {} with locale {} to Float", (Object)stringValue, (Object)locale);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = format.parse(stringValue, parsePosition);
        if (parsePosition.getIndex() != stringValue.length()) {
            throw new TypeConversionException("Unparseable number: \"" + stringValue + "\" at position " + parsePosition.getIndex());
        }
        if (!this.isInRange(number, stringValue, Float.class)) {
            throw new TypeConversionException("Overflow or underflow converting: \"" + stringValue + "\" into class " + number.getClass().getName());
        }
        if (number != null) {
            return Float.valueOf(number.floatValue());
        }
        return null;
    }

    protected NumberFormat getNumberFormat(Locale locale) {
        NumberFormat format = NumberFormat.getNumberInstance(locale);
        format.setGroupingUsed(true);
        return format;
    }

    protected String normalize(String strValue, char separator) {
        if (separator == '\u00a0') {
            strValue = strValue.replaceAll(" ", String.valueOf(separator));
        }
        return strValue;
    }

    protected boolean isInRange(Number value, String stringValue, Class toType) {
        Number upperBound;
        Number lowerBound;
        Number bigValue;
        block9: {
            bigValue = null;
            lowerBound = null;
            upperBound = null;
            try {
                if (Double.TYPE == toType || Double.class == toType) {
                    bigValue = new BigDecimal(stringValue);
                    lowerBound = BigDecimal.valueOf(Double.MAX_VALUE).negate();
                    upperBound = BigDecimal.valueOf(Double.MAX_VALUE);
                    break block9;
                }
                if (Float.TYPE == toType || Float.class == toType) {
                    bigValue = new BigDecimal(stringValue);
                    lowerBound = BigDecimal.valueOf(3.4028234663852886E38).negate();
                    upperBound = BigDecimal.valueOf(3.4028234663852886E38);
                    break block9;
                }
                if (Byte.TYPE == toType || Byte.class == toType) {
                    bigValue = new BigInteger(stringValue);
                    lowerBound = BigInteger.valueOf(-128L);
                    upperBound = BigInteger.valueOf(127L);
                    break block9;
                }
                if (Character.TYPE == toType || Character.class == toType) {
                    bigValue = new BigInteger(stringValue);
                    lowerBound = BigInteger.valueOf(0L);
                    upperBound = BigInteger.valueOf(65535L);
                    break block9;
                }
                if (Short.TYPE == toType || Short.class == toType) {
                    bigValue = new BigInteger(stringValue);
                    lowerBound = BigInteger.valueOf(-32768L);
                    upperBound = BigInteger.valueOf(32767L);
                    break block9;
                }
                if (Integer.TYPE == toType || Integer.class == toType) {
                    bigValue = new BigInteger(stringValue);
                    lowerBound = BigInteger.valueOf(Integer.MIN_VALUE);
                    upperBound = BigInteger.valueOf(Integer.MAX_VALUE);
                    break block9;
                }
                if (Long.TYPE == toType || Long.class == toType) {
                    bigValue = new BigInteger(stringValue);
                    lowerBound = BigInteger.valueOf(Long.MIN_VALUE);
                    upperBound = BigInteger.valueOf(Long.MAX_VALUE);
                    break block9;
                }
                throw new IllegalArgumentException("Unexpected numeric type: " + toType.getName());
            }
            catch (NumberFormatException e) {
                return true;
            }
        }
        return ((Comparable)((Object)bigValue)).compareTo(lowerBound) >= 0 && ((Comparable)((Object)bigValue)).compareTo(upperBound) <= 0;
    }

    private boolean isIntegerType(Class type) {
        return Double.TYPE != type && Float.TYPE != type && Double.class != type && Float.class != type && Character.TYPE != type && Character.class != type;
    }
}

