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
public final class Birth
extends Property
implements Escapable {
    public static final PropertyFactory<Birth> FACTORY = new Factory();
    private static final long serialVersionUID = -3204898745557127754L;
    private final String value;

    public Birth(String value) {
        super(Property.Id.BIRTH);
        this.value = value;
    }

    public Birth(List<Parameter> params, String value) {
        super(Property.Id.BIRTH, params);
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
    implements PropertyFactory<Birth> {
        private Factory() {
        }

        @Override
        public Birth createProperty(List<Parameter> params, String value) {
            return new Birth(params, Strings.unescape(value));
        }

        @Override
        public Birth createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

