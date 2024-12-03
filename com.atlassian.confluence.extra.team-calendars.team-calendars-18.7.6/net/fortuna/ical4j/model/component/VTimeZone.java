/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model.component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Objects;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzUrl;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.component.VTimeZoneValidator;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VTimeZone
extends CalendarComponent {
    private static final long serialVersionUID = 5629679741050917815L;
    private final Validator itipValidator = new VTimeZoneValidator(new ValidationRule[0]);
    private ComponentList<Observance> observances;

    public VTimeZone() {
        super("VTIMEZONE");
        this.observances = new ComponentList();
    }

    public VTimeZone(PropertyList properties) {
        super("VTIMEZONE", properties);
        this.observances = new ComponentList();
    }

    public VTimeZone(ComponentList<Observance> observances) {
        super("VTIMEZONE");
        this.observances = observances;
    }

    public VTimeZone(PropertyList properties, ComponentList<Observance> observances) {
        super("VTIMEZONE", properties);
        this.observances = observances;
    }

    @Override
    public final String toString() {
        return "BEGIN:" + this.getName() + "\r\n" + this.getProperties() + this.observances + "END" + ':' + this.getName() + "\r\n";
    }

    @Override
    public final void validate(boolean recurse) throws ValidationException {
        PropertyValidator.assertOne("TZID", this.getProperties());
        PropertyValidator.assertOneOrLess("LAST-MODIFIED", this.getProperties());
        PropertyValidator.assertOneOrLess("TZURL", this.getProperties());
        if (this.getObservances().getComponent("STANDARD") == null && this.getObservances().getComponent("DAYLIGHT") == null) {
            throw new ValidationException("Sub-components [STANDARD,DAYLIGHT] must be specified at least once");
        }
        for (Observance observance : this.getObservances()) {
            observance.validate(recurse);
        }
        if (recurse) {
            this.validateProperties();
        }
    }

    protected Validator getValidator(Method method) {
        return this.itipValidator;
    }

    public final ComponentList<Observance> getObservances() {
        return this.observances;
    }

    public final Observance getApplicableObservance(Date date) {
        Observance latestObservance = null;
        Date latestOnset = null;
        for (Observance observance : this.getObservances()) {
            Date onset = observance.getLatestOnset(date);
            if (latestOnset != null && (onset == null || !onset.after(latestOnset))) continue;
            latestOnset = onset;
            latestObservance = observance;
        }
        return latestObservance;
    }

    public final TzId getTimeZoneId() {
        return (TzId)this.getProperty("TZID");
    }

    public final LastModified getLastModified() {
        return (LastModified)this.getProperty("LAST-MODIFIED");
    }

    public final TzUrl getTimeZoneUrl() {
        return (TzUrl)this.getProperty("TZURL");
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof VTimeZone) {
            return super.equals(arg0) && Objects.equals(this.observances, ((VTimeZone)arg0).getObservances());
        }
        return super.equals(arg0);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getName()).append(this.getProperties()).append(this.getObservances()).toHashCode();
    }

    @Override
    public Component copy() throws ParseException, IOException, URISyntaxException {
        VTimeZone copy = (VTimeZone)super.copy();
        copy.observances = new ComponentList<Observance>(this.observances);
        return copy;
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<VTimeZone> {
        public Factory() {
            super("VTIMEZONE");
        }

        @Override
        public VTimeZone createComponent() {
            return new VTimeZone();
        }

        @Override
        public VTimeZone createComponent(PropertyList properties) {
            return new VTimeZone(properties);
        }

        @Override
        public VTimeZone createComponent(PropertyList properties, ComponentList subComponents) {
            return new VTimeZone(properties, subComponents);
        }
    }
}

