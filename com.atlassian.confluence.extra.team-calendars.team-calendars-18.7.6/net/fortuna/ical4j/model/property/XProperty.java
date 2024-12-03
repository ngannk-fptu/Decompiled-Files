/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;

public class XProperty
extends Property
implements Escapable {
    private static final long serialVersionUID = 2331763266954894541L;
    private String value;

    public XProperty(String name) {
        super(name, new Factory(name));
    }

    public XProperty(String aName, String aValue) {
        super(aName, new Factory(aName));
        this.setValue(aValue);
    }

    public XProperty(String aName, ParameterList aList, String aValue) {
        super(aName, aList, new Factory(aName));
        this.setValue(aValue);
    }

    @Override
    public final void setValue(String aValue) {
        this.value = aValue;
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    @Override
    public final void validate() throws ValidationException {
        if (!CompatibilityHints.isHintEnabled("ical4j.validation.relaxed") && !this.getName().startsWith("X-")) {
            throw new ValidationException("Invalid name [" + this.getName() + "]. Experimental properties must have the following prefix: " + "X-");
        }
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;
        private final String name;

        public Factory(String name) {
            super(name);
            this.name = name;
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new XProperty(this.name, parameters, value);
        }

        public Property createProperty() {
            return new XProperty(this.name);
        }
    }
}

