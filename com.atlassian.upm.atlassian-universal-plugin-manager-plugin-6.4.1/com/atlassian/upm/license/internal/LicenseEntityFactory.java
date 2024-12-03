/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Contact
 *  com.atlassian.extras.api.Organisation
 *  com.atlassian.extras.api.Partner
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.plugin.Plugin
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal;

import com.atlassian.extras.api.Contact;
import com.atlassian.extras.api.Organisation;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.plugin.Plugin;
import com.atlassian.upm.api.license.entity.Organization;
import com.atlassian.upm.api.license.entity.Partner;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.HostApplicationEmbeddedAddonLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseAttributes;
import org.joda.time.DateTime;

public interface LicenseEntityFactory {
    public com.atlassian.upm.api.license.entity.Contact getContact(Contact var1);

    public com.atlassian.upm.api.license.entity.Contact getContact(String var1, String var2);

    public Organization getOrganization(Organisation var1);

    public Organization getOrganization(String var1);

    public Partner getPartner(com.atlassian.extras.api.Partner var1);

    public Partner getPartner(String var1);

    public Option<DateTime> getPluginBuildDate(Option<Plugin> var1);

    public PluginLicense getPluginLicense(ProductLicense var1, String var2, Option<Plugin> var3, String var4, HostApplicationLicenseAttributes var5, boolean var6);

    public PluginLicense getPluginLicense(HostApplicationEmbeddedAddonLicense var1, Option<Plugin> var2, HostApplicationLicenseAttributes var3);
}

