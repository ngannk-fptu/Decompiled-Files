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
public final class Version
extends Property {
    private static final long serialVersionUID = -4345025177285348717L;
    public static final Version VERSION_4_0 = new Version(Collections.unmodifiableList(new ArrayList()), "4.0");
    public static final PropertyFactory<Version> FACTORY = new Factory();
    private final String value;

    public Version(String value) {
        super(Property.Id.VERSION);
        this.value = value;
    }

    private Version(List<Parameter> params, String value) {
        super(Property.Id.VERSION, params);
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
    implements PropertyFactory<Version> {
        private Factory() {
        }

        @Override
        public Version createProperty(List<Parameter> params, String value) {
            if (VERSION_4_0.getValue().equals(value)) {
                return VERSION_4_0;
            }
            return new Version(value);
        }

        @Override
        public Version createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

