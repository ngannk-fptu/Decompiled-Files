/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Name
extends Property
implements Escapable {
    public static final PropertyFactory<Name> FACTORY = new Factory();
    private static final long serialVersionUID = -3524639290151277814L;
    private final String value;

    public Name(String value) {
        super(Property.Id.NAME);
        this.value = value;
    }

    public Name(List<Parameter> params, String value) {
        super(Property.Id.NAME, params);
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
    implements PropertyFactory<Name> {
        private Factory() {
        }

        @Override
        public Name createProperty(List<Parameter> params, String value) {
            return new Name(params, Strings.unescape(value));
        }

        @Override
        public Name createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

