/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

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
public final class Restricted
extends Property {
    public static final PropertyFactory<Restricted> FACTORY = new Factory();
    private static final long serialVersionUID = 123456789L;
    private final boolean value;

    public Restricted(boolean val, Type ... types) {
        super(Property.Id.RESTRICTED);
        this.value = val;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public Restricted(List<Parameter> params, String value) {
        super(Property.Id.RESTRICTED, params);
        this.value = Boolean.parseBoolean(value);
    }

    public boolean getBoolean() {
        return this.value;
    }

    @Override
    public String getValue() {
        return Strings.valueOf(this.value);
    }

    @Override
    public void validate() throws ValidationException {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Restricted> {
        private Factory() {
        }

        @Override
        public Restricted createProperty(List<Parameter> params, String value) {
            return new Restricted(params, value);
        }

        @Override
        public Restricted createProperty(Group group, List<Parameter> params, String value) throws ParseException {
            return null;
        }
    }
}

