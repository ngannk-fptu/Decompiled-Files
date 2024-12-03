/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.ICalPersonToConfluenceUserTransformer;
import java.security.InvalidParameterException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.transform.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextLimitICalTransformer
implements Transformer<Calendar> {
    private static final Logger LOG = LoggerFactory.getLogger(ICalPersonToConfluenceUserTransformer.class);
    private final int numOfWordLimit;

    public TextLimitICalTransformer(int numOfWordLimit) {
        if (numOfWordLimit < 0) {
            throw new InvalidParameterException("Number of word limit much greater than zero");
        }
        this.numOfWordLimit = numOfWordLimit;
    }

    @Override
    public Calendar transform(Calendar calendar) {
        ComponentList vEvents = calendar.getComponents("VEVENT");
        vEvents.forEach(vEvent -> {
            Description description;
            PropertyList<Property> eventProperties = vEvent.getProperties();
            Summary summary = (Summary)eventProperties.getProperty("SUMMARY");
            if (summary != null) {
                summary.setValue(CalendarUtil.limitByWord(summary.getValue(), this.numOfWordLimit));
            }
            if ((description = (Description)eventProperties.getProperty("DESCRIPTION")) != null) {
                description.setValue(CalendarUtil.limitByWord(description.getValue(), this.numOfWordLimit));
            }
        });
        return calendar;
    }
}

