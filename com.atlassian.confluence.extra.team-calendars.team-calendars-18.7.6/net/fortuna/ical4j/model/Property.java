/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class Property
extends Content {
    private static final long serialVersionUID = 7048785558435608687L;
    public static final String PRODID = "PRODID";
    public static final String VERSION = "VERSION";
    public static final String CALSCALE = "CALSCALE";
    public static final String METHOD = "METHOD";
    public static final String BUSYTYPE = "BUSYTYPE";
    public static final String CLASS = "CLASS";
    public static final String CREATED = "CREATED";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String DTSTART = "DTSTART";
    public static final String GEO = "GEO";
    public static final String LAST_MODIFIED = "LAST-MODIFIED";
    public static final String LOCATION = "LOCATION";
    public static final String ORGANIZER = "ORGANIZER";
    public static final String PERCENT_COMPLETE = "PERCENT-COMPLETE";
    public static final String PRIORITY = "PRIORITY";
    public static final String DTSTAMP = "DTSTAMP";
    public static final String SEQUENCE = "SEQUENCE";
    public static final String STATUS = "STATUS";
    public static final String SUMMARY = "SUMMARY";
    public static final String TRANSP = "TRANSP";
    public static final String UID = "UID";
    public static final String URL = "URL";
    public static final String RECURRENCE_ID = "RECURRENCE-ID";
    public static final String COMPLETED = "COMPLETED";
    public static final String DUE = "DUE";
    public static final String FREEBUSY = "FREEBUSY";
    public static final String TZID = "TZID";
    public static final String TZNAME = "TZNAME";
    public static final String TZOFFSETFROM = "TZOFFSETFROM";
    public static final String TZOFFSETTO = "TZOFFSETTO";
    public static final String TZURL = "TZURL";
    public static final String ACTION = "ACTION";
    public static final String REPEAT = "REPEAT";
    public static final String TRIGGER = "TRIGGER";
    public static final String REQUEST_STATUS = "REQUEST-STATUS";
    public static final String DTEND = "DTEND";
    public static final String DURATION = "DURATION";
    public static final String ATTACH = "ATTACH";
    public static final String ATTENDEE = "ATTENDEE";
    public static final String CATEGORIES = "CATEGORIES";
    public static final String COMMENT = "COMMENT";
    public static final String CONTACT = "CONTACT";
    public static final String EXDATE = "EXDATE";
    public static final String EXRULE = "EXRULE";
    public static final String RELATED_TO = "RELATED-TO";
    public static final String RESOURCES = "RESOURCES";
    public static final String RDATE = "RDATE";
    public static final String RRULE = "RRULE";
    public static final String EXPERIMENTAL_PREFIX = "X-";
    public static final String COUNTRY = "COUNTRY";
    public static final String EXTENDED_ADDRESS = "EXTENDED-ADDRESS";
    public static final String LOCALITY = "LOCALITY";
    public static final String LOCATION_TYPE = "LOCATION-TYPE";
    public static final String NAME = "NAME";
    public static final String POSTALCODE = "POSTAL-CODE";
    public static final String REGION = "REGION";
    public static final String STREET_ADDRESS = "STREET-ADDRESS";
    public static final String TEL = "TEL";
    public static final String ACKNOWLEDGED = "ACKNOWLEDGED";
    private final String name;
    private final ParameterList parameters;
    private final PropertyFactory factory;

    protected Property(String aName, PropertyFactory factory) {
        this(aName, new ParameterList(), factory);
    }

    protected Property(String aName, ParameterList aList, PropertyFactory factory) {
        this.name = aName;
        this.parameters = aList;
        this.factory = factory;
    }

    protected Property(Property property) throws IOException, URISyntaxException, ParseException {
        this(property.getName(), new ParameterList(property.getParameters(), false), property.factory);
        this.setValue(property.getValue());
    }

    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.getName());
        if (this.getParameters() != null) {
            buffer.append(this.getParameters());
        }
        buffer.append(':');
        boolean needsEscape = false;
        if (this instanceof XProperty) {
            Value valParam = (Value)this.getParameter("VALUE");
            if (valParam == null || valParam.equals(Value.TEXT)) {
                needsEscape = true;
            }
        } else if (this instanceof Escapable) {
            needsEscape = true;
        }
        if (needsEscape) {
            buffer.append(Strings.escape(Strings.valueOf(this.getValue())));
        } else {
            buffer.append(Strings.valueOf(this.getValue()));
        }
        buffer.append("\r\n");
        return buffer.toString();
    }

    @Override
    public final String getName() {
        return this.name;
    }

    public final ParameterList getParameters() {
        return this.parameters;
    }

    public final ParameterList getParameters(String name) {
        return this.getParameters().getParameters(name);
    }

    public final <T extends Parameter> T getParameter(String name) {
        return this.getParameters().getParameter(name);
    }

    public abstract void setValue(String var1) throws IOException, URISyntaxException, ParseException;

    public abstract void validate() throws ValidationException;

    public final boolean equals(Object arg0) {
        if (arg0 instanceof Property) {
            Property p = (Property)arg0;
            return this.getName().equals(p.getName()) && new EqualsBuilder().append((Object)this.getValue(), (Object)p.getValue()).append((Object)this.getParameters(), (Object)p.getParameters()).isEquals();
        }
        return super.equals(arg0);
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getName().toUpperCase()).append((Object)this.getValue()).append((Object)this.getParameters()).toHashCode();
    }

    public Property copy() throws IOException, URISyntaxException, ParseException {
        if (this.factory == null) {
            throw new UnsupportedOperationException("No factory specified");
        }
        ParameterList params = new ParameterList(this.getParameters(), false);
        return this.factory.createProperty(params, this.getValue());
    }
}

