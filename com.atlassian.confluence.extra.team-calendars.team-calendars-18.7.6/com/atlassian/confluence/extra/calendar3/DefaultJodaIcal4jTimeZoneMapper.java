/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.TimeZone
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.Collections2
 *  org.joda.time.DateTimeZone
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.util.TimeZoneUtil;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.Collections2;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import net.fortuna.ical4j.model.TimeZone;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="jodaIcal4jTimeZoneMapper")
public class DefaultJodaIcal4jTimeZoneMapper
implements JodaIcal4jTimeZoneMapper,
InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJodaIcal4jTimeZoneMapper.class);
    private static final String SYSTEM_TIME_ZONE_ID = "Australia/Sydney";
    private final SettingsManager settingsManager;
    private final UserAccessor userAccessor;
    private final Set<String> supportedTimeZoneIds;
    private final Map<String, String> timeZoneAliases;

    @Autowired
    public DefaultJodaIcal4jTimeZoneMapper(@ComponentImport SettingsManager settingsManager, @ComponentImport UserAccessor userAccessor) {
        this.settingsManager = settingsManager;
        this.userAccessor = userAccessor;
        this.supportedTimeZoneIds = new LinkedHashSet<String>();
        this.timeZoneAliases = new ConcurrentHashMap<String, String>();
    }

    public void afterPropertiesSet() throws Exception {
        if (!this.supportedTimeZoneIds.isEmpty()) {
            this.supportedTimeZoneIds.clear();
        }
        this.supportedTimeZoneIds.addAll(this.getTimeZoneIdsSupportedByJodaAndIcal4j());
        this.initTimeZoneAliases();
    }

    @Override
    public DateTimeZone toJodaTimeZone(String ical4jTimeZoneId) {
        return DateTimeZone.forID((String)this.getTimeZoneIdForAlias(ical4jTimeZoneId));
    }

    @Override
    public TimeZone toIcal4jTimeZone(String jodaTimeZoneId) {
        return TimeZoneUtil.getTimeZone(DateTimeZone.forID((String)jodaTimeZoneId).getID());
    }

    @Override
    public TimeZone getIcal4jTimeZone(String ical4jTimeZoneId) {
        return TimeZoneUtil.getTimeZone(this.getTimeZoneIdForAlias(ical4jTimeZoneId));
    }

    @Override
    public Set<String> getSupportedTimeZoneIds() {
        if (this.supportedTimeZoneIds.isEmpty()) {
            this.supportedTimeZoneIds.addAll(this.getTimeZoneIdsSupportedByJodaAndIcal4j());
        }
        return Collections.unmodifiableSet(this.supportedTimeZoneIds);
    }

    @Override
    public Collection<com.atlassian.confluence.core.TimeZone> getSupportConfluenceTimeZones() {
        Set<String> supportedTimeZoneIds = this.getSupportedTimeZoneIds();
        return Collections2.filter((Collection)com.atlassian.confluence.core.TimeZone.getSortedTimeZones(), timeZone -> {
            boolean supported = supportedTimeZoneIds.contains(timeZone.getID());
            if (!supported) {
                try {
                    supported = supportedTimeZoneIds.contains(DateTimeZone.forID((String)timeZone.getID()).getID());
                }
                catch (IllegalArgumentException invalidTimeZoneId) {
                    LOG.debug(String.format("Unsupported Confluence time zone ID %s", timeZone.getID()));
                }
            }
            return supported;
        });
    }

    @Override
    public String getSystemTimeZoneIdJoda(boolean returnNullIfUnsupported) {
        com.atlassian.confluence.core.TimeZone systemTimeZone = this.settingsManager.getGlobalSettings().getTimeZone();
        String systemTimeZoneId = systemTimeZone.getID();
        Set<String> supportedTimeZoneIds = this.getSupportedTimeZoneIds();
        if (!supportedTimeZoneIds.contains(systemTimeZoneId)) {
            return returnNullIfUnsupported ? null : DateTimeZone.forID((String)this.getSystemTimeZoneId()).getID();
        }
        return systemTimeZoneId;
    }

    @Override
    public String getSystemTimeZoneIdJoda() {
        return this.getSystemTimeZoneIdJoda(false);
    }

    @Override
    public String getUserTimeZoneIdJoda(ConfluenceUser user) {
        com.atlassian.confluence.core.TimeZone userConfluenceTz = this.getUserTimeZoneJoda(user);
        String confluenceUserTimeZoneId = userConfluenceTz.getID();
        if (this.getSupportedTimeZoneIds().contains(confluenceUserTimeZoneId)) {
            return confluenceUserTimeZoneId;
        }
        return DateTimeZone.forOffsetMillis((int)userConfluenceTz.getWrappedTimeZone().getOffset(System.currentTimeMillis())).getID();
    }

    public com.atlassian.confluence.core.TimeZone getUserTimeZoneJoda(ConfluenceUser user) {
        if (null != user) {
            ConfluenceUserPreferences confluenceUserPreferences = this.userAccessor.getConfluenceUserPreferences((User)user);
            return confluenceUserPreferences.getTimeZone();
        }
        return this.settingsManager.getGlobalSettings().getTimeZone();
    }

    @Override
    public boolean isTimeZoneIdAnAlias(String alias) {
        String timeZoneId = this.timeZoneAliases.get(alias);
        while (null != timeZoneId && this.timeZoneAliases.containsKey(timeZoneId)) {
            timeZoneId = this.timeZoneAliases.get(timeZoneId);
        }
        return null != timeZoneId;
    }

    @Override
    public boolean isTimeZoneSupported(TimeZone timeZone) {
        if (timeZone == null) {
            return false;
        }
        return this.getSupportedTimeZoneIds().contains(timeZone.getID());
    }

    @Override
    public String getTimeZoneIdForAlias(String aliasOrActualTimeZoneId) {
        Set<String> supportedTimeZoneIds = this.getSupportedTimeZoneIds();
        if (supportedTimeZoneIds.contains(aliasOrActualTimeZoneId)) {
            return aliasOrActualTimeZoneId;
        }
        String timeZoneId = this.timeZoneAliases.get(aliasOrActualTimeZoneId);
        while (null != timeZoneId && this.timeZoneAliases.containsKey(timeZoneId)) {
            timeZoneId = this.timeZoneAliases.get(timeZoneId);
        }
        return null != timeZoneId && supportedTimeZoneIds.contains(timeZoneId) ? timeZoneId : null;
    }

    private String getSystemTimeZoneId() {
        return SYSTEM_TIME_ZONE_ID;
    }

    private void initTimeZoneAliases() {
        try (InputStream tzAliasOverrideInput = this.getClass().getClassLoader().getResourceAsStream("tz.alias");){
            try (InputStream tzAliasDefaultInput = this.getClass().getClassLoader().getResourceAsStream("net/fortuna/ical4j/model/tz.alias");){
                Properties aliases = new Properties();
                aliases.load(new SequenceInputStream(tzAliasOverrideInput, tzAliasDefaultInput));
                for (Object property : aliases.keySet()) {
                    String propertyName = property.toString();
                    String propertyValue = aliases.getProperty(propertyName);
                    LOG.debug(String.format("Read alias: %s -> %s", propertyName, propertyValue));
                    this.timeZoneAliases.put(propertyName, propertyValue);
                }
            }
            catch (IOException errorReadingTzAlias) {
                LOG.warn("Unable to read default iCal4j time zone aliases", (Throwable)errorReadingTzAlias);
            }
        }
        catch (IOException errorReadingTzAlias) {
            LOG.error("Unable to read time zone aliases", (Throwable)errorReadingTzAlias);
        }
    }

    private Set<String> getTimeZoneIdsSupportedByJodaAndIcal4j() {
        HashSet<String> supportedTimeZoneIds = new HashSet<String>();
        HashSet<String> unsupportedTimeZoneIds = new HashSet<String>();
        StringBuilder timeZoneClassPathIcsBuilder = new StringBuilder();
        Set jodaTimeZoneIds = DateTimeZone.getAvailableIDs();
        for (String jodaTimeZoneId : jodaTimeZoneIds) {
            DateTimeZone jodaTimeZone = DateTimeZone.forID((String)jodaTimeZoneId);
            timeZoneClassPathIcsBuilder.setLength(0);
            (null != this.getClass().getClassLoader().getResource(timeZoneClassPathIcsBuilder.append("zoneinfo/").append(jodaTimeZone.getID()).append(".ics").toString()) ? supportedTimeZoneIds : unsupportedTimeZoneIds).add(jodaTimeZoneId);
        }
        for (com.atlassian.confluence.core.TimeZone timeZone : com.atlassian.confluence.core.TimeZone.getSortedTimeZones()) {
            String confluenceTimeZoneId = timeZone.getID();
            if (supportedTimeZoneIds.contains(confluenceTimeZoneId)) continue;
            try {
                DateTimeZone dateTimeZone = DateTimeZone.forID((String)confluenceTimeZoneId);
                if (!supportedTimeZoneIds.contains(dateTimeZone.getID())) continue;
                supportedTimeZoneIds.add(confluenceTimeZoneId);
            }
            catch (IllegalArgumentException invalidTimeZoneId) {
                unsupportedTimeZoneIds.add(confluenceTimeZoneId);
            }
        }
        LOG.debug(String.format("Time zone IDs not supported (%d): %s", unsupportedTimeZoneIds.size(), unsupportedTimeZoneIds));
        return new TreeSet<String>(supportedTimeZoneIds);
    }
}

