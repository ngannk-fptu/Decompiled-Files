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
import net.fortuna.ical4j.vcard.parameter.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class FbUrl
extends Property {
    public static final PropertyFactory<FbUrl> FACTORY = new Factory();
    private static final long serialVersionUID = 7406097765207265428L;
    private final URI uri;

    public FbUrl(URI uri, Type ... types) {
        super(Property.Id.FBURL);
        this.uri = uri;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public FbUrl(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.FBURL, params);
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
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<FbUrl> {
        private Factory() {
        }

        @Override
        public FbUrl createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new FbUrl(params, value);
        }

        @Override
        public FbUrl createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

