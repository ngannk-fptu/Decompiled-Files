/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class Description
extends Property
implements Escapable {
    private static final long serialVersionUID = 7287564228220558361L;
    private String value;
    private Validator<Property> validator = new PropertyValidator(Arrays.asList(new ValidationRule(ValidationRule.ValidationType.OneOrLess, "ALTREP", "LANGUAGE")));

    public Description() {
        super("DESCRIPTION", new ParameterList(), new Factory());
    }

    public Description(String aValue) {
        super("DESCRIPTION", new ParameterList(), new Factory());
        this.setValue(aValue);
    }

    public Description(ParameterList aList, String aValue) {
        super("DESCRIPTION", aList, new Factory());
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
    public void validate() throws ValidationException {
        this.validator.validate(this);
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("DESCRIPTION");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Description(parameters, value);
        }

        public Property createProperty() {
            return new Description();
        }
    }
}

