/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneLoader;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.ResourceLoader;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeZoneRegistryImpl
implements TimeZoneRegistry {
    private static final String DEFAULT_RESOURCE_PREFIX = "zoneinfo/";
    private static final Pattern TZ_ID_SUFFIX = Pattern.compile("(?<=/)[^/]*/[^/]*$");
    private static final Map<String, TimeZone> DEFAULT_TIMEZONES = new ConcurrentHashMap<String, TimeZone>();
    private static final Properties ALIASES = new Properties();
    private final TimeZoneLoader timeZoneLoader;
    private Map<String, TimeZone> timezones;

    public TimeZoneRegistryImpl() {
        this(DEFAULT_RESOURCE_PREFIX);
    }

    public TimeZoneRegistryImpl(String resourcePrefix) {
        this.timeZoneLoader = new TimeZoneLoader(resourcePrefix);
        this.timezones = new ConcurrentHashMap<String, TimeZone>();
    }

    @Override
    public final void register(TimeZone timezone) {
        this.register(timezone, false);
    }

    @Override
    public final void register(TimeZone timezone, boolean update) {
        if (update) {
            try {
                this.timezones.put(timezone.getID(), new TimeZone(this.timeZoneLoader.loadVTimeZone(timezone.getID())));
            }
            catch (IOException | ParseException | ParserException e) {
                Logger log = LoggerFactory.getLogger(TimeZoneRegistryImpl.class);
                log.warn("Error occurred loading VTimeZone", (Throwable)e);
            }
        } else {
            this.timezones.put(timezone.getID(), timezone);
        }
    }

    @Override
    public final void clear() {
        this.timezones.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final TimeZone getTimeZone(String id) {
        TimeZone timezone = this.timezones.get(id);
        if (timezone == null) {
            Validate.notBlank((CharSequence)id, (String)"Invalid TimeZone ID: [%s]", (Object[])new Object[]{id});
            timezone = DEFAULT_TIMEZONES.get(id);
            if (timezone == null) {
                String alias = ALIASES.getProperty(id);
                if (alias != null) {
                    return this.getTimeZone(alias);
                }
                Map<String, TimeZone> map = DEFAULT_TIMEZONES;
                synchronized (map) {
                    timezone = DEFAULT_TIMEZONES.get(id);
                    if (timezone == null) {
                        try {
                            Matcher matcher;
                            VTimeZone vTimeZone = this.timeZoneLoader.loadVTimeZone(id);
                            if (vTimeZone != null) {
                                timezone = new TimeZone(vTimeZone);
                                DEFAULT_TIMEZONES.put(timezone.getID(), timezone);
                            } else if (CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed") && (matcher = TZ_ID_SUFFIX.matcher(id)).find()) {
                                return this.getTimeZone(matcher.group());
                            }
                        }
                        catch (IOException | ParseException | ParserException e) {
                            Logger log = LoggerFactory.getLogger(TimeZoneRegistryImpl.class);
                            log.warn("Error occurred loading VTimeZone", (Throwable)e);
                        }
                    }
                }
            }
        }
        return timezone;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        InputStream aliasInputStream = null;
        try {
            aliasInputStream = ResourceLoader.getResourceAsStream("net/fortuna/ical4j/model/tz.alias");
            ALIASES.load(aliasInputStream);
        }
        catch (IOException ioe) {
            LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn("Error loading timezone aliases: " + ioe.getMessage());
        }
        finally {
            if (aliasInputStream != null) {
                try {
                    aliasInputStream.close();
                }
                catch (IOException e) {
                    LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn("Error closing resource stream: " + e.getMessage());
                }
            }
        }
        try {
            aliasInputStream = ResourceLoader.getResourceAsStream("tz.alias");
            ALIASES.load(aliasInputStream);
        }
        catch (IOException | NullPointerException e) {
            LoggerFactory.getLogger(TimeZoneRegistryImpl.class).debug("Error loading custom timezone aliases: " + e.getMessage());
        }
        finally {
            if (aliasInputStream != null) {
                try {
                    aliasInputStream.close();
                }
                catch (IOException e) {
                    LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn("Error closing resource stream: " + e.getMessage());
                }
            }
        }
    }
}

