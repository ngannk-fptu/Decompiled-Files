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

public class Method
extends Property {
    private static final long serialVersionUID = 7220956532685378719L;
    public static final Method PUBLISH = new ImmutableMethod("PUBLISH");
    public static final Method REQUEST = new ImmutableMethod("REQUEST");
    public static final Method REPLY = new ImmutableMethod("REPLY");
    public static final Method ADD = new ImmutableMethod("ADD");
    public static final Method CANCEL = new ImmutableMethod("CANCEL");
    public static final Method REFRESH = new ImmutableMethod("REFRESH");
    public static final Method COUNTER = new ImmutableMethod("COUNTER");
    public static final Method DECLINE_COUNTER = new ImmutableMethod("DECLINECOUNTER");
    private String value;

    public Method() {
        super("METHOD", new Factory());
    }

    public Method(String aValue) {
        super("METHOD", new Factory());
        this.value = aValue;
    }

    public Method(ParameterList aList, String aValue) {
        super("METHOD", aList, new Factory());
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
    implements PropertyFactory<Method> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("METHOD");
        }

        @Override
        public Method createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            Method method = ADD.getValue().equals(value) ? ADD : (CANCEL.getValue().equals(value) ? CANCEL : (COUNTER.getValue().equals(value) ? COUNTER : (DECLINE_COUNTER.getValue().equals(value) ? DECLINE_COUNTER : (PUBLISH.getValue().equals(value) ? PUBLISH : (REFRESH.getValue().equals(value) ? REFRESH : (REPLY.getValue().equals(value) ? REPLY : (REQUEST.getValue().equals(value) ? REQUEST : new Method(parameters, value))))))));
            return method;
        }

        @Override
        public Method createProperty() {
            return new Method();
        }
    }

    private static final class ImmutableMethod
    extends Method {
        private static final long serialVersionUID = 5332607957381969713L;

        private ImmutableMethod(String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
    }
}

