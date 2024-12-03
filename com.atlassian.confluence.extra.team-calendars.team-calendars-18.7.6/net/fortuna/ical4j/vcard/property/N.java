/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class N
extends Property {
    private static final long serialVersionUID = 1117450875931318523L;
    public static final PropertyFactory<N> FACTORY = new Factory();
    private final String familyName;
    private String givenName;
    private String[] additionalNames;
    private String[] prefixes;
    private String[] suffixes;

    public N(String familyName, String givenName, String[] additionalNames, String[] prefixes, String[] suffixes) {
        super(Property.Id.N);
        this.familyName = familyName;
        this.givenName = givenName;
        this.additionalNames = additionalNames;
        this.prefixes = prefixes;
        this.suffixes = suffixes;
    }

    public N(List<Parameter> params, String value) {
        super(Property.Id.N, params);
        String[] names = value.split(";", -1);
        this.familyName = names[0];
        if (names.length >= 2) {
            this.givenName = names[1];
        }
        if (CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) {
            this.parseValueRelaxed(names);
        } else {
            this.parseValue(names);
        }
    }

    private void parseValueRelaxed(String[] names) {
        if (names.length >= 3) {
            this.additionalNames = names[2].split(",");
        }
        if (names.length >= 4) {
            this.prefixes = names[3].split(",");
        }
        if (names.length >= 5) {
            this.suffixes = names[4].split(",");
        }
    }

    private void parseValue(String[] names) {
        if (names.length > 2) {
            this.additionalNames = names[2].split(",");
        }
        if (names.length > 3) {
            this.prefixes = names[3].split(",");
        }
        if (names.length > 4) {
            this.suffixes = names[4].split(",");
        }
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public String getGivenName() {
        return this.givenName;
    }

    public String[] getAdditionalNames() {
        return this.additionalNames;
    }

    public String[] getPrefixes() {
        return this.prefixes;
    }

    public String[] getSuffixes() {
        return this.suffixes;
    }

    @Override
    public String getValue() {
        int i;
        StringBuilder b = new StringBuilder();
        if (StringUtils.isNotEmpty(this.familyName)) {
            b.append(this.familyName);
        }
        if (StringUtils.isNotEmpty(this.givenName)) {
            if (b.length() > 0) {
                b.append(';');
            }
            b.append(this.givenName);
        }
        if (!ArrayUtils.isEmpty(this.additionalNames)) {
            if (b.length() > 0) {
                b.append(';');
            }
            for (i = 0; i < this.additionalNames.length; ++i) {
                if (i > 0) {
                    b.append(',');
                }
                b.append(this.additionalNames[i]);
            }
        }
        if (!ArrayUtils.isEmpty(this.prefixes)) {
            if (b.length() > 0) {
                b.append(';');
            }
            for (i = 0; i < this.prefixes.length; ++i) {
                if (i > 0) {
                    b.append(',');
                }
                b.append(this.prefixes[i]);
            }
        }
        if (!ArrayUtils.isEmpty(this.suffixes)) {
            if (b.length() > 0) {
                b.append(';');
            }
            for (i = 0; i < this.suffixes.length; ++i) {
                if (i > 0) {
                    b.append(',');
                }
                b.append(this.suffixes[i]);
            }
        }
        return b.toString();
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
    implements PropertyFactory<N> {
        private Factory() {
        }

        @Override
        public N createProperty(List<Parameter> params, String value) {
            return new N(params, value);
        }

        @Override
        public N createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

