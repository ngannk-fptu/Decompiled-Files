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
import net.fortuna.ical4j.vcard.parameter.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AutoSchedule
extends Property {
    public static final PropertyFactory<AutoSchedule> FACTORY = new Factory();
    private static final long serialVersionUID = 123456789L;
    private final String value;

    public AutoSchedule(String val, Type ... types) {
        super(Property.Id.AUTOSCHEDULE);
        this.value = val;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public AutoSchedule(List<Parameter> params, String value) {
        super(Property.Id.AUTOSCHEDULE, params);
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<AutoSchedule> {
        private Factory() {
        }

        @Override
        public AutoSchedule createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new AutoSchedule(params, value);
        }

        @Override
        public AutoSchedule createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

