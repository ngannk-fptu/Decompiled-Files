/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.MessageFormat;
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
public final class Email
extends Property {
    private static final long serialVersionUID = 6134254373259957228L;
    public static final PropertyFactory<Email> FACTORY = new Factory();
    private String value;

    public Email(String value) {
        this((Group)null, value);
    }

    public Email(Group group, String value) {
        super(group, Property.Id.EMAIL);
        this.value = value;
    }

    public Email(List<Parameter> params, String value) {
        this(null, params, value);
    }

    public Email(Group group, List<Parameter> params, String value) {
        super(group, Property.Id.EMAIL, params);
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            Parameter.Id id = param.getId();
            if (Parameter.Id.PID.equals((Object)id) || Parameter.Id.PREF.equals((Object)id) || Parameter.Id.TYPE.equals((Object)id)) continue;
            throw new ValidationException(MessageFormat.format("Illegal parameter [{0}]", new Object[]{id}));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Email> {
        private Factory() {
        }

        @Override
        public Email createProperty(List<Parameter> params, String value) {
            return new Email(params, value);
        }

        @Override
        public Email createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return new Email(group, params, value);
        }
    }
}

