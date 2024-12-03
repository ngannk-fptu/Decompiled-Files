/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.model.UtcOffset;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;
import net.fortuna.ical4j.vcard.parameter.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Tz
extends Property {
    public static final PropertyFactory<Tz> FACTORY = new Factory();
    private static final long serialVersionUID = 930436197799477318L;
    private UtcOffset offset;
    private String text;

    public Tz(UtcOffset offset) {
        super(Property.Id.TZ);
        this.offset = offset;
    }

    public Tz(String text) {
        super(Property.Id.TZ);
        this.text = text;
        this.getParameters().add(Value.TEXT);
    }

    public Tz(List<Parameter> params, String value) {
        super(Property.Id.TZ, params);
        if (Value.TEXT.equals(this.getParameter(Parameter.Id.VALUE))) {
            this.text = value;
        } else {
            this.offset = new UtcOffset(value);
        }
    }

    public UtcOffset getOffset() {
        return this.offset;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String getValue() {
        String value = null;
        if (Value.TEXT.equals(this.getParameter(Parameter.Id.VALUE))) {
            value = this.text;
        } else if (this.offset != null) {
            value = this.offset.toString();
        }
        return value;
    }

    @Override
    public void validate() throws ValidationException {
        this.assertParametersEmpty();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Tz> {
        private Factory() {
        }

        @Override
        public Tz createProperty(List<Parameter> params, String value) {
            return new Tz(params, value);
        }

        @Override
        public Tz createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

