/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlObject;

public interface SimpleValue
extends XmlObject {
    public SchemaType instanceType();

    public String getStringValue();

    public boolean getBooleanValue();

    public byte getByteValue();

    public short getShortValue();

    public int getIntValue();

    public long getLongValue();

    public BigInteger getBigIntegerValue();

    public BigDecimal getBigDecimalValue();

    public float getFloatValue();

    public double getDoubleValue();

    public byte[] getByteArrayValue();

    public StringEnumAbstractBase getEnumValue();

    public Calendar getCalendarValue();

    public Date getDateValue();

    public GDate getGDateValue();

    public GDuration getGDurationValue();

    public QName getQNameValue();

    public List<?> getListValue();

    public List<? extends XmlAnySimpleType> xgetListValue();

    public Object getObjectValue();

    public void setStringValue(String var1);

    public void setBooleanValue(boolean var1);

    public void setByteValue(byte var1);

    public void setShortValue(short var1);

    public void setIntValue(int var1);

    public void setLongValue(long var1);

    public void setBigIntegerValue(BigInteger var1);

    public void setBigDecimalValue(BigDecimal var1);

    public void setFloatValue(float var1);

    public void setDoubleValue(double var1);

    public void setByteArrayValue(byte[] var1);

    public void setEnumValue(StringEnumAbstractBase var1);

    public void setCalendarValue(Calendar var1);

    public void setDateValue(Date var1);

    public void setGDateValue(GDate var1);

    public void setGDurationValue(GDuration var1);

    public void setQNameValue(QName var1);

    public void setListValue(List<?> var1);

    public void setObjectValue(Object var1);
}

