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
public final class Clazz
extends Property {
    private static final long serialVersionUID = -3339099487456754606L;
    public static final Clazz PUBLIC = new Clazz(Collections.unmodifiableList(new ArrayList()), "PUBLIC");
    public static final Clazz PRIVATE = new Clazz(Collections.unmodifiableList(new ArrayList()), "PRIVATE");
    public static final Clazz CONFIDENTIAL = new Clazz(Collections.unmodifiableList(new ArrayList()), "CONFIDENTIAL");
    public static final PropertyFactory<Clazz> FACTORY = new Factory();
    private final String value;

    public Clazz(String value) {
        super(Property.Id.CLASS);
        this.value = value;
    }

    public Clazz(List<Parameter> params, String value) {
        super(Property.Id.CLASS, params);
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
        this.assertParametersEmpty();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Clazz> {
        private Factory() {
        }

        @Override
        public Clazz createProperty(List<Parameter> params, String value) {
            Clazz property = null;
            property = CONFIDENTIAL.getValue().equals(value) ? CONFIDENTIAL : (PRIVATE.getValue().equals(value) ? PRIVATE : (PUBLIC.getValue().equals(value) ? PUBLIC : new Clazz(params, value)));
            return property;
        }

        @Override
        public Clazz createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

