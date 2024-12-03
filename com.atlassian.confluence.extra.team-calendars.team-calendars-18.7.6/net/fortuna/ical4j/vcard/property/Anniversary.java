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
public final class Anniversary
extends Property
implements Escapable {
    private static final long serialVersionUID = 4298026868242865633L;
    public static final PropertyFactory<Anniversary> FACTORY = new Factory();
    private Date date;
    private String text;

    public Anniversary(Date date) {
        super(Property.Id.ANNIVERSARY);
        this.date = date;
    }

    public Anniversary(String text) {
        super(Property.Id.ANNIVERSARY);
        this.text = text;
        this.getParameters().add(Value.TEXT);
    }

    public Anniversary(List<Parameter> params, String value) throws ParseException {
        super(Property.Id.ANNIVERSARY, params);
        if (Value.TEXT.equals(this.getParameter(Parameter.Id.VALUE))) {
            this.text = value;
        } else {
            try {
                this.date = new Date(value);
            }
            catch (ParseException e) {
                try {
                    this.date = new DateTime(value);
                }
                catch (ParseException e2) {
                    try {
                        this.date = new Date(value, "yyyy'-'MM'-'dd");
                    }
                    catch (ParseException e3) {
                        this.date = new DateTime(value, "yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'", true);
                    }
                }
            }
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
        if (this.getParameters().size() > 1) {
            throw new ValidationException("Illegal parameter count");
        }
        for (Parameter param : this.getParameters()) {
            if (Value.TEXT.equals(param)) continue;
            throw new ValidationException("Illegal parameter [" + (Object)((Object)param.getId()) + "]");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Anniversary> {
        private Factory() {
        }

        @Override
        public Anniversary createProperty(List<Parameter> params, String value) throws ParseException {
            return new Anniversary(params, Strings.unescape(value));
        }

        @Override
        public Anniversary createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

