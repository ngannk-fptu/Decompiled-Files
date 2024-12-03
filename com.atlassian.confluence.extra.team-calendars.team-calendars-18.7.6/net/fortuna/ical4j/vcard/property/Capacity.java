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
public final class Capacity
extends Property {
    public static final PropertyFactory<Capacity> FACTORY = new Factory();
    private static final long serialVersionUID = 123456789L;
    private final int value;

    public Capacity(int val, Type ... types) {
        super(Property.Id.CAPACITY);
        this.value = val;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public Capacity(List<Parameter> params, String value) {
        super(Property.Id.CAPACITY, params);
        this.value = Integer.parseInt(value);
    }

    public int getInteger() {
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
    implements PropertyFactory<Capacity> {
        private Factory() {
        }

        @Override
        public Capacity createProperty(List<Parameter> params, String value) {
            return new Capacity(params, value);
        }

        @Override
        public Capacity createProperty(Group group, List<Parameter> params, String value) throws ParseException {
            return null;
        }
    }
}

