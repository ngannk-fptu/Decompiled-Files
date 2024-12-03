/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.model.Dur;
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
public final class BookingWindowStart
extends Property {
    public static final PropertyFactory<BookingWindowStart> FACTORY = new Factory();
    private static final long serialVersionUID = 123456789L;
    private final Dur value;

    public BookingWindowStart(Dur val, Type ... types) {
        super(Property.Id.BOOKINGWINDOWSTART);
        this.value = val;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public BookingWindowStart(List<Parameter> params, String value) {
        super(Property.Id.BOOKINGWINDOWSTART, params);
        this.value = new Dur(value);
    }

    public Dur getUri() {
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
    implements PropertyFactory<BookingWindowStart> {
        private Factory() {
        }

        @Override
        public BookingWindowStart createProperty(List<Parameter> params, String value) {
            return new BookingWindowStart(params, value);
        }

        @Override
        public BookingWindowStart createProperty(Group group, List<Parameter> params, String value) throws ParseException {
            return null;
        }
    }
}

