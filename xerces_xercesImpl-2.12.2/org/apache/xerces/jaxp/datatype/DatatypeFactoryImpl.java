/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.jaxp.datatype.DurationImpl;
import org.apache.xerces.jaxp.datatype.XMLGregorianCalendarImpl;

public class DatatypeFactoryImpl
extends DatatypeFactory {
    @Override
    public Duration newDuration(String string) {
        return new DurationImpl(string);
    }

    @Override
    public Duration newDuration(long l) {
        return new DurationImpl(l);
    }

    @Override
    public Duration newDuration(boolean bl, BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5, BigDecimal bigDecimal) {
        return new DurationImpl(bl, bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigDecimal);
    }

    @Override
    public XMLGregorianCalendar newXMLGregorianCalendar() {
        return new XMLGregorianCalendarImpl();
    }

    @Override
    public XMLGregorianCalendar newXMLGregorianCalendar(String string) {
        return new XMLGregorianCalendarImpl(string);
    }

    @Override
    public XMLGregorianCalendar newXMLGregorianCalendar(GregorianCalendar gregorianCalendar) {
        return new XMLGregorianCalendarImpl(gregorianCalendar);
    }

    @Override
    public XMLGregorianCalendar newXMLGregorianCalendar(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        return XMLGregorianCalendarImpl.createDateTime(n, n2, n3, n4, n5, n6, n7, n8);
    }

    @Override
    public XMLGregorianCalendar newXMLGregorianCalendar(BigInteger bigInteger, int n, int n2, int n3, int n4, int n5, BigDecimal bigDecimal, int n6) {
        return new XMLGregorianCalendarImpl(bigInteger, n, n2, n3, n4, n5, bigDecimal, n6);
    }
}

