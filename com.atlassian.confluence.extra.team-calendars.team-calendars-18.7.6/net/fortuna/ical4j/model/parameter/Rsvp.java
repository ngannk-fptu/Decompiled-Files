/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;

public class Rsvp
extends Parameter {
    private static final long serialVersionUID = -5381653882942018012L;
    private static final String VALUE_TRUE = "TRUE";
    private static final String VALUE_FALSE = "FALSE";
    public static final Rsvp TRUE = new Rsvp("TRUE");
    public static final Rsvp FALSE = new Rsvp("FALSE");
    private Boolean rsvp;

    public Rsvp(String aValue) {
        this(Boolean.valueOf(aValue));
    }

    public Rsvp(Boolean aValue) {
        super("RSVP", new Factory());
        this.rsvp = aValue;
    }

    public final Boolean getRsvp() {
        return this.rsvp;
    }

    @Override
    public final String getValue() {
        if (this.rsvp.booleanValue()) {
            return VALUE_TRUE;
        }
        return VALUE_FALSE;
    }

    public final Parameter copy() {
        if (this.rsvp.booleanValue()) {
            return TRUE;
        }
        return FALSE;
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("RSVP");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            Rsvp parameter = new Rsvp(value);
            if (TRUE.equals(parameter)) {
                parameter = TRUE;
            } else if (FALSE.equals(parameter)) {
                parameter = FALSE;
            }
            return parameter;
        }
    }
}

