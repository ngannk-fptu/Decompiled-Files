/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.transform.rfc5545;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.DefaultTimeZoneRegistryFactory;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DateProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TzHelper {
    private static final String MS_TIMEZONES_FILE = "msTimezones";
    private static final Map<String, String> MS_TIMEZONE_IDS = new HashMap<String, String>();
    private static final Map<String, String> MS_TIMEZONE_NAMES = new HashMap<String, String>();
    private static final TimeZoneRegistry TIMEZONE_REGISTRY = DefaultTimeZoneRegistryFactory.getInstance().createRegistry();
    private static final Logger LOG = LoggerFactory.getLogger(TzHelper.class);

    TzHelper() {
    }

    private static void initMsTimezones() {
        try (Scanner scanner = new Scanner(TzHelper.class.getResourceAsStream(MS_TIMEZONES_FILE));){
            while (scanner.hasNext()) {
                String[] arr = scanner.nextLine().split("=");
                String standardTzId = arr[1];
                String[] displayNameAndMsTzId = arr[0].split(";");
                MS_TIMEZONE_NAMES.put(displayNameAndMsTzId[0], standardTzId);
                MS_TIMEZONE_IDS.put(displayNameAndMsTzId[1], standardTzId);
            }
        }
        catch (RuntimeException e) {
            LOG.error("Could not load MS timezones", (Throwable)e);
            throw new RuntimeException("Unable to load resource file msTimezones", e);
        }
    }

    static void correctTzParameterFrom(Property property) {
        if (property.getParameter("TZID") != null) {
            String newTimezoneId = TzHelper.getCorrectedTimezoneFromTzParameter(property);
            TzHelper.correctTzParameter(property, newTimezoneId);
        }
    }

    static void correctTzParameterFrom(DateProperty property) {
        if (property.getValue() != null && property.getValue().endsWith("Z")) {
            property.getParameters().removeAll("TZID");
            return;
        }
        if (property.getParameter("TZID") != null) {
            String newTimezone = TzHelper.getCorrectedTimezoneFromTzParameter(property);
            String value = property.getValue();
            TzHelper.correctTzParameter(property, newTimezone);
            if (newTimezone != null) {
                property.setTimeZone(TIMEZONE_REGISTRY.getTimeZone(newTimezone));
                try {
                    property.setValue(value);
                }
                catch (ParseException e) {
                    LOG.warn("Failed to reset property value", (Throwable)e);
                }
            } else {
                property.setUtc(true);
            }
        }
    }

    private static void correctTzParameter(Property property, String newTimezoneId) {
        property.getParameters().removeAll("TZID");
        if (newTimezoneId != null) {
            property.getParameters().add(new TzId(newTimezoneId));
        }
    }

    private static String getCorrectedTimezoneFromTzParameter(Property property) {
        String tzIdValue = ((Content)property.getParameter("TZID")).getValue();
        return TzHelper.getCorrectedTimeZoneIdFrom(tzIdValue);
    }

    static void correctTzValueOf(net.fortuna.ical4j.model.property.TzId tzProperty) {
        String validTimezone = TzHelper.getCorrectedTimeZoneIdFrom(tzProperty.getValue());
        if (validTimezone != null) {
            tzProperty.setValue(validTimezone);
        }
    }

    private static String getCorrectedTimeZoneIdFrom(String value) {
        if (value != null) {
            String string = value = value.contains("\"") ? value.replaceAll("\"", "") : value;
            if (TIMEZONE_REGISTRY.getTimeZone(value) != null) {
                return TIMEZONE_REGISTRY.getTimeZone(value).getID();
            }
            String nameCandidate = MS_TIMEZONE_NAMES.get(value);
            if (nameCandidate != null) {
                return TIMEZONE_REGISTRY.getTimeZone(nameCandidate) != null ? TIMEZONE_REGISTRY.getTimeZone(nameCandidate).getID() : nameCandidate;
            }
            return MS_TIMEZONE_IDS.get(value);
        }
        return null;
    }

    static {
        TzHelper.initMsTimezones();
    }
}

