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
public final class MaxInstances
extends Property {
    public static final PropertyFactory<MaxInstances> FACTORY = new Factory();
    private static final long serialVersionUID = 123456789L;
    private final int value;

    public MaxInstances(int val, Type ... types) {
        super(Property.Id.MAXINSTANCES);
        this.value = val;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public MaxInstances(List<Parameter> params, String value) {
        super(Property.Id.MAXINSTANCES, params);
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
    implements PropertyFactory<MaxInstances> {
        private Factory() {
        }

        @Override
        public MaxInstances createProperty(List<Parameter> params, String value) {
            return new MaxInstances(params, value);
        }

        @Override
        public MaxInstances createProperty(Group group, List<Parameter> params, String value) throws ParseException {
            return null;
        }
    }
}

