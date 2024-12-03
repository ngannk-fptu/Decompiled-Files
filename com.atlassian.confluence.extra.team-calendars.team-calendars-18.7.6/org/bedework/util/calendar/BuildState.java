/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZoneRegistry;

public class BuildState {
    private ContentHandler handler;
    private TimeZoneRegistry tzRegistry;
    private List<Calendar> calendars = new ArrayList<Calendar>();
    private Calendar calendar;
    private LinkedList<Component> components = new LinkedList();
    private Property property;
    private List<Property> datesMissingTimezones = new ArrayList<Property>();

    public BuildState(TimeZoneRegistry tzRegistry) {
        this.tzRegistry = tzRegistry;
    }

    public void setContentHandler(ContentHandler val) {
        this.handler = val;
    }

    public TimeZoneRegistry getTzRegistry() {
        return this.tzRegistry;
    }

    public ContentHandler getContentHandler() {
        return this.handler;
    }

    public void setCalendar(Calendar val) {
        this.calendar = val;
    }

    public Calendar getCalendar() {
        return this.calendar;
    }

    public List<Calendar> getCalendars() {
        return this.calendars;
    }

    public Component getComponent() {
        if (this.components.size() == 0) {
            return null;
        }
        return this.components.peek();
    }

    public Component getSubComponent() {
        if (this.components.size() < 2) {
            return null;
        }
        return this.components.get(1);
    }

    public void startComponent(Component component) {
        this.components.push(component);
    }

    public void endComponent() {
        this.components.pop();
    }

    public Property getProperty() {
        return this.property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public List<Property> getDatesMissingTimezones() {
        return this.datesMissingTimezones;
    }
}

