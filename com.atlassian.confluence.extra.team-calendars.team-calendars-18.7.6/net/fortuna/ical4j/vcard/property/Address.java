/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;
import net.fortuna.ical4j.vcard.parameter.Type;
import org.apache.commons.lang.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Address
extends Property {
    private static final long serialVersionUID = 6538745668985015384L;
    public static final PropertyFactory<Address> FACTORY = new Factory();
    private String poBox;
    private String extended;
    private String street;
    private String locality;
    private String region;
    private String postcode;
    private String country;

    public Address(String poBox, String extended, String street, String locality, String region, String postcode, String country, Type ... types) {
        this(null, poBox, extended, street, locality, region, postcode, country, types);
    }

    public Address(Group group, String poBox, String extended, String street, String locality, String region, String postcode, String country, Type ... types) {
        super(group, Property.Id.ADR);
        this.poBox = poBox;
        this.extended = extended;
        this.street = street;
        this.locality = locality;
        this.region = region;
        this.postcode = postcode;
        this.country = country;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public Address(List<Parameter> params, String value) throws ParseException {
        this(null, params, value);
    }

    public Address(Group group, List<Parameter> params, String value) throws ParseException {
        super(group, Property.Id.ADR, params);
        if (CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) {
            this.parseValueRelaxed(value);
        } else {
            this.parseValue(value);
        }
    }

    private void parseValue(String value) throws ParseException {
        String[] components = value.split(";");
        if (components.length < 6) {
            throw new ParseException("ADR value must have all address components", 0);
        }
        this.poBox = components[0];
        this.extended = components[1];
        this.street = components[2];
        this.locality = components[3];
        this.region = components[4];
        this.postcode = components[5];
        if (components.length > 6) {
            this.country = components[6];
        }
    }

    private void parseValueRelaxed(String value) {
        String[] components = value.split(";");
        int length = components.length;
        this.poBox = length >= 1 ? components[0] : "";
        this.extended = length >= 2 ? components[1] : "";
        this.street = length >= 3 ? components[2] : "";
        this.locality = length >= 4 ? components[3] : "";
        this.region = length >= 5 ? components[4] : "";
        this.postcode = length >= 6 ? components[5] : null;
        this.country = length >= 7 ? components[6] : null;
    }

    public String getPoBox() {
        return this.poBox;
    }

    public String getExtended() {
        return this.extended;
    }

    public String getStreet() {
        return this.street;
    }

    public String getLocality() {
        return this.locality;
    }

    public String getRegion() {
        return this.region;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public String getCountry() {
        return this.country;
    }

    @Override
    public String getValue() {
        StringBuilder b = new StringBuilder();
        if (StringUtils.isNotEmpty(this.poBox)) {
            b.append(Strings.escape(this.poBox));
        }
        b.append(';');
        if (StringUtils.isNotEmpty(this.extended)) {
            b.append(Strings.escape(this.extended));
        }
        b.append(';');
        if (StringUtils.isNotEmpty(this.street)) {
            b.append(Strings.escape(this.street));
        }
        b.append(';');
        if (StringUtils.isNotEmpty(this.locality)) {
            b.append(Strings.escape(this.locality));
        }
        b.append(';');
        if (StringUtils.isNotEmpty(this.region)) {
            b.append(Strings.escape(this.region));
        }
        b.append(';');
        if (StringUtils.isNotEmpty(this.postcode)) {
            b.append(Strings.escape(this.postcode));
        }
        b.append(';');
        if (StringUtils.isNotEmpty(this.country)) {
            b.append(Strings.escape(this.country));
        }
        b.append(';');
        return b.toString();
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            try {
                this.assertTypeParameter(param);
            }
            catch (ValidationException ve) {
                try {
                    this.assertTextParameter(param);
                }
                catch (ValidationException ve2) {
                    this.assertPidParameter(param);
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Address> {
        private Factory() {
        }

        @Override
        public Address createProperty(List<Parameter> params, String value) throws ParseException {
            return new Address(params, Strings.unescape(value));
        }

        @Override
        public Address createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return new Address(group, params, Strings.unescape(value));
        }
    }
}

