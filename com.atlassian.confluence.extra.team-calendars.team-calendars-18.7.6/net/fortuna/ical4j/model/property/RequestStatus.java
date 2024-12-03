/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.StringTokenizer;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class RequestStatus
extends Property {
    private static final long serialVersionUID = -3273944031884755345L;
    public static final String PRELIM_SUCCESS = "1";
    public static final String SUCCESS = "2";
    public static final String CLIENT_ERROR = "3";
    public static final String SCHEDULING_ERROR = "4";
    private String statusCode;
    private String description;
    private String exData;
    private final Validator<Property> validator = new PropertyValidator(Arrays.asList(new ValidationRule(ValidationRule.ValidationType.OneOrLess, "LANGUAGE")));

    public RequestStatus() {
        super("REQUEST-STATUS", new ParameterList(), new Factory());
    }

    public RequestStatus(ParameterList aList, String aValue) {
        super("REQUEST-STATUS", aList, new Factory());
        this.setValue(aValue);
    }

    public RequestStatus(String aStatusCode, String aDescription, String data) {
        super("REQUEST-STATUS", new ParameterList(), new Factory());
        this.statusCode = aStatusCode;
        this.description = aDescription;
        this.exData = data;
    }

    public RequestStatus(ParameterList aList, String aStatusCode, String aDescription, String data) {
        super("REQUEST-STATUS", aList, new Factory());
        this.statusCode = aStatusCode;
        this.description = aDescription;
        this.exData = data;
    }

    public final String getDescription() {
        return this.description;
    }

    public final String getExData() {
        return this.exData;
    }

    public final String getStatusCode() {
        return this.statusCode;
    }

    @Override
    public final void setValue(String aValue) {
        StringTokenizer t = new StringTokenizer(aValue, ";");
        if (t.hasMoreTokens()) {
            this.statusCode = t.nextToken();
        }
        if (t.hasMoreTokens()) {
            this.description = t.nextToken();
        }
        if (t.hasMoreTokens()) {
            this.exData = t.nextToken();
        }
    }

    @Override
    public final String getValue() {
        StringBuilder b = new StringBuilder();
        if (this.getStatusCode() != null) {
            b.append(this.getStatusCode());
        }
        if (this.getDescription() != null) {
            b.append(';');
            b.append(this.getDescription());
        }
        if (this.getExData() != null) {
            b.append(';');
            b.append(this.getExData());
        }
        return b.toString();
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final void setExData(String exData) {
        this.exData = exData;
    }

    public final void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
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
            super("REQUEST-STATUS");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new RequestStatus(parameters, value);
        }

        public Property createProperty() {
            return new RequestStatus();
        }
    }
}

