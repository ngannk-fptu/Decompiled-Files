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

public class Url
extends Property {
    private static final long serialVersionUID = 1092576402256525737L;
    private URI uri;

    public Url() {
        super("URL", new Factory());
    }

    public Url(ParameterList aList, String aValue) throws URISyntaxException {
        super("URL", aList, new Factory());
        this.setValue(aValue);
    }

    public Url(URI aUri) {
        super("URL", new Factory());
        this.uri = aUri;
    }

    public Url(ParameterList aList, URI aUri) {
        super("URL", aList, new Factory());
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
            super("URL");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Url(parameters, value);
        }

        public Property createProperty() {
            return new Url();
        }
    }
}

