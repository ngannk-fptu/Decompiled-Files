/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Geo
extends Property {
    public static final PropertyFactory<Geo> FACTORY = new Factory();
    private static final long serialVersionUID = 1533383111522264554L;
    private static final String DELIMITER = ";";
    private BigDecimal latitude;
    private BigDecimal longitude;

    public Geo(BigDecimal latitude, BigDecimal longitude) {
        super(Property.Id.GEO);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Geo(List<Parameter> params, String value) {
        this(null, params, value);
    }

    public Geo(Group group, List<Parameter> params, String value) {
        super(group, Property.Id.GEO, params);
        String[] components = null;
        components = CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed") ? value.split(";|,") : value.split(DELIMITER);
        this.latitude = new BigDecimal(components[0]);
        this.longitude = new BigDecimal(components[1]);
    }

    @Override
    public String getValue() {
        return String.valueOf(this.getLatitude()) + DELIMITER + String.valueOf(this.getLongitude());
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    @Override
    public void validate() throws ValidationException {
        this.assertParametersEmpty();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Geo> {
        private Factory() {
        }

        @Override
        public Geo createProperty(List<Parameter> params, String value) {
            return new Geo(params, value);
        }

        @Override
        public Geo createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return new Geo(group, params, value);
        }
    }
}

