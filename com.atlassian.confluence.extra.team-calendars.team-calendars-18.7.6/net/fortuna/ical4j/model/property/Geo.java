/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;
import org.apache.commons.lang3.StringUtils;

public class Geo
extends Property {
    private static final long serialVersionUID = -902100715801867636L;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public Geo() {
        super("GEO", new Factory());
        this.latitude = BigDecimal.valueOf(0L);
        this.longitude = BigDecimal.valueOf(0L);
    }

    public Geo(String value) {
        super("GEO", new Factory());
        this.setValue(value);
    }

    public Geo(ParameterList aList, String aValue) {
        super("GEO", aList, new Factory());
        this.setValue(aValue);
    }

    public Geo(BigDecimal latitude, BigDecimal longitude) {
        super("GEO", new Factory());
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Geo(ParameterList aList, BigDecimal latitude, BigDecimal longitude) {
        super("GEO", aList, new Factory());
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public final BigDecimal getLatitude() {
        return this.latitude;
    }

    public final BigDecimal getLongitude() {
        return this.longitude;
    }

    @Override
    public final void setValue(String aValue) {
        String latitudeString = aValue.substring(0, aValue.indexOf(59));
        this.latitude = StringUtils.isNotBlank((CharSequence)latitudeString) ? new BigDecimal(latitudeString) : BigDecimal.valueOf(0L);
        String longitudeString = aValue.substring(aValue.indexOf(59) + 1);
        this.longitude = StringUtils.isNotBlank((CharSequence)longitudeString) ? new BigDecimal(longitudeString) : BigDecimal.valueOf(0L);
    }

    @Override
    public final String getValue() {
        return String.valueOf(this.getLatitude()) + ";" + String.valueOf(this.getLongitude());
    }

    public final void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public final void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    @Override
    public void validate() throws ValidationException {
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("GEO");
        }

        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Geo(parameters, value);
        }

        public Property createProperty() {
            return new Geo();
        }
    }
}

