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
public final class NoCost
extends Property {
    public static final PropertyFactory<NoCost> FACTORY = new Factory();
    private static final long serialVersionUID = 123456789L;
    private final boolean value;

    public NoCost(boolean val, Type ... types) {
        super(Property.Id.NOCOST);
        this.value = val;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public NoCost(List<Parameter> params, String value) {
        super(Property.Id.NOCOST, params);
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
    implements PropertyFactory<NoCost> {
        private Factory() {
        }

        @Override
        public NoCost createProperty(List<Parameter> params, String value) {
            return new NoCost(params, value);
        }

        @Override
        public NoCost createProperty(Group group, List<Parameter> params, String value) throws ParseException {
            return null;
        }
    }
}

