/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;

public class Attendee
extends Property {
    private static final long serialVersionUID = 8430929418723298803L;
    private URI calAddress;

    public Attendee() {
        super("ATTENDEE", new Factory());
    }

    public Attendee(String aValue) throws URISyntaxException {
        super("ATTENDEE", new Factory());
        this.setValue(aValue);
    }

    public Attendee(ParameterList aList, String aValue) throws URISyntaxException {
        super("ATTENDEE", aList, new Factory());
        this.setValue(aValue);
    }

    public Attendee(URI aUri) {
        super("ATTENDEE", new Factory());
        this.calAddress = aUri;
    }

    public Attendee(ParameterList aList, URI aUri) {
        super("ATTENDEE", aList, new Factory());
        this.calAddress = aUri;
    }

    @Override
    public final void setValue(String aValue) throws URISyntaxException {
        this.calAddress = Uris.create(aValue);
    }

    @Override
    public final void validate() throws ValidationException {
        Arrays.asList("CUTYPE", "MEMBER", "ROLE", "PARTSTAT", "RSVP", "DELEGATED-TO", "DELEGATED-FROM", "SENT-BY", "CN", "DIR", "LANGUAGE").forEach(parameter -> ParameterValidator.assertOneOrLess(parameter, this.getParameters()));
        ParameterValidator.assertOneOrLess("SCHEDULE-AGENT", this.getParameters());
        ParameterValidator.assertOneOrLess("SCHEDULE-STATUS", this.getParameters());
    }

    public final URI getCalAddress() {
        return this.calAddress;
    }

    @Override
    public final String getValue() {
        return Uris.decode(Strings.valueOf(this.getCalAddress()));
    }

    public final void setCalAddress(URI calAddress) {
        this.calAddress = calAddress;
    }

    @Override
    public final Property copy() {
        return new Attendee(new ParameterList(this.getParameters(), false), this.calAddress);
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("ATTENDEE");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Attendee(parameters, value);
        }

        public Property createProperty() {
            return new Attendee();
        }
    }
}

