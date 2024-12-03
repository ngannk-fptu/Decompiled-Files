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

public class Categories
extends Property {
    private static final long serialVersionUID = -7769987073466681634L;
    private TextList categories;
    private Validator<Property> validator = new PropertyValidator(Arrays.asList(new ValidationRule(ValidationRule.ValidationType.OneOrLess, "LANGUAGE")));

    public Categories() {
        super("CATEGORIES", new ParameterList(), new Factory());
        this.categories = new TextList();
    }

    public Categories(String aValue) {
        super("CATEGORIES", new ParameterList(), new Factory());
        this.setValue(aValue);
    }

    public Categories(ParameterList aList, String aValue) {
        super("CATEGORIES", aList, new Factory());
        this.setValue(aValue);
    }

    public Categories(TextList cList) {
        super("CATEGORIES", new ParameterList(), new Factory());
        this.categories = cList;
    }

    public Categories(ParameterList aList, TextList cList) {
        super("CATEGORIES", aList, new Factory());
        this.categories = cList;
    }

    @Override
    public final void setValue(String aValue) {
        this.categories = new TextList(aValue);
    }

    public final TextList getCategories() {
        return this.categories;
    }

    @Override
    public final String getValue() {
        return this.getCategories().toString();
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
            super("CATEGORIES");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Categories(parameters, value);
        }

        public Property createProperty() {
            return new Categories();
        }
    }
}

