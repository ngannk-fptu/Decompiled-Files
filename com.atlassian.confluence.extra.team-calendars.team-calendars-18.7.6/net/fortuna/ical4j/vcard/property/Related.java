/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
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
public final class Related
extends Property {
    public static final PropertyFactory<Related> FACTORY = new Factory();
    private static final long serialVersionUID = -3319959600372278036L;
    private URI uri;
    private String text;

    public Related(String text, Type ... types) {
        super(Property.Id.RELATED);
        this.text = text;
        this.getParameters().add(Value.TEXT);
        this.getParameters().addAll(Arrays.asList(types));
    }

    public Related(URI uri, Type ... types) {
        super(Property.Id.RELATED);
        this.uri = uri;
        this.getParameters().addAll(Arrays.asList(types));
    }

    public Related(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.RELATED, params);
        if (Value.TEXT.equals(this.getParameter(Parameter.Id.VALUE))) {
            this.text = value;
        } else {
            this.uri = new URI(value);
        }
    }

    public URI getUri() {
        return this.uri;
    }

    @Override
    public String getValue() {
        if (Value.TEXT.equals(this.getParameter(Parameter.Id.VALUE))) {
            return this.text;
        }
        return Strings.valueOf(this.uri);
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            try {
                this.assertTypeParameter(param);
            }
            catch (ValidationException ve) {
                this.assertPidParameter(param);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Related> {
        private Factory() {
        }

        @Override
        public Related createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new Related(params, value);
        }

        @Override
        public Related createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

