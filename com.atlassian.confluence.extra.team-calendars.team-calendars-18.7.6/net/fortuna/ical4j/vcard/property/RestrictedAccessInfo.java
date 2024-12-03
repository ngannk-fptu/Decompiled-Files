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
import net.fortuna.ical4j.vcard.parameter.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RestrictedAccessInfo
extends Property {
    public static final PropertyFactory<RestrictedAccessInfo> FACTORY = new Factory();
    private static final long serialVersionUID = 123L;
    private URI uri;
    private String value;

    public RestrictedAccessInfo(URI uri, Type ... types) {
        super(Property.Id.RESTRICTEDACCESSINFO);
        this.uri = uri;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public RestrictedAccessInfo(String val, Type ... types) throws URISyntaxException {
        super(Property.Id.RESTRICTEDACCESSINFO);
        this.value = val;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public RestrictedAccessInfo(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.RESTRICTEDACCESSINFO, params);
        Value v = (Value)this.getParameter(Parameter.Id.VALUE);
        if (v == null | v.getValue().toLowerCase().equals("uri")) {
            this.uri = new URI(value);
        } else {
            this.value = value;
        }
    }

    public URI getUri() {
        return this.uri;
    }

    @Override
    public String getValue() {
        if (this.value != null) {
            return this.value;
        }
        return Strings.valueOf(this.uri);
    }

    @Override
    public void validate() throws ValidationException {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<RestrictedAccessInfo> {
        private Factory() {
        }

        @Override
        public RestrictedAccessInfo createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new RestrictedAccessInfo(params, value);
        }

        @Override
        public RestrictedAccessInfo createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

