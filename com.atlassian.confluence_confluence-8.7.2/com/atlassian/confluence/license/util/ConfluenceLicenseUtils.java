/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.extras.common.LicensePropertiesConstants
 *  com.atlassian.fugue.Option
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.license.util;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.extras.common.LicensePropertiesConstants;
import com.atlassian.fugue.Option;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Internal
public class ConfluenceLicenseUtils {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceLicenseUtils.class);
    private static final long UPDATE_ALLOWED_PERIOD = 31622400000L;
    private static final long ALMOST_EXPIRED_PERIOD = 3628800000L;
    private static final int YEAR_ATLASSIAN_FOUNDED = 2002;
    public static final Product CONFLUENCE_QUESTION = new Product("Confluence Questions", "com.atlassian.confluence.plugins.confluence-questions", true);

    @Deprecated
    public static boolean isDataCenter(ConfluenceLicense license) {
        return license.isClusteringEnabled();
    }

    @Deprecated
    public static @NonNull Option<Integer> numberOfClusterNodes(ConfluenceLicense license) {
        return ConfluenceLicenseUtils.obtainIntegerProperty(license, LicensePropertiesConstants.getKey((Product)license.getProduct(), (String)"NumberOfClusterNodes"));
    }

    public static long getSupportPeriodEnd(ConfluenceLicense license) {
        DateTime when;
        Date maintenanceExpiryDate = license.getMaintenanceExpiryDate();
        boolean validMaintenanceExpiryDate = maintenanceExpiryDate == null ? false : (when = new DateTime((Object)maintenanceExpiryDate)).getYear() >= 2002;
        return validMaintenanceExpiryDate ? maintenanceExpiryDate.getTime() : license.getCreationDate().getTime() + 31622400000L;
    }

    public static long getSupportPeriodAlmostExpiredDate(ConfluenceLicense license) {
        return ConfluenceLicenseUtils.getSupportPeriodEnd(license) - 3628800000L;
    }

    private static @NonNull Option<Integer> obtainIntegerProperty(ConfluenceLicense license, String key) {
        String strValue = license.getProperty(key);
        if (strValue == null) {
            return Option.none();
        }
        Integer result = null;
        try {
            result = Integer.parseInt(strValue);
        }
        catch (NumberFormatException ex) {
            log.warn("Unable to parse '{}' as an integer", (Object)strValue);
        }
        return Option.option((Object)result);
    }
}

