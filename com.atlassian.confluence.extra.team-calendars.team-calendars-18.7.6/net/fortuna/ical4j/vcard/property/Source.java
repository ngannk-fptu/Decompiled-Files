/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;
import net.fortuna.ical4j.vcard.parameter.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Source
extends Property {
    public static final PropertyFactory<Source> FACTORY = new Factory();
    private static final long serialVersionUID = -8097388189864368448L;
    private final URI uri;

    public Source(URI uri) {
        super(Property.Id.SOURCE);
        this.uri = uri;
    }

    public Source(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.SOURCE, params);
        this.uri = new URI(value);
    }

    public URI getUri() {
        return this.uri;
    }

    @Override
    public String getValue() {
        return this.uri.toString();
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            if (Value.URI.equals(param) || Parameter.Id.EXTENDED.equals((Object)param.getId()) || Parameter.Id.PID.equals((Object)param.getId())) continue;
            throw new ValidationException("Illegal parameter [" + (Object)((Object)param.getId()) + "]");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Source> {
        private Factory() {
        }

        @Override
        public Source createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new Source(params, value);
        }

        @Override
        public Source createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

