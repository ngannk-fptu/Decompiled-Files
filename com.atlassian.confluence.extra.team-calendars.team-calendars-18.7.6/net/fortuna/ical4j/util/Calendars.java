/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.IndexedComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Uid;

public final class Calendars {
    private Calendars() {
    }

    public static Calendar load(String filename) throws IOException, ParserException {
        return new CalendarBuilder().build(Files.newInputStream(Paths.get(filename, new String[0]), new OpenOption[0]));
    }

    public static Calendar load(URL url) throws IOException, ParserException {
        CalendarBuilder builder = new CalendarBuilder();
        return builder.build(url.openStream());
    }

    public static Calendar merge(Calendar c1, Calendar c2) {
        Calendar result = new Calendar();
        result.getProperties().addAll(c1.getProperties());
        for (Property p : c2.getProperties()) {
            if (result.getProperties().contains(p)) continue;
            result.getProperties().add(p);
        }
        result.getComponents().addAll(c1.getComponents());
        for (CalendarComponent c : c2.getComponents()) {
            if (result.getComponents().contains(c)) continue;
            result.getComponents().add(c);
        }
        return result;
    }

    public static Calendar wrap(CalendarComponent ... component) {
        ComponentList<CalendarComponent> components = new ComponentList<CalendarComponent>(Arrays.asList(component));
        return new Calendar(components);
    }

    public static Calendar[] split(Calendar calendar) {
        if (calendar.getComponents().size() <= 1 || calendar.getComponents("VTIMEZONE").size() == calendar.getComponents().size()) {
            return new Calendar[]{calendar};
        }
        ComponentList timezoneList = calendar.getComponents("VTIMEZONE");
        IndexedComponentList timezones = new IndexedComponentList(timezoneList, "TZID");
        HashMap<Uid, Calendar> calendars = new HashMap<Uid, Calendar>();
        for (CalendarComponent c : calendar.getComponents()) {
            if (c instanceof VTimeZone) continue;
            Uid uid = (Uid)c.getProperty("UID");
            Calendar uidCal = (Calendar)calendars.get(uid);
            if (uidCal == null) {
                uidCal = new Calendar(calendar.getProperties(), new ComponentList<CalendarComponent>());
                for (Property mp : uidCal.getProperties("METHOD")) {
                    uidCal.getProperties().remove(mp);
                }
                calendars.put(uid, uidCal);
            }
            for (Property p : c.getProperties()) {
                TzId tzid = (TzId)p.getParameter("TZID");
                if (tzid == null) continue;
                VTimeZone timezone = (VTimeZone)timezones.getComponent(tzid.getValue());
                if (uidCal.getComponents().contains(timezone)) continue;
                uidCal.getComponents().add(timezone);
            }
            uidCal.getComponents().add(c);
        }
        return calendars.values().toArray(new Calendar[0]);
    }

    public static Uid getUid(Calendar calendar) throws ConstraintViolationException {
        Property uid = null;
        for (Component component : calendar.getComponents()) {
            for (Property foundUid : component.getProperties("UID")) {
                if (uid != null && !uid.equals(foundUid)) {
                    throw new ConstraintViolationException("More than one UID found in calendar");
                }
                uid = (Uid)foundUid;
            }
        }
        if (uid == null) {
            throw new ConstraintViolationException("Calendar must specify a single unique identifier (UID)");
        }
        return uid;
    }

    public static String getContentType(Calendar calendar, Charset charset) {
        StringBuilder b = new StringBuilder("text/calendar");
        Method method = (Method)calendar.getProperty("METHOD");
        if (method != null) {
            b.append("; method=");
            b.append(method.getValue());
        }
        if (charset != null) {
            b.append("; charset=");
            b.append(charset);
        }
        return b.toString();
    }
}

