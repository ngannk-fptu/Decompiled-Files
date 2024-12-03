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
public final class CalAdrUri
extends Property {
    public static final PropertyFactory<CalAdrUri> FACTORY = new Factory();
    private static final long serialVersionUID = -6507220241297111022L;
    private final URI uri;

    public CalAdrUri(URI uri, Type ... types) {
        super(Property.Id.CALADRURI);
        this.uri = uri;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public CalAdrUri(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.CALADRURI);
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
    implements PropertyFactory<CalAdrUri> {
        private Factory() {
        }

        @Override
        public CalAdrUri createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new CalAdrUri(params, value);
        }

        @Override
        public CalAdrUri createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

