/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.data.DefaultComponentFactorySupplier;
import net.fortuna.ical4j.data.DefaultParameterFactorySupplier;
import net.fortuna.ical4j.data.DefaultPropertyFactorySupplier;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.CalendarException;
import net.fortuna.ical4j.model.ComponentBuilder;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterBuilder;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyBuilder;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultContentHandler
implements ContentHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultContentHandler.class);
    private final Supplier<List<ParameterFactory<?>>> parameterFactorySupplier;
    private final Supplier<List<PropertyFactory<?>>> propertyFactorySupplier;
    private final Supplier<List<ComponentFactory<?>>> componentFactorySupplier;
    private final TimeZoneRegistry tzRegistry;
    private List<Property> propertiesWithTzId;
    private boolean propertyHasTzId = false;
    private final Consumer<Calendar> consumer;
    private PropertyBuilder propertyBuilder;
    private final LinkedList<ComponentBuilder<CalendarComponent>> components = new LinkedList();
    private Calendar calendar;

    public DefaultContentHandler(Consumer<Calendar> consumer, TimeZoneRegistry tzRegistry) {
        this(consumer, tzRegistry, new DefaultParameterFactorySupplier(), new DefaultPropertyFactorySupplier(), new DefaultComponentFactorySupplier());
    }

    public DefaultContentHandler(Consumer<Calendar> consumer, TimeZoneRegistry tzRegistry, Supplier<List<ParameterFactory<?>>> parameterFactorySupplier, Supplier<List<PropertyFactory<?>>> propertyFactorySupplier, Supplier<List<ComponentFactory<?>>> componentFactorySupplier) {
        this.consumer = consumer;
        this.tzRegistry = tzRegistry;
        this.parameterFactorySupplier = parameterFactorySupplier;
        this.propertyFactorySupplier = propertyFactorySupplier;
        this.componentFactorySupplier = componentFactorySupplier;
    }

    public ComponentBuilder<CalendarComponent> getComponentBuilder() {
        if (this.components.size() == 0) {
            return null;
        }
        return this.components.peek();
    }

    public void endComponent() {
        this.components.pop();
    }

    @Override
    public void startCalendar() {
        this.calendar = new Calendar();
        this.components.clear();
        this.propertiesWithTzId = new ArrayList<Property>();
    }

    @Override
    public void endCalendar() throws IOException {
        this.resolveTimezones();
        this.consumer.accept(this.calendar);
    }

    @Override
    public void startComponent(String name) {
        if (this.components.size() > 10) {
            throw new RuntimeException("Components nested too deep");
        }
        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.factories(this.componentFactorySupplier.get()).name(name);
        this.components.push(componentBuilder);
    }

    @Override
    public void endComponent(String name) {
        this.assertComponent(this.getComponentBuilder());
        ComponentBuilder<CalendarComponent> componentBuilder = this.getComponentBuilder();
        this.endComponent();
        ComponentBuilder<CalendarComponent> parent = this.getComponentBuilder();
        if (parent != null) {
            CalendarComponent subComponent = componentBuilder.build();
            parent.subComponent(subComponent);
        } else {
            CalendarComponent component = componentBuilder.build();
            this.calendar.getComponents().add(component);
            if (component instanceof VTimeZone && this.tzRegistry != null) {
                this.tzRegistry.register(new TimeZone((VTimeZone)component));
            }
        }
    }

    @Override
    public void startProperty(String name) {
        this.propertyBuilder = new PropertyBuilder().factories(this.propertyFactorySupplier.get()).name(name);
        this.propertyHasTzId = false;
    }

    @Override
    public void propertyValue(String value) {
        this.propertyBuilder.value(value);
    }

    @Override
    public void endProperty(String name) throws URISyntaxException, ParseException, IOException {
        this.assertProperty(this.propertyBuilder);
        Property property = this.propertyBuilder.build();
        if (this.propertyHasTzId) {
            this.propertiesWithTzId.add(property);
        }
        property = Constants.forProperty(property);
        if (this.getComponentBuilder() != null) {
            this.getComponentBuilder().property(property);
        } else if (this.calendar != null) {
            this.calendar.getProperties().add(property);
        }
        property = null;
    }

    @Override
    public void parameter(String name, String value) throws URISyntaxException {
        this.assertProperty(this.propertyBuilder);
        Parameter parameter = new ParameterBuilder().factories(this.parameterFactorySupplier.get()).name(name).value(value).build();
        if (parameter instanceof TzId && this.tzRegistry != null) {
            this.propertyHasTzId = true;
        }
        this.propertyBuilder.parameter(parameter);
    }

    private void assertComponent(ComponentBuilder<?> component) {
        if (component == null) {
            throw new CalendarException("Expected component not initialised");
        }
    }

    private void assertProperty(PropertyBuilder property) {
        if (property == null) {
            throw new CalendarException("Expected property not initialised");
        }
    }

    private void resolveTimezones() throws IOException {
        for (Property property : this.propertiesWithTzId) {
            TimeZone timezone;
            TzId tzParam = (TzId)property.getParameter("TZID");
            if (tzParam == null || (timezone = this.tzRegistry.getTimeZone(tzParam.getValue())) == null) continue;
            String strDate = property.getValue();
            if (property instanceof DateProperty) {
                ((DateProperty)property).setTimeZone(timezone);
            } else if (property instanceof DateListProperty) {
                ((DateListProperty)property).setTimeZone(timezone);
            } else {
                LOG.warn("Property [%s] doesn't support parameter [%s]", (Object)property.getName(), (Object)tzParam.getName());
            }
            try {
                property.setValue(strDate);
            }
            catch (URISyntaxException | ParseException e) {
                throw new CalendarException(e);
            }
        }
    }
}

