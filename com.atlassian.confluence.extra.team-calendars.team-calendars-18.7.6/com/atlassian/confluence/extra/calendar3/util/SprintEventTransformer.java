/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SprintEventTransformer {
    private static Logger logger = LoggerFactory.getLogger(SprintEventTransformer.class);
    private final String subCalendarType;
    private final SimpleDateFormat allDayDateFormat;

    public SprintEventTransformer(String subCalendarType) {
        this.subCalendarType = subCalendarType;
        this.allDayDateFormat = new SimpleDateFormat("yyyyMMdd");
    }

    public VEvent transform(VEvent eventComponent) {
        if (!StringUtils.equals((CharSequence)"jira-agile-sprint", (CharSequence)this.subCalendarType)) {
            return eventComponent;
        }
        DtStart dtStart = (DtStart)eventComponent.getProperty("DTSTART");
        DtEnd dtEnd = (DtEnd)eventComponent.getProperty("DTEND");
        try {
            Date startDate = new Date(this.allDayDateFormat.format(dtStart.getDate()));
            dtStart.setDate(startDate);
            Date endDate = new Date(this.allDayDateFormat.format(dtEnd.getDate()));
            dtEnd.setDate(endDate);
        }
        catch (ParseException e) {
            logger.error("Could not reset DtStart and DtEnd for Jira Sprint Issues");
        }
        return eventComponent;
    }
}

