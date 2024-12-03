/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import net.fortuna.ical4j.validate.ValidationException;

public class TzUrl
extends Property {
    private static final long serialVersionUID = 9106100107954797406L;
    private URI uri;

    public TzUrl() {
        super("TZURL", new Factory());
    }

    public TzUrl(ParameterList aList, String aValue) throws URISyntaxException {
        super("TZURL", aList, new Factory());
        this.setValue(aValue);
    }

    public TzUrl(URI aUri) {
        super("TZURL", new Factory());
        this.uri = aUri;
    }

    public TzUrl(ParameterList aList, URI aUri) {
        super("TZURL", aList, new Factory());
        this.uri = aUri;
    }

    public final URI getUri() {
        return this.uri;
    }

    @Override
    public final void setValue(String aValue) throws URISyntaxException {
        this.uri = Uris.create(aValue);
    }

    @Override
    public final String getValue() {
        return Uris.decode(Strings.valueOf(this.getUri()));
    }

    public final void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("TZURL");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new TzUrl(parameters, value);
        }

        public Property createProperty() {
            return new TzUrl();
        }
    }
}

