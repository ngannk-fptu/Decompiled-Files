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
public final class Xproperty
extends Property
implements Escapable {
    public static final PropertyFactory<Xproperty> FACTORY = new ExtendedFactory();
    private static final long serialVersionUID = -3524639290151277814L;
    private final String value;

    public Xproperty(String extendedName, String value) {
        super(extendedName);
        this.value = value;
    }

    public Xproperty(String extendedName, List<Parameter> params, String value) {
        super(extendedName, params);
        this.value = value;
    }

    public Xproperty(Group group, String extendedName, List<Parameter> params, String value) {
        super(group, extendedName, params);
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
    public static class ExtendedFactory
    implements PropertyFactory<Xproperty> {
        public Xproperty createProperty(String extendedName, List<Parameter> params, String value) {
            return new Xproperty(extendedName, params, Strings.unescape(value));
        }

        public Xproperty createProperty(Group group, String extendedName, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return new Xproperty(group, extendedName, params, Strings.unescape(value));
        }

        @Override
        public Xproperty createProperty(List<Parameter> params, String value) {
            return new Xproperty(null, params, Strings.unescape(value));
        }

        @Override
        public Xproperty createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

