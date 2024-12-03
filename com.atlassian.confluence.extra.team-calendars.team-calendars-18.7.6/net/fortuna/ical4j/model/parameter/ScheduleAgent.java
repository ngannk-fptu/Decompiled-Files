/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class ScheduleAgent
extends Parameter {
    private static final long serialVersionUID = 4205758749959461020L;
    private static final String VALUE_SERVER = "SERVER";
    private static final String VALUE_CLIENT = "CLIENT";
    private static final String VALUE_NONE = "NONE";
    public static final ScheduleAgent SERVER = new ScheduleAgent("SERVER");
    public static final ScheduleAgent CLIENT = new ScheduleAgent("CLIENT");
    public static final ScheduleAgent NONE = new ScheduleAgent("NONE");
    private String value;

    public ScheduleAgent(String aValue) {
        super("SCHEDULE-AGENT", new Factory());
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
            super("SCHEDULE-AGENT");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            ScheduleAgent parameter = new ScheduleAgent(value);
            if (SERVER.equals(parameter)) {
                return SERVER;
            }
            if (CLIENT.equals(parameter)) {
                return CLIENT;
            }
            if (NONE.equals(parameter)) {
                return NONE;
            }
            return parameter;
        }
    }
}

