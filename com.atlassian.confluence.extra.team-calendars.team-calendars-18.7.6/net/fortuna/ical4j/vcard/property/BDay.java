/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Escapable;
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
public final class BDay
extends Property
implements Escapable {
    private static final long serialVersionUID = 4298026868242865633L;
    public static final PropertyFactory<BDay> FACTORY = new Factory();
    private Date date;
    private String text;

    public BDay(Date date) {
        super(Property.Id.BDAY);
        this.date = date;
    }

    public BDay(String text) {
        super(Property.Id.BDAY);
        this.text = text;
        this.getParameters().add(Value.TEXT);
    }

    public BDay(List<Parameter> params, String value) throws ParseException {
        super(Property.Id.BDAY, params);
        if (Value.TEXT.equals(this.getParameter(Parameter.Id.VALUE))) {
            this.text = value;
            return;
        }
        try {
            if (!value.contains("T")) {
                this.date = new Date(value);
                return;
            }
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        try {
            this.date = new DateTime(value);
            return;
        }
        catch (ParseException parseException) {
            try {
                this.date = new Date(value, "yyyy'-'MM'-'dd");
            }
            catch (ParseException e3) {
                this.date = new DateTime(value, "yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'", true);
            }
            return;
        }
    }

    public Date getDate() {
        return this.date;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String getValue() {
        if (Value.TEXT.equals(this.getParameter(Parameter.Id.VALUE))) {
            return this.text;
        }
        return Strings.valueOf(this.date);
    }

    @Override
    public void validate() throws ValidationException {
        this.assertOneOrLess(Parameter.Id.VALUE);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<BDay> {
        private Factory() {
        }

        @Override
        public BDay createProperty(List<Parameter> params, String value) throws ParseException {
            return new BDay(params, Strings.unescape(value));
        }

        @Override
        public BDay createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

