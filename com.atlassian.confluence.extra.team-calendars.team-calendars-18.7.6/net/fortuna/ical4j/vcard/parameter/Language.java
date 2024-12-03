/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import java.util.Locale;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;
import org.apache.commons.lang.StringUtils;

public final class Language
extends Parameter {
    private static final long serialVersionUID = 8762124184853766503L;
    public static final ParameterFactory<Language> FACTORY = new Factory();
    private final Locale locale;

    public Language(String value) {
        this(new Locale(value));
    }

    public Language(Locale locale) {
        super(Parameter.Id.LANGUAGE);
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String getValue() {
        StringBuilder b = new StringBuilder();
        b.append(this.locale.getLanguage());
        if (!StringUtils.isEmpty(this.locale.getCountry())) {
            b.append('-');
            b.append(this.locale.getCountry());
        }
        if (!StringUtils.isEmpty(this.locale.getVariant())) {
            b.append('-');
            b.append(this.locale.getVariant());
        }
        return b.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<Language> {
        private Factory() {
        }

        @Override
        public Language createParameter(String value) {
            return new Language(new Locale(value));
        }
    }
}

