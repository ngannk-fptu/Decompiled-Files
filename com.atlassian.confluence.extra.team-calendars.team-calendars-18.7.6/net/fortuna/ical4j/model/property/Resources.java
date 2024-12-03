/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TextList;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class Resources
extends Property {
    private static final long serialVersionUID = -848562477226746807L;
    private TextList resources;
    private final Validator<Property> validator = new PropertyValidator(Arrays.asList(new ValidationRule(ValidationRule.ValidationType.OneOrLess, "ALTREP", "LANGUAGE")));

    public Resources() {
        super("RESOURCES", new ParameterList(), new Factory());
        this.resources = new TextList();
    }

    public Resources(ParameterList aList, String aValue) {
        super("RESOURCES", aList, new Factory());
        this.setValue(aValue);
    }

    public Resources(TextList rList) {
        super("RESOURCES", new ParameterList(), new Factory());
        this.resources = rList;
    }

    public Resources(ParameterList aList, TextList rList) {
        super("RESOURCES", aList, new Factory());
        this.resources = rList;
    }

    public final TextList getResources() {
        return this.resources;
    }

    @Override
    public final void setValue(String aValue) {
        this.resources = new TextList(aValue);
    }

    @Override
    public final String getValue() {
        return this.getResources().toString();
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
            super("RESOURCES");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Resources(parameters, value);
        }

        public Property createProperty() {
            return new Resources();
        }
    }
}

