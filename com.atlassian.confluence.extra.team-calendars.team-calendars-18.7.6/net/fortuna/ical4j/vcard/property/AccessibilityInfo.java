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
public final class AccessibilityInfo
extends Property {
    public static final PropertyFactory<AccessibilityInfo> FACTORY = new Factory();
    private static final long serialVersionUID = 1234L;
    private URI uri;
    private String value;

    public AccessibilityInfo(URI uri, Type ... types) {
        super(Property.Id.ACCESSIBILITYINFO);
        this.uri = uri;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public AccessibilityInfo(String val, Type ... types) throws URISyntaxException {
        super(Property.Id.BOOKINGINFO);
        this.value = val;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public AccessibilityInfo(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.ACCESSIBILITYINFO, params);
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
    implements PropertyFactory<AccessibilityInfo> {
        private Factory() {
        }

        @Override
        public AccessibilityInfo createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new AccessibilityInfo(params, value);
        }

        @Override
        public AccessibilityInfo createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

