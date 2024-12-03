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
import net.fortuna.ical4j.vcard.parameter.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Uid
extends Property {
    public static final PropertyFactory<Uid> FACTORY = new Factory();
    private static final long serialVersionUID = -7120539613021006347L;
    private URI uri;
    private String text;

    public Uid(URI uri) {
        super(Property.Id.UID);
        this.uri = uri;
    }

    public Uid(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.UID, params);
        if (Value.TEXT.equals(this.getParameter(Parameter.Id.VALUE))) {
            this.text = value;
            return;
        }
        if (Value.URI.equals(this.getParameter(Parameter.Id.VALUE))) {
            this.uri = new URI(value);
            return;
        }
        try {
            this.uri = new URI(value);
        }
        catch (Throwable t) {
            this.text = value;
        }
    }

    public URI getUri() {
        return this.uri;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String getValue() {
        if (this.text != null) {
            return this.text;
        }
        return Strings.valueOf(this.uri);
    }

    @Override
    public void validate() throws ValidationException {
        this.assertOneOrLess(Parameter.Id.VALUE);
        if (this.getParameters().size() > 1) {
            throw new ValidationException("Illegal parameter count");
        }
        for (Parameter param : this.getParameters()) {
            if (Value.TEXT.equals(param) || Value.URI.equals(param)) continue;
            throw new ValidationException("Illegal parameter [" + (Object)((Object)param.getId()) + "]");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Uid> {
        private Factory() {
        }

        @Override
        public Uid createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new Uid(params, value);
        }

        @Override
        public Uid createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

