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

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SortString
extends Property {
    public static final PropertyFactory<SortString> FACTORY = new Factory();
    private static final long serialVersionUID = 980796364808362907L;
    private final String value;

    public SortString(String value) {
        super(Property.Id.SORT_STRING);
        this.value = value;
    }

    public SortString(List<Parameter> params, String value) {
        super(Property.Id.SORT_STRING, params);
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            this.assertTextParameter(param);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<SortString> {
        private Factory() {
        }

        @Override
        public SortString createProperty(List<Parameter> params, String value) {
            return new SortString(params, value);
        }

        @Override
        public SortString createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

