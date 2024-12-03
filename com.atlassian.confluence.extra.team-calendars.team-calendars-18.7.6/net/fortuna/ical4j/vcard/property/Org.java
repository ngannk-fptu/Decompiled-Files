/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Org
extends Property {
    private static final long serialVersionUID = -1435956318814896568L;
    private static final String VALUES_SPLIT_REGEX = "(?<!\\\\)(?>\\\\\\\\)*;";
    public static final PropertyFactory<Org> FACTORY = new Factory();
    private String[] values;

    public Org(String ... value) {
        this((Group)null, value);
    }

    public Org(Group group, String ... value) {
        super(group, Property.Id.ORG);
        if (value.length == 0) {
            throw new IllegalArgumentException("Must specify at least one organization");
        }
        this.values = value;
    }

    public Org(List<Parameter> params, String value) {
        this(null, params, value);
    }

    public Org(Group group, List<Parameter> params, String value) {
        super(group, Property.Id.ORG, params);
        this.values = value.split(VALUES_SPLIT_REGEX);
    }

    public String[] getValues() {
        return this.values;
    }

    @Override
    public String getValue() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < this.values.length; ++i) {
            b.append(Strings.escape(this.values[i]));
            if (i >= this.values.length - 1) continue;
            b.append(';');
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
    implements PropertyFactory<Org> {
        private Factory() {
        }

        @Override
        public Org createProperty(List<Parameter> params, String value) {
            return new Org(params, value);
        }

        @Override
        public Org createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return new Org(group, params, value);
        }
    }
}

