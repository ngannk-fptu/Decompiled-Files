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
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;

public class Priority
extends Property {
    private static final long serialVersionUID = -5654367843953827397L;
    public static final Priority UNDEFINED = new ImmutablePriority(0);
    public static final Priority HIGH = new ImmutablePriority(1);
    public static final Priority MEDIUM = new ImmutablePriority(5);
    public static final Priority LOW = new ImmutablePriority(9);
    private int level;

    public Priority() {
        super("PRIORITY", new Factory());
        this.level = UNDEFINED.getLevel();
    }

    public Priority(ParameterList aList, String aValue) {
        super("PRIORITY", aList, new Factory());
        try {
            this.level = Integer.parseInt(aValue);
        }
        catch (NumberFormatException e) {
            if (CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) {
                this.level = Priority.UNDEFINED.level;
            }
            throw e;
        }
    }

    public Priority(int aLevel) {
        super("PRIORITY", new Factory());
        this.level = aLevel;
    }

    public Priority(ParameterList aList, int aLevel) {
        super("PRIORITY", aList, new Factory());
        this.level = aLevel;
    }

    public final int getLevel() {
        return this.level;
    }

    @Override
    public void setValue(String aValue) {
        this.level = Integer.parseInt(aValue);
    }

    @Override
    public final String getValue() {
        return String.valueOf(this.getLevel());
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<Priority> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("PRIORITY");
        }

        @Override
        public Priority createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            Priority priority = HIGH.getValue().equals(value) ? HIGH : (MEDIUM.getValue().equals(value) ? MEDIUM : (LOW.getValue().equals(value) ? LOW : (UNDEFINED.getValue().equals(value) ? UNDEFINED : new Priority(parameters, value))));
            return priority;
        }

        @Override
        public Priority createProperty() {
            return new Priority();
        }
    }

    private static final class ImmutablePriority
    extends Priority {
        private static final long serialVersionUID = 5884973714694108418L;

        private ImmutablePriority(int level) {
            super(new ParameterList(true), level);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }

        @Override
        public void setLevel(int level) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

