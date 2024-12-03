/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class Parameter
extends Content {
    private static final long serialVersionUID = -2058497904769713528L;
    public static final String ABBREV = "ABBREV";
    public static final String ALTREP = "ALTREP";
    public static final String CN = "CN";
    public static final String CUTYPE = "CUTYPE";
    public static final String DELEGATED_FROM = "DELEGATED-FROM";
    public static final String DELEGATED_TO = "DELEGATED-TO";
    public static final String DIR = "DIR";
    public static final String DISPLAY = "DISPLAY";
    public static final String EMAIL = "EMAIL";
    public static final String ENCODING = "ENCODING";
    public static final String FEATURE = "FEATURE";
    public static final String FMTTYPE = "FMTTYPE";
    public static final String FBTYPE = "FBTYPE";
    public static final String LABEL = "LABEL";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String MEMBER = "MEMBER";
    public static final String PARTSTAT = "PARTSTAT";
    public static final String RANGE = "RANGE";
    public static final String RELATED = "RELATED";
    public static final String RELTYPE = "RELTYPE";
    public static final String ROLE = "ROLE";
    public static final String RSVP = "RSVP";
    public static final String SCHEDULE_AGENT = "SCHEDULE-AGENT";
    public static final String SCHEDULE_STATUS = "SCHEDULE-STATUS";
    public static final String SENT_BY = "SENT-BY";
    public static final String TYPE = "TYPE";
    public static final String TZID = "TZID";
    public static final String VALUE = "VALUE";
    public static final String VVENUE = "VVENUE";
    public static final String EXPERIMENTAL_PREFIX = "X-";
    private String name;
    private final ParameterFactory factory;

    public Parameter(String aName, ParameterFactory factory) {
        this.name = aName;
        this.factory = factory;
    }

    public final String toString() {
        StringBuilder b = new StringBuilder();
        b.append(this.getName());
        b.append('=');
        if (this.isQuotable()) {
            b.append(Strings.quote(Strings.valueOf(this.getValue())));
        } else {
            b.append(Strings.valueOf(this.getValue()));
        }
        return b.toString();
    }

    protected boolean isQuotable() {
        return Strings.PARAM_QUOTE_PATTERN.matcher(Strings.valueOf(this.getValue())).find();
    }

    @Override
    public final String getName() {
        return this.name;
    }

    public final boolean equals(Object arg0) {
        if (arg0 instanceof Parameter) {
            Parameter p = (Parameter)arg0;
            return new EqualsBuilder().append((Object)this.getName(), (Object)p.getName()).append((Object)this.getValue(), (Object)p.getValue()).isEquals();
        }
        return super.equals(arg0);
    }

    public final int hashCode() {
        return new HashCodeBuilder().append((Object)this.getName().toUpperCase()).append((Object)this.getValue()).toHashCode();
    }

    public <T extends Parameter> T copy() throws URISyntaxException {
        if (this.factory == null) {
            throw new UnsupportedOperationException("No factory specified");
        }
        return this.factory.createParameter(this.getValue());
    }
}

