/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Gender
extends Property {
    private static final long serialVersionUID = -2739534182576803750L;
    public static final Gender MALE = new Gender(Collections.unmodifiableList(new ArrayList()), "M");
    public static final Gender FEMALE = new Gender(Collections.unmodifiableList(new ArrayList()), "F");
    public static final PropertyFactory<Gender> FACTORY = new Factory();
    private final String value;

    public Gender(String value) {
        super(Property.Id.GENDER);
        this.value = value;
    }

    private Gender(List<Parameter> params, String value) {
        super(Property.Id.GENDER, params);
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
    implements PropertyFactory<Gender> {
        private Factory() {
        }

        @Override
        public Gender createProperty(List<Parameter> params, String value) {
            Gender property = null;
            property = FEMALE.getValue().equals(value) ? FEMALE : (MALE.getValue().equals(value) ? MALE : new Gender(value));
            return property;
        }

        @Override
        public Gender createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

