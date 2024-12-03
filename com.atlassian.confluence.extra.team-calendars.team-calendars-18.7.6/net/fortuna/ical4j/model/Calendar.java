/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.AbstractCalendarValidatorFactory;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Calendar
implements Serializable {
    private static final long serialVersionUID = -1654118204678581940L;
    public static final String BEGIN = "BEGIN";
    public static final String VCALENDAR = "VCALENDAR";
    public static final String END = "END";
    private final PropertyList<Property> properties;
    private final ComponentList<CalendarComponent> components;
    private final Validator<Calendar> validator;

    public Calendar() {
        this(new PropertyList<Property>(), new ComponentList<CalendarComponent>());
    }

    public Calendar(ComponentList<CalendarComponent> components) {
        this(new PropertyList<Property>(), components);
    }

    public Calendar(PropertyList<Property> properties, ComponentList<CalendarComponent> components) {
        this(properties, components, AbstractCalendarValidatorFactory.getInstance().newInstance());
    }

    public Calendar(PropertyList<Property> p, ComponentList<CalendarComponent> c, Validator<Calendar> validator) {
        this.properties = p;
        this.components = c;
        this.validator = validator;
    }

    public Calendar(Calendar c) throws ParseException, IOException, URISyntaxException {
        this(new PropertyList<Property>(c.getProperties()), new ComponentList<CalendarComponent>(c.getComponents()));
    }

    public final String toString() {
        return "BEGIN:VCALENDAR\r\n" + this.getProperties() + this.getComponents() + END + ':' + VCALENDAR + "\r\n";
    }

    public final ComponentList<CalendarComponent> getComponents() {
        return this.components;
    }

    public final <C extends CalendarComponent> ComponentList<C> getComponents(String name) {
        return this.getComponents().getComponents(name);
    }

    public final CalendarComponent getComponent(String name) {
        return this.getComponents().getComponent(name);
    }

    public final PropertyList<Property> getProperties() {
        return this.properties;
    }

    public final PropertyList<Property> getProperties(String name) {
        return this.getProperties().getProperties(name);
    }

    public final <T extends Property> T getProperty(String name) {
        return (T)((Property)this.getProperties().getProperty(name));
    }

    public final void validate() throws ValidationException {
        this.validate(true);
    }

    public void validate(boolean recurse) throws ValidationException {
        this.validator.validate(this);
        if (recurse) {
            this.validateProperties();
            this.validateComponents();
        }
    }

    private void validateProperties() throws ValidationException {
        for (Property property : this.getProperties()) {
            property.validate();
        }
    }

    private void validateComponents() throws ValidationException {
        for (Component component : this.getComponents()) {
            component.validate();
        }
    }

    public final ProdId getProductId() {
        return (ProdId)this.getProperty("PRODID");
    }

    public final Version getVersion() {
        return (Version)this.getProperty("VERSION");
    }

    public final CalScale getCalendarScale() {
        return (CalScale)this.getProperty("CALSCALE");
    }

    public final Method getMethod() {
        return (Method)this.getProperty("METHOD");
    }

    public final boolean equals(Object arg0) {
        if (arg0 instanceof Calendar) {
            Calendar calendar = (Calendar)arg0;
            return new EqualsBuilder().append(this.getProperties(), calendar.getProperties()).append(this.getComponents(), calendar.getComponents()).isEquals();
        }
        return super.equals(arg0);
    }

    public final int hashCode() {
        return new HashCodeBuilder().append(this.getProperties()).append(this.getComponents()).toHashCode();
    }
}

