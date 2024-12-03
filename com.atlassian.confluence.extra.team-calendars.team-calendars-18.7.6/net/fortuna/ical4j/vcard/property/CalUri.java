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
public final class CalUri
extends Property {
    public static final PropertyFactory<CalUri> FACTORY = new Factory();
    private static final long serialVersionUID = 4821378252642288695L;
    private final URI uri;

    public CalUri(URI uri, Type ... types) {
        super(Property.Id.CALURI);
        this.uri = uri;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public CalUri(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.CALURI);
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
    implements PropertyFactory<CalUri> {
        private Factory() {
        }

        @Override
        public CalUri createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new CalUri(params, value);
        }

        @Override
        public CalUri createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

