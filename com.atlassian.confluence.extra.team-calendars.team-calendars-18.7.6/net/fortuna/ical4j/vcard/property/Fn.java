/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Fn
extends Property {
    private static final long serialVersionUID = -3576886478408668365L;
    public static final PropertyFactory<Fn> FACTORY = new Factory();
    private final String value;

    public Fn(String value) {
        super(Property.Id.FN);
        this.value = value;
    }

    public Fn(List<Parameter> params, String value) {
        super(Property.Id.FN, params);
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            this.assertTextParameter(param);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Fn> {
        private Factory() {
        }

        @Override
        public Fn createProperty(List<Parameter> params, String value) {
            return new Fn(value);
        }

        @Override
        public Fn createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

