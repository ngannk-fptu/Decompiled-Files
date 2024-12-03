/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
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
import org.apache.commons.lang.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Telephone
extends Property {
    private static final long serialVersionUID = -7747040131815077325L;
    private static final String TEL_SCHEME = "tel";
    public static final PropertyFactory<Telephone> FACTORY = new Factory();
    private URI uri;
    private String value;

    public Telephone(URI uri, Type ... types) {
        this(null, uri, types);
    }

    public Telephone(Group group, URI uri, Type ... types) {
        super(group, Property.Id.TEL);
        this.uri = this.normalise(uri);
        this.getParameters().add(Value.URI);
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public Telephone(String value, Type ... types) {
        super(null, Property.Id.TEL);
        this.value = value;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public Telephone(List<Parameter> params, String value) throws URISyntaxException {
        this(null, params, value);
    }

    public Telephone(Group group, List<Parameter> params, String value) throws URISyntaxException {
        super(group, Property.Id.TEL, params);
        if (Value.URI.equals(this.getParameter(Parameter.Id.VALUE))) {
            this.uri = this.normalise(new URI(value.trim().replaceAll("\\s+", "-")));
        } else {
            this.value = value;
        }
    }

    private URI normalise(URI uri) {
        URI retVal = null;
        if (uri.getScheme() == null && StringUtils.isNotEmpty(uri.getSchemeSpecificPart())) {
            try {
                retVal = new URI(TEL_SCHEME, uri.getSchemeSpecificPart(), uri.getFragment());
            }
            catch (URISyntaxException e) {
                retVal = uri;
            }
        } else {
            retVal = uri;
        }
        return retVal;
    }

    public URI getUri() {
        return this.uri;
    }

    @Override
    public String getValue() {
        if (this.uri != null) {
            return Strings.valueOf(this.uri);
        }
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            Parameter.Id id = param.getId();
            if (Parameter.Id.PID.equals((Object)id) || Parameter.Id.PREF.equals((Object)id) || Parameter.Id.TYPE.equals((Object)id)) continue;
            throw new ValidationException(MessageFormat.format("Illegal parameter [{0}]", new Object[]{id}));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Telephone> {
        private Factory() {
        }

        @Override
        public Telephone createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new Telephone(params, value);
        }

        @Override
        public Telephone createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return new Telephone(group, params, value);
        }
    }
}

