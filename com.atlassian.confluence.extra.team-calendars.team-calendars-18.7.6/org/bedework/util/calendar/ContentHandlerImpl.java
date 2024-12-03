/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fortuna.ical4j.model.component.VPoll
 *  net.fortuna.ical4j.model.component.VVoter
 *  net.fortuna.ical4j.model.component.Vote
 */
package org.bedework.util.calendar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.CalendarException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactoryImpl;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.model.ParameterFactoryRegistry;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryRegistry;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.Available;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VAvailability;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VPoll;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.component.VVoter;
import net.fortuna.ical4j.model.component.Vote;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Constants;
import net.fortuna.ical4j.util.Strings;
import org.bedework.util.calendar.BuildState;

public class ContentHandlerImpl
implements ContentHandler {
    private final BuildState bs;
    private final ComponentFactoryImpl componentFactory;
    private final PropertyFactoryRegistry propertyFactory;
    private final ParameterFactoryRegistry parameterFactory;

    public ContentHandlerImpl(BuildState bs) {
        this.bs = bs;
        this.componentFactory = ComponentFactoryImpl.getInstance();
        this.propertyFactory = new PropertyFactoryRegistry();
        this.parameterFactory = new ParameterFactoryRegistry();
    }

    @Override
    public void endCalendar() {
    }

    @Override
    public void endComponent(String name) {
        this.assertComponent(this.bs.getComponent());
        Component component = this.bs.getComponent();
        this.bs.endComponent();
        Component parent = this.bs.getComponent();
        if (parent != null) {
            if (parent instanceof VTimeZone) {
                ((VTimeZone)parent).getObservances().add((Observance)component);
            } else if (parent instanceof VEvent) {
                ((VEvent)parent).getAlarms().add((VAlarm)component);
            } else if (parent instanceof VToDo) {
                ((VToDo)parent).getAlarms().add((VAlarm)component);
            } else if (parent instanceof VAvailability) {
                ((VAvailability)parent).getAvailable().add((Available)component);
            } else if (parent instanceof VVoter) {
                ((VVoter)parent).getVotes().add((Vote)component);
            } else if (parent instanceof VPoll) {
                if (component instanceof VAlarm) {
                    ((VPoll)parent).getAlarms().add((VAlarm)component);
                } else if (component instanceof VVoter) {
                    ((VPoll)parent).getVoters().add((VVoter)component);
                } else {
                    ((VPoll)parent).getCandidates().add(component);
                }
            }
        } else {
            this.bs.getCalendar().getComponents().add((CalendarComponent)component);
            if (component instanceof VTimeZone && this.bs.getTzRegistry() != null) {
                this.bs.getTzRegistry().register(new TimeZone((VTimeZone)component));
            }
        }
    }

    @Override
    public void endProperty(String name) {
        this.assertProperty(this.bs.getProperty());
        this.bs.setProperty(Constants.forProperty(this.bs.getProperty()));
        if (this.bs.getComponent() != null) {
            this.bs.getComponent().getProperties().add((Object)this.bs.getProperty());
        } else if (this.bs.getCalendar() != null) {
            this.bs.getCalendar().getProperties().add((Object)this.bs.getProperty());
        }
        this.bs.setProperty(null);
    }

    @Override
    public void parameter(String name, String value) throws URISyntaxException {
        this.assertProperty(this.bs.getProperty());
        Parameter param = this.parameterFactory.createParameter(name.toUpperCase(), value);
        this.bs.getProperty().getParameters().add(param);
        if (param instanceof TzId && this.bs.getTzRegistry() != null) {
            TimeZone timezone = this.bs.getTzRegistry().getTimeZone(param.getValue());
            if (timezone != null) {
                this.updateTimeZone(this.bs.getProperty(), timezone);
                if (!timezone.getID().equals(param.getValue())) {
                    ParameterList pl = this.bs.getProperty().getParameters();
                    pl.replace(ParameterFactoryImpl.getInstance().createParameter("TZID", timezone.getID()));
                }
            } else {
                this.bs.getDatesMissingTimezones().add(this.bs.getProperty());
            }
        }
    }

    @Override
    public void propertyValue(String value) throws URISyntaxException, ParseException, IOException {
        this.assertProperty(this.bs.getProperty());
        if (this.bs.getProperty() instanceof Escapable) {
            this.bs.getProperty().setValue(Strings.unescape(value));
        } else {
            this.bs.getProperty().setValue(value);
        }
    }

    @Override
    public void startCalendar() {
        this.bs.setCalendar(new Calendar());
    }

    @Override
    public void startComponent(String name) {
        this.bs.startComponent((Component)this.componentFactory.createComponent(name));
    }

    @Override
    public void startProperty(String name) {
        this.bs.setProperty(this.propertyFactory.createProperty(name.toUpperCase()));
    }

    private void assertComponent(Component component) {
        if (component == null) {
            throw new CalendarException("Expected component not initialised");
        }
    }

    private void assertProperty(Property property) {
        if (property == null) {
            throw new CalendarException("Expected property not initialised");
        }
    }

    private void updateTimeZone(Property property, TimeZone timezone) {
        block4: {
            try {
                ((DateProperty)property).setTimeZone(timezone);
            }
            catch (ClassCastException e) {
                try {
                    ((DateListProperty)property).setTimeZone(timezone);
                }
                catch (ClassCastException e2) {
                    if (CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) break block4;
                    throw e2;
                }
            }
        }
    }
}

