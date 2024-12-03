/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;

public class Action
extends Property {
    private static final long serialVersionUID = -2353353838411753712L;
    public static final Action AUDIO = new ImmutableAction("AUDIO");
    public static final Action DISPLAY = new ImmutableAction("DISPLAY");
    public static final Action EMAIL = new ImmutableAction("EMAIL");
    public static final Action PROCEDURE = new ImmutableAction("PROCEDURE");
    private String value;

    public Action() {
        super("ACTION", new Factory());
    }

    public Action(String aValue) {
        super("ACTION", new Factory());
        this.value = aValue;
    }

    public Action(ParameterList aList, String aValue) {
        super("ACTION", aList, new Factory());
        this.value = aValue;
    }

    @Override
    public void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<Action> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("ACTION");
        }

        @Override
        public Action createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            Action action = AUDIO.getValue().equals(value) ? AUDIO : (DISPLAY.getValue().equals(value) ? DISPLAY : (EMAIL.getValue().equals(value) ? EMAIL : (PROCEDURE.getValue().equals(value) ? PROCEDURE : new Action(parameters, value))));
            return action;
        }

        @Override
        public Action createProperty() {
            return new Action();
        }
    }

    private static final class ImmutableAction
    extends Action {
        private static final long serialVersionUID = -2752235951243969905L;

        private ImmutableAction(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

