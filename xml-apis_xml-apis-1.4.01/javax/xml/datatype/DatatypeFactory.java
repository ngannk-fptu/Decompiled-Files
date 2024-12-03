/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.Duration;
import javax.xml.datatype.FactoryFinder;
import javax.xml.datatype.SecuritySupport;
import javax.xml.datatype.XMLGregorianCalendar;

public abstract class DatatypeFactory {
    public static final String DATATYPEFACTORY_PROPERTY = "javax.xml.datatype.DatatypeFactory";
    public static final String DATATYPEFACTORY_IMPLEMENTATION_CLASS = new String("org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl");

    protected DatatypeFactory() {
    }

    public static DatatypeFactory newInstance() throws DatatypeConfigurationException {
        try {
            return (DatatypeFactory)FactoryFinder.find(DATATYPEFACTORY_PROPERTY, DATATYPEFACTORY_IMPLEMENTATION_CLASS);
        }
        catch (FactoryFinder.ConfigurationError configurationError) {
            throw new DatatypeConfigurationException(configurationError.getMessage(), configurationError.getException());
        }
    }

    public static DatatypeFactory newInstance(String string, ClassLoader classLoader) throws DatatypeConfigurationException {
        if (string == null) {
            throw new DatatypeConfigurationException("factoryClassName cannot be null.");
        }
        if (classLoader == null) {
            classLoader = SecuritySupport.getContextClassLoader();
        }
        try {
            return (DatatypeFactory)FactoryFinder.newInstance(string, classLoader);
        }
        catch (FactoryFinder.ConfigurationError configurationError) {
            throw new DatatypeConfigurationException(configurationError.getMessage(), configurationError.getException());
        }
    }

    public abstract Duration newDuration(String var1);

    public abstract Duration newDuration(long var1);

    public abstract Duration newDuration(boolean var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6, BigDecimal var7);

    public Duration newDuration(boolean bl, int n, int n2, int n3, int n4, int n5, int n6) {
        BigInteger bigInteger = n != Integer.MIN_VALUE ? BigInteger.valueOf(n) : null;
        BigInteger bigInteger2 = n2 != Integer.MIN_VALUE ? BigInteger.valueOf(n2) : null;
        BigInteger bigInteger3 = n3 != Integer.MIN_VALUE ? BigInteger.valueOf(n3) : null;
        BigInteger bigInteger4 = n4 != Integer.MIN_VALUE ? BigInteger.valueOf(n4) : null;
        BigInteger bigInteger5 = n5 != Integer.MIN_VALUE ? BigInteger.valueOf(n5) : null;
        BigDecimal bigDecimal = n6 != Integer.MIN_VALUE ? BigDecimal.valueOf(n6) : null;
        return this.newDuration(bl, bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigDecimal);
    }

    public Duration newDurationDayTime(String string) {
        if (string == null) {
            throw new NullPointerException("The lexical representation cannot be null.");
        }
        int n = string.indexOf(84);
        int n2 = n >= 0 ? n : string.length();
        int n3 = 0;
        while (n3 < n2) {
            char c = string.charAt(n3);
            if (c == 'Y' || c == 'M') {
                throw new IllegalArgumentException("Invalid dayTimeDuration value: " + string);
            }
            ++n3;
        }
        return this.newDuration(string);
    }

    public Duration newDurationDayTime(long l) {
        boolean bl;
        long l2 = l;
        if (l2 == 0L) {
            return this.newDuration(true, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0, 0, 0);
        }
        boolean bl2 = false;
        if (l2 < 0L) {
            bl = false;
            if (l2 == Long.MIN_VALUE) {
                ++l2;
                bl2 = true;
            }
            l2 *= -1L;
        } else {
            bl = true;
        }
        long l3 = l2;
        int n = (int)(l3 % 60000L);
        if (bl2) {
            ++n;
        }
        if (n % 1000 == 0) {
            int n2 = n / 1000;
            int n3 = (int)((l3 /= 60000L) % 60L);
            int n4 = (int)((l3 /= 60L) % 24L);
            long l4 = l3 / 24L;
            if (l4 <= Integer.MAX_VALUE) {
                return this.newDuration(bl, Integer.MIN_VALUE, Integer.MIN_VALUE, (int)l4, n4, n3, n2);
            }
            return this.newDuration(bl, null, null, BigInteger.valueOf(l4), BigInteger.valueOf(n4), BigInteger.valueOf(n3), BigDecimal.valueOf(n, 3));
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(n, 3);
        BigInteger bigInteger = BigInteger.valueOf((l3 /= 60000L) % 60L);
        BigInteger bigInteger2 = BigInteger.valueOf((l3 /= 60L) % 24L);
        BigInteger bigInteger3 = BigInteger.valueOf(l3 /= 24L);
        return this.newDuration(bl, null, null, bigInteger3, bigInteger2, bigInteger, bigDecimal);
    }

    public Duration newDurationDayTime(boolean bl, BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4) {
        return this.newDuration(bl, null, null, bigInteger, bigInteger2, bigInteger3, bigInteger4 != null ? new BigDecimal(bigInteger4) : null);
    }

    public Duration newDurationDayTime(boolean bl, int n, int n2, int n3, int n4) {
        return this.newDuration(bl, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, n4);
    }

    public Duration newDurationYearMonth(String string) {
        if (string == null) {
            throw new NullPointerException("The lexical representation cannot be null.");
        }
        int n = string.length();
        int n2 = 0;
        while (n2 < n) {
            char c = string.charAt(n2);
            if (c == 'D' || c == 'T') {
                throw new IllegalArgumentException("Invalid yearMonthDuration value: " + string);
            }
            ++n2;
        }
        return this.newDuration(string);
    }

    public Duration newDurationYearMonth(long l) {
        return this.newDuration(l);
    }

    public Duration newDurationYearMonth(boolean bl, BigInteger bigInteger, BigInteger bigInteger2) {
        return this.newDuration(bl, bigInteger, bigInteger2, null, null, null, null);
    }

    public Duration newDurationYearMonth(boolean bl, int n, int n2) {
        return this.newDuration(bl, n, n2, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public abstract XMLGregorianCalendar newXMLGregorianCalendar();

    public abstract XMLGregorianCalendar newXMLGregorianCalendar(String var1);

    public abstract XMLGregorianCalendar newXMLGregorianCalendar(GregorianCalendar var1);

    public abstract XMLGregorianCalendar newXMLGregorianCalendar(BigInteger var1, int var2, int var3, int var4, int var5, int var6, BigDecimal var7, int var8);

    public XMLGregorianCalendar newXMLGregorianCalendar(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        BigInteger bigInteger = n != Integer.MIN_VALUE ? BigInteger.valueOf(n) : null;
        BigDecimal bigDecimal = null;
        if (n7 != Integer.MIN_VALUE) {
            if (n7 < 0 || n7 > 1000) {
                throw new IllegalArgumentException("javax.xml.datatype.DatatypeFactory#newXMLGregorianCalendar(int year, int month, int day, int hour, int minute, int second, int millisecond, int timezone)with invalid millisecond: " + n7);
            }
            bigDecimal = BigDecimal.valueOf(n7, 3);
        }
        return this.newXMLGregorianCalendar(bigInteger, n2, n3, n4, n5, n6, bigDecimal, n8);
    }

    public XMLGregorianCalendar newXMLGregorianCalendarDate(int n, int n2, int n3, int n4) {
        return this.newXMLGregorianCalendar(n, n2, n3, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, n4);
    }

    public XMLGregorianCalendar newXMLGregorianCalendarTime(int n, int n2, int n3, int n4) {
        return this.newXMLGregorianCalendar(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, Integer.MIN_VALUE, n4);
    }

    public XMLGregorianCalendar newXMLGregorianCalendarTime(int n, int n2, int n3, BigDecimal bigDecimal, int n4) {
        return this.newXMLGregorianCalendar(null, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, bigDecimal, n4);
    }

    public XMLGregorianCalendar newXMLGregorianCalendarTime(int n, int n2, int n3, int n4, int n5) {
        BigDecimal bigDecimal = null;
        if (n4 != Integer.MIN_VALUE) {
            if (n4 < 0 || n4 > 1000) {
                throw new IllegalArgumentException("javax.xml.datatype.DatatypeFactory#newXMLGregorianCalendarTime(int hours, int minutes, int seconds, int milliseconds, int timezone)with invalid milliseconds: " + n4);
            }
            bigDecimal = BigDecimal.valueOf(n4, 3);
        }
        return this.newXMLGregorianCalendarTime(n, n2, n3, bigDecimal, n5);
    }
}

