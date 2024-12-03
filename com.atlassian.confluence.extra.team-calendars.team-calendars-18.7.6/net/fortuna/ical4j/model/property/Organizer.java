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

public class Organizer
extends Property {
    private static final long serialVersionUID = -5216965653165090725L;
    private URI calAddress;

    public Organizer() {
        super("ORGANIZER", new Factory());
    }

    public Organizer(String value) throws URISyntaxException {
        super("ORGANIZER", new Factory());
        this.setValue(value);
    }

    public Organizer(ParameterList aList, String aValue) throws URISyntaxException {
        super("ORGANIZER", aList, new Factory());
        this.setValue(aValue);
    }

    public Organizer(URI aUri) {
        super("ORGANIZER", new Factory());
        this.calAddress = aUri;
    }

    public Organizer(ParameterList aList, URI aUri) {
        super("ORGANIZER", aList, new Factory());
        this.calAddress = aUri;
    }

    @Override
    public final void validate() throws ValidationException {
        Arrays.asList("CN", "DIR", "SENT-BY", "LANGUAGE").forEach(parameter -> ParameterValidator.assertOneOrLess(parameter, this.getParameters()));
        ParameterValidator.assertOneOrLess("SCHEDULE-STATUS", this.getParameters());
    }

    public final URI getCalAddress() {
        return this.calAddress;
    }

    @Override
    public final void setValue(String aValue) throws URISyntaxException {
        this.calAddress = Uris.create(aValue);
    }

    @Override
    public final String getValue() {
        return Uris.decode(Strings.valueOf(this.getCalAddress()));
    }

    public final void setCalAddress(URI calAddress) {
        this.calAddress = calAddress;
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("ORGANIZER");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Organizer(parameters, value);
        }

        public Property createProperty() {
            return new Organizer();
        }
    }
}

