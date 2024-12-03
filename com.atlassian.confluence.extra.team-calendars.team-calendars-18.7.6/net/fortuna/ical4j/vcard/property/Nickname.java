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
public final class Nickname
extends Property {
    public static final PropertyFactory<Nickname> FACTORY = new Factory();
    private static final long serialVersionUID = 2512809288464680577L;
    private final String[] names;

    public Nickname(String ... names) {
        super(Property.Id.NICKNAME);
        if (names.length == 0) {
            throw new IllegalArgumentException("Must specify at least one nickname");
        }
        this.names = names;
    }

    public Nickname(List<Parameter> params, String value) {
        super(Property.Id.NICKNAME, params);
        this.names = value.split(",");
    }

    public String[] getNames() {
        return this.names;
    }

    @Override
    public String getValue() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < this.names.length; ++i) {
            if (i > 0) {
                b.append(',');
            }
            b.append(this.names[i]);
        }
        return b.toString();
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            try {
                this.assertTextParameter(param);
            }
            catch (ValidationException ve) {
                this.assertPidParameter(param);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Nickname> {
        private Factory() {
        }

        @Override
        public Nickname createProperty(List<Parameter> params, String value) {
            return new Nickname(params, value);
        }

        @Override
        public Nickname createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

