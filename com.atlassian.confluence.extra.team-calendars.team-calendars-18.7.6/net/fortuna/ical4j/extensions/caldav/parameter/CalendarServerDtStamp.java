/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.caldav.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class CalendarServerDtStamp
extends Parameter {
    private static final long serialVersionUID = 1438225631470825963L;
    public static final String PARAMETER_NAME = "X-CALENDARSERVER-DTSTAMP";
    private String value;

    public CalendarServerDtStamp(String aValue) {
        super(PARAMETER_NAME, new Factory());
        this.value = Strings.unquote(aValue);
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(CalendarServerDtStamp.PARAMETER_NAME);
        }

        public Parameter createParameter(String value) {
            return new CalendarServerDtStamp(value);
        }
    }
}

