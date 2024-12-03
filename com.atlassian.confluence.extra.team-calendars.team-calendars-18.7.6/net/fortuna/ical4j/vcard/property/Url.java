/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Url
extends Property {
    public static final PropertyFactory<Url> FACTORY = new Factory();
    private static final long serialVersionUID = -6689531541656904891L;
    private final URI uri;

    public Url(URI uri) {
        super(Property.Id.URL);
        this.uri = uri;
    }

    public Url(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.URL, params);
        this.uri = new URI(value);
    }

    public URI getUri() {
        return this.uri;
    }

    @Override
    public String getValue() {
        return Strings.valueOf(this.uri);
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            this.assertPidParameter(param);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Url> {
        private Factory() {
        }

        @Override
        public Url createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new Url(params, value);
        }

        @Override
        public Url createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

