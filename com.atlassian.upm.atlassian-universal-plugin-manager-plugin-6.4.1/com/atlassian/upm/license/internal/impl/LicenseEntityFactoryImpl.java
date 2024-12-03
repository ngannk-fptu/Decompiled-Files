/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Contact
 *  com.atlassian.extras.api.Organisation
 *  com.atlassian.extras.api.Partner
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.apache.commons.io.IOUtils
 *  org.joda.time.DateTime
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.osgi.framework.Bundle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.extras.api.Contact;
import com.atlassian.extras.api.Organisation;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.PluginMetadata;
import com.atlassian.upm.api.license.entity.Organization;
import com.atlassian.upm.api.license.entity.Partner;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.HostApplicationEmbeddedAddonLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseAttributes;
import com.atlassian.upm.license.internal.LicenseEntityFactory;
import com.atlassian.upm.license.internal.impl.ContactImpl;
import com.atlassian.upm.license.internal.impl.OrganizationImpl;
import com.atlassian.upm.license.internal.impl.PartnerImpl;
import com.atlassian.upm.license.internal.impl.PluginLicenseBuilder;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseEntityFactoryImpl
implements LicenseEntityFactory {
    private static final Logger log = LoggerFactory.getLogger(LicenseEntityFactoryImpl.class);
    private static final DateTimeFormatter BF_BUILD_DATE_FORMAT = DateTimeFormat.forPattern((String)"yyyy-MM-dd").withOffsetParsed();
    private static final DateTimeFormatter TC_BUILD_DATE_FORMAT = DateTimeFormat.forPattern((String)"yyyy-MM-dd HH:mm:ss").withOffsetParsed();
    private final HostApplicationDescriptor hostApplicationDescriptor;
    private final RoleBasedLicensingPluginService roleBasedLicensingPluginService;
    private final ApplicationProperties applicationProperties;
    private final UpmAppManager appManager;
    private static final Map<String, DateTime> greenhopperBuildDateMap = new HashMap<String, DateTime>();

    public LicenseEntityFactoryImpl(HostApplicationDescriptor hostApplicationDescriptor, RoleBasedLicensingPluginService roleBasedLicensingPluginService, ApplicationProperties applicationProperties, UpmAppManager appManager) {
        this.hostApplicationDescriptor = Objects.requireNonNull(hostApplicationDescriptor, "hostApplicationDescriptor");
        this.roleBasedLicensingPluginService = Objects.requireNonNull(roleBasedLicensingPluginService, "roleBasedLicensingPluginService");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.appManager = Objects.requireNonNull(appManager, "appManager");
    }

    @Override
    public com.atlassian.upm.api.license.entity.Contact getContact(Contact contact) {
        return new ContactImpl(contact);
    }

    @Override
    public com.atlassian.upm.api.license.entity.Contact getContact(String name, String email) {
        return new ContactImpl(name, email);
    }

    @Override
    public Organization getOrganization(Organisation organization) {
        return new OrganizationImpl(organization);
    }

    @Override
    public Organization getOrganization(String organizationName) {
        return new OrganizationImpl(organizationName);
    }

    @Override
    public Partner getPartner(com.atlassian.extras.api.Partner partner) {
        return new PartnerImpl(partner);
    }

    @Override
    public Partner getPartner(String partnerName) {
        return new PartnerImpl(partnerName);
    }

    @Override
    public Option<DateTime> getPluginBuildDate(Option<Plugin> maybePlugin) {
        Iterator<DateTime> iterator = PluginMetadata.getPluginBuildDate(maybePlugin).iterator();
        if (iterator.hasNext()) {
            DateTime buildDate = iterator.next();
            return Option.some(buildDate);
        }
        for (Plugin plugin : maybePlugin) {
            Iterator<Bundle> iterator2 = PluginMetadata.getPluginBundle(plugin).iterator();
            if (!iterator2.hasNext()) continue;
            Bundle bundle = iterator2.next();
            return this.getLegacyPluginBuildDate(bundle);
        }
        return Option.none();
    }

    @Override
    public PluginLicense getPluginLicense(ProductLicense pluginLicense, String pluginKey, Option<Plugin> plugin, String rawLicense, HostApplicationLicenseAttributes hostLicense, boolean isForgedLicense) {
        return PluginLicenseBuilder.from(pluginLicense, pluginKey, plugin, rawLicense, isForgedLicense, hostLicense, this.roleBasedLicensingPluginService, this, this.applicationProperties).build();
    }

    @Override
    public PluginLicense getPluginLicense(HostApplicationEmbeddedAddonLicense pluginLicense, Option<Plugin> plugin, HostApplicationLicenseAttributes hostLicense) {
        return PluginLicenseBuilder.from(pluginLicense, plugin, hostLicense, this).build();
    }

    private Option<DateTime> getLegacyPluginBuildDate(Bundle bundle) {
        String pluginKey = bundle.getSymbolicName();
        if (pluginKey.equals("com.pyxis.greenhopper.jira")) {
            return Option.option(greenhopperBuildDateMap.get(bundle.getVersion().toString()));
        }
        if (pluginKey.equals("com.atlassian.confluence.extra.team-calendars")) {
            return this.getTeamCalendarsBuildDateString(bundle).flatMap(this.toDate(TC_BUILD_DATE_FORMAT));
        }
        if (pluginKey.equals("com.atlassian.bonfire.plugin")) {
            return this.getBonfirePluginBuildDateString(bundle).flatMap(this.toDate(BF_BUILD_DATE_FORMAT));
        }
        return Option.none();
    }

    private Option<String> getTeamCalendarsBuildDateString(Bundle bundle) {
        return this.getPropertyFromResourceUrl("build.date", bundle.getResource("com/atlassian/confluence/extra/calendar3/build.properties"));
    }

    private Option<String> getBonfirePluginBuildDateString(Bundle bundle) {
        return this.getPropertyFromResourceUrl("build.time", bundle.getResource("build/build.properties"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Option<String> getPropertyFromResourceUrl(String propertyName, URL resourceUrl) {
        Option<String> option;
        if (resourceUrl == null) {
            return Option.none();
        }
        Properties properties = new Properties();
        InputStream is = resourceUrl.openStream();
        try {
            properties.load(resourceUrl.openStream());
            option = Option.option(properties.getProperty(propertyName));
        }
        catch (Throwable throwable) {
            try {
                IOUtils.closeQuietly((InputStream)is);
                throw throwable;
            }
            catch (IOException e) {
                log.warn("Unable to open stream " + resourceUrl.toString());
                return Option.none();
            }
        }
        IOUtils.closeQuietly((InputStream)is);
        return option;
    }

    private Function<String, Option<DateTime>> toDate(DateTimeFormatter buildDateFormat) {
        return dateString -> {
            try {
                return Option.some(buildDateFormat.parseDateTime(dateString));
            }
            catch (IllegalArgumentException e) {
                log.warn("Invalid Build Date of \"" + dateString + "\"");
                return Option.none();
            }
        };
    }

    static {
        greenhopperBuildDateMap.put("5.9.3", new DateTime(2012, 3, 26, 0, 0, 0, 0));
        greenhopperBuildDateMap.put("5.9.2", new DateTime(2012, 3, 22, 0, 0, 0, 0));
        greenhopperBuildDateMap.put("5.9.1", new DateTime(2012, 3, 12, 0, 0, 0, 0));
        greenhopperBuildDateMap.put("5.9.0", new DateTime(2012, 2, 21, 0, 0, 0, 0));
        greenhopperBuildDateMap.put("5.8.8", new DateTime(2012, 3, 1, 0, 0, 0, 0));
        greenhopperBuildDateMap.put("5.8.7", new DateTime(2012, 2, 21, 0, 0, 0, 0));
        greenhopperBuildDateMap.put("5.8.6", new DateTime(2012, 1, 30, 0, 0, 0, 0));
        greenhopperBuildDateMap.put("5.8.5", new DateTime(2012, 1, 30, 0, 0, 0, 0));
        greenhopperBuildDateMap.put("5.8.4", new DateTime(2011, 12, 1, 0, 0, 0, 0));
        greenhopperBuildDateMap.put("5.8.3", new DateTime(2011, 11, 14, 0, 0, 0, 0));
        greenhopperBuildDateMap.put("5.8.2", new DateTime(2011, 10, 21, 15, 9, 0, 0));
        greenhopperBuildDateMap.put("5.8.1", new DateTime(2011, 10, 19, 10, 45, 0, 0));
        greenhopperBuildDateMap.put("5.8.0", new DateTime(2011, 10, 11, 13, 54, 0, 0));
        greenhopperBuildDateMap.put("5.7.4", new DateTime(2011, 9, 23, 14, 27, 0, 0));
        greenhopperBuildDateMap.put("5.7.3", new DateTime(2011, 9, 20, 14, 44, 0, 0));
        greenhopperBuildDateMap.put("5.7.2", new DateTime(2011, 9, 7, 15, 41, 0, 0));
        greenhopperBuildDateMap.put("5.7.1", new DateTime(2011, 8, 10, 16, 32, 0, 0));
        greenhopperBuildDateMap.put("5.7.0", new DateTime(2011, 7, 25, 12, 37, 0, 0));
        greenhopperBuildDateMap.put("5.6.9", new DateTime(2011, 7, 14, 10, 2, 0, 0));
        greenhopperBuildDateMap.put("5.6.8", new DateTime(2011, 6, 29, 12, 8, 0, 0));
        greenhopperBuildDateMap.put("5.6.7", new DateTime(2011, 6, 16, 12, 19, 0, 0));
        greenhopperBuildDateMap.put("5.6.6", new DateTime(2011, 6, 14, 14, 59, 0, 0));
        greenhopperBuildDateMap.put("5.6.5", new DateTime(2011, 5, 31, 12, 4, 0, 0));
        greenhopperBuildDateMap.put("5.6.4", new DateTime(2011, 5, 23, 16, 35, 0, 0));
        greenhopperBuildDateMap.put("5.6.3", new DateTime(2011, 5, 20, 11, 15, 0, 0));
        greenhopperBuildDateMap.put("5.6.2", new DateTime(2011, 5, 5, 12, 28, 0, 0));
        greenhopperBuildDateMap.put("5.6.1", new DateTime(2011, 4, 13, 12, 0, 0, 0));
        greenhopperBuildDateMap.put("5.6.0", new DateTime(2011, 3, 30, 17, 35, 0, 0));
        greenhopperBuildDateMap.put("5.5.2", new DateTime(2011, 3, 17, 11, 37, 0, 0));
        greenhopperBuildDateMap.put("5.5.1", new DateTime(2011, 3, 17, 10, 31, 0, 0));
        greenhopperBuildDateMap.put("5.5.0", new DateTime(2011, 3, 15, 17, 30, 0, 0));
        greenhopperBuildDateMap.put("5.4.2", new DateTime(2011, 4, 5, 15, 50, 0, 0));
        greenhopperBuildDateMap.put("5.4.1", new DateTime(2011, 1, 12, 15, 28, 0, 0));
        greenhopperBuildDateMap.put("5.4.0", new DateTime(2010, 12, 9, 10, 18, 0, 0));
        greenhopperBuildDateMap.put("5.3.0", new DateTime(2010, 10, 21, 10, 20, 0, 0));
        greenhopperBuildDateMap.put("5.2.4", new DateTime(2010, 10, 13, 11, 34, 0, 0));
        greenhopperBuildDateMap.put("5.2.3", new DateTime(2010, 10, 1, 11, 28, 0, 0));
        greenhopperBuildDateMap.put("5.2.2", new DateTime(2010, 9, 7, 11, 46, 0, 0));
        greenhopperBuildDateMap.put("5.2.1", new DateTime(2010, 9, 1, 16, 26, 0, 0));
        greenhopperBuildDateMap.put("5.2.0", new DateTime(2010, 7, 30, 12, 23, 0, 0));
        greenhopperBuildDateMap.put("5.1.0", new DateTime(2010, 7, 14, 23, 10, 0, 0));
        greenhopperBuildDateMap.put("5.0.1", new DateTime(2010, 7, 1, 17, 37, 0, 0));
        greenhopperBuildDateMap.put("5.0.0", new DateTime(2010, 6, 9, 18, 9, 0, 0));
        greenhopperBuildDateMap.put("4.4.1", new DateTime(2010, 5, 26, 12, 6, 0, 0));
        greenhopperBuildDateMap.put("4.4.0", new DateTime(2010, 3, 28, 19, 41, 0, 0));
        greenhopperBuildDateMap.put("4.3.2", new DateTime(2010, 5, 3, 19, 37, 0, 0));
        greenhopperBuildDateMap.put("4.3.1", new DateTime(2010, 3, 11, 5, 56, 0, 0));
        greenhopperBuildDateMap.put("4.3.0", new DateTime(2010, 3, 2, 23, 46, 0, 0));
        greenhopperBuildDateMap.put("4.3.0.jira40", new DateTime(2009, 12, 3, 21, 2, 0, 0));
        greenhopperBuildDateMap.put("4.2.2.jira40", new DateTime(2009, 12, 22, 21, 1, 0, 0));
        greenhopperBuildDateMap.put("4.2.1.jira40", new DateTime(2009, 12, 8, 22, 29, 0, 0));
        greenhopperBuildDateMap.put("4.2.0.jira40", new DateTime(2009, 11, 19, 14, 26, 0, 0));
        greenhopperBuildDateMap.put("4.1.0.jira40", new DateTime(2009, 10, 27, 0, 5, 0, 0));
        greenhopperBuildDateMap.put("4.0.0.jira40", new DateTime(2009, 10, 20, 23, 20, 0, 0));
        greenhopperBuildDateMap.put("3.7.1.studio-1", new DateTime(2009, 5, 13, 16, 28, 0, 0));
        greenhopperBuildDateMap.put("3.7.0.jirastudio-1", new DateTime(2009, 5, 13, 16, 28, 0, 0));
        greenhopperBuildDateMap.put("3.3.0.studio-4", new DateTime(2009, 5, 8, 10, 28, 0, 0));
        greenhopperBuildDateMap.put("3.3.0.studio-3", new DateTime(2009, 1, 28, 13, 54, 0, 0));
        greenhopperBuildDateMap.put("3.3.0.studio-2", new DateTime(2009, 1, 28, 12, 8, 0, 0));
        greenhopperBuildDateMap.put("3.3.0.studio-1", new DateTime(2009, 1, 15, 13, 36, 0, 0));
        greenhopperBuildDateMap.put("3.3.0.jiraStudio", new DateTime(2009, 1, 15, 13, 36, 0, 0));
        greenhopperBuildDateMap.put("3.1.1.studio-4", new DateTime(2008, 11, 26, 11, 5, 0, 0));
        greenhopperBuildDateMap.put("3.1.1.studio-3", new DateTime(2008, 11, 17, 12, 41, 0, 0));
        greenhopperBuildDateMap.put("3.1.1.jiraStudio", new DateTime(2008, 11, 17, 12, 41, 0, 0));
        greenhopperBuildDateMap.put("3.1.0.studio-2", new DateTime(2008, 10, 14, 15, 21, 0, 0));
        greenhopperBuildDateMap.put("3.1.0.studio-1", new DateTime(2008, 10, 13, 14, 19, 0, 0));
        greenhopperBuildDateMap.put("3.1.0.jiraStudio", new DateTime(2008, 10, 13, 14, 19, 0, 0));
        greenhopperBuildDateMap.put("3.0.2.studio-1", new DateTime(2008, 10, 1, 11, 3, 0, 0));
        greenhopperBuildDateMap.put("3.0.2.jira310", new DateTime(2008, 10, 1, 11, 3, 0, 0));
    }
}

