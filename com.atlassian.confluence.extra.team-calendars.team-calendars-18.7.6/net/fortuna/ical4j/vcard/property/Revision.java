/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Revision
extends Property {
    public static final PropertyFactory<Revision> FACTORY = new Factory();
    private static final long serialVersionUID = -1342640230576672871L;
    private Date date;

    public Revision(Date date) {
        super(Property.Id.REV);
        this.date = date;
    }

    public Revision(List<Parameter> params, String value) throws ParseException {
        super(Property.Id.REV, params);
        try {
            this.date = new DateTime(value);
        }
        catch (ParseException e) {
            try {
                this.date = new Date(value);
            }
            catch (ParseException e2) {
                try {
                    this.date = new DateTime(value, "yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'", true);
                }
                catch (ParseException e3) {
                    this.date = new Date(value, "yyyy'-'MM'-'dd");
                }
            }
        }
    }

    public Date getDate() {
        return this.date;
    }

    @Override
    public String getValue() {
        return Strings.valueOf(this.date);
    }

    @Override
    public void validate() throws ValidationException {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Revision> {
        private Factory() {
        }

        @Override
        public Revision createProperty(List<Parameter> params, String value) throws ParseException {
            return new Revision(params, value);
        }

        @Override
        public Revision createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

