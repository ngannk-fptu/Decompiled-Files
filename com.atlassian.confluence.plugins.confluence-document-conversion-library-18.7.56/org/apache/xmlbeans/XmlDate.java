/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.util.Calendar;
import java.util.Date;
import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlDate
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlDate> Factory = new XmlObjectFactory("_BI_date");
    public static final SchemaType type = Factory.getType();

    public Calendar getCalendarValue();

    public void setCalendarValue(Calendar var1);

    public GDate getGDateValue();

    public void setGDateValue(GDate var1);

    public Date getDateValue();

    public void setDateValue(Date var1);
}

