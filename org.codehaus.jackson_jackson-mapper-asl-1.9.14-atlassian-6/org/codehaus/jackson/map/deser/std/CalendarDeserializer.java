/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class CalendarDeserializer
extends StdScalarDeserializer<Calendar> {
    protected final Class<? extends Calendar> _calendarClass;

    public CalendarDeserializer() {
        this((Class<? extends Calendar>)null);
    }

    public CalendarDeserializer(Class<? extends Calendar> cc) {
        super(Calendar.class);
        this._calendarClass = cc;
    }

    @Override
    public Calendar deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Date d = this._parseDate(jp, ctxt);
        if (d == null) {
            return null;
        }
        if (this._calendarClass == null) {
            return ctxt.constructCalendar(d);
        }
        try {
            Calendar c = this._calendarClass.newInstance();
            c.setTimeInMillis(d.getTime());
            return c;
        }
        catch (Exception e) {
            throw ctxt.instantiationException(this._calendarClass, e);
        }
    }
}

