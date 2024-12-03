/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.LocationTypeList;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class LocationType
extends Property {
    private static final long serialVersionUID = -3541686430899510312L;
    private LocationTypeList locationTypes;
    private final Validator<Property> validator = new PropertyValidator(Arrays.asList(new ValidationRule(ValidationRule.ValidationType.OneOrLess, "LANGUAGE")));

    public LocationType() {
        super("LOCATION-TYPE", new ParameterList(), new Factory());
        this.locationTypes = new LocationTypeList();
    }

    public LocationType(String aValue) {
        super("LOCATION-TYPE", new ParameterList(), new Factory());
        this.setValue(aValue);
    }

    public LocationType(ParameterList aList, String aValue) {
        super("LOCATION-TYPE", aList, new Factory());
        this.setValue(aValue);
    }

    public LocationType(LocationTypeList cList) {
        super("LOCATION-TYPE", new ParameterList(), new Factory());
        this.locationTypes = cList;
    }

    public LocationType(ParameterList aList, LocationTypeList cList) {
        super("LOCATION-TYPE", aList, new Factory());
        this.locationTypes = cList;
    }

    @Override
    public final void setValue(String aValue) {
        this.locationTypes = new LocationTypeList(aValue);
    }

    public final LocationTypeList getLocationTypes() {
        return this.locationTypes;
    }

    @Override
    public final String getValue() {
        return this.getLocationTypes().toString();
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
            super("LOCATION-TYPE");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new LocationType(parameters, value);
        }

        public Property createProperty() {
            return new LocationType();
        }
    }
}

