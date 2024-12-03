/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Lang
extends Property {
    public static final PropertyFactory<Lang> FACTORY = new Factory();
    private static final long serialVersionUID = 1863658302945551760L;
    private final Locale[] locales;

    public Lang(Locale ... locales) {
        super(Property.Id.LANG);
        if (locales.length == 0) {
            throw new IllegalArgumentException("Must have at least one locale");
        }
        this.locales = locales;
    }

    public Lang(List<Parameter> params, String value) {
        super(Property.Id.LANG, params);
        ArrayList<Locale> list = new ArrayList<Locale>();
        for (String langString : value.split(",")) {
            list.add(new Locale(langString));
        }
        this.locales = list.toArray(new Locale[list.size()]);
    }

    public Locale[] getLocales() {
        return this.locales;
    }

    @Override
    public String getValue() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < this.locales.length; ++i) {
            if (i > 0) {
                b.append(',');
            }
            b.append(this.locales[i].getLanguage());
        }
        return b.toString();
    }

    @Override
    public void validate() throws ValidationException {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Lang> {
        private Factory() {
        }

        @Override
        public Lang createProperty(List<Parameter> params, String value) {
            return new Lang(params, value);
        }

        @Override
        public Lang createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

