/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaSubCalendarAccessor;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl.DefaultBandanaContextProvider;
import com.atlassian.confluence.extra.calendar3.util.Ical4jIoUtil;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBandanaSubCalendarAccessor
implements BandanaSubCalendarAccessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBandanaSubCalendarAccessor.class);
    private DefaultBandanaContextProvider bandanaContextProvider;
    private BandanaManager bandanaManager;

    public DefaultBandanaSubCalendarAccessor(BandanaManager bandanaManager, DefaultBandanaContextProvider bandanaContextProvider) {
        this.bandanaManager = bandanaManager;
        this.bandanaContextProvider = bandanaContextProvider;
    }

    @Override
    public Calendar getSubCalendarContent(BandanaSubCalendarsProvider provider, String subCalendarId) throws IOException, ParserException {
        Calendar calendar = null;
        LOGGER.debug("Getting sub calendar {} events from Bandana", (Object)subCalendarId);
        BandanaContext context = this.bandanaContextProvider.getSubCalendarContext(provider, subCalendarId);
        String calendarString = (String)this.bandanaManager.getValue(context, subCalendarId);
        if (StringUtils.isNotBlank((CharSequence)calendarString)) {
            calendar = Ical4jIoUtil.newCalendarBuilder().build(new StringReader(calendarString));
        }
        return calendar;
    }

    @Override
    public JSONObject getSubCalendarJson(BandanaSubCalendarsProvider provider, String subCalendarId) throws JSONException {
        BandanaContext context = this.bandanaContextProvider.getSubCalendarContext(provider);
        String jsonString = (String)this.bandanaManager.getValue(context, subCalendarId);
        return StringUtils.isBlank((CharSequence)jsonString) ? null : new JSONObject(jsonString);
    }

    @Override
    public Set<String> getSubCalendarIds(BandanaSubCalendarsProvider provider) {
        HashSet subCalendarIds;
        if (provider == null) {
            throw new InvalidParameterException("BandanaSubCalendarsProvider should not be null");
        }
        BandanaContext context = this.bandanaContextProvider.getSubCalendarContext(provider);
        Iterable subCalendarIdsIterator = this.bandanaManager.getKeys(context);
        HashSet hashSet = subCalendarIds = subCalendarIdsIterator != null ? Sets.newHashSet((Iterable)subCalendarIdsIterator) : Collections.emptySet();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SubCalendarIDs is {}", (Object)subCalendarIds);
        }
        return subCalendarIds;
    }
}

