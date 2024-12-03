/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public enum BuildInformation {
    INSTANCE;

    public static final String TIMESTMP_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String PROPERTIES_LOCATION = "/com/atlassian/confluence/default.properties";
    private static final String DEFAULT_VALUE_NON_EMPTY = "UNKNOWN";
    private final String versionNumber;
    private final Date buildDate;
    private final Date buildTimestamp;
    private final String buildNumber;
    private final String bambooBuildKey;
    private final String bambooBuildNumber;
    private final String gitCommitHash;
    private final String buildYear;
    private final Supplier<String> toStringRepresentation;
    private final String marketplaceBuildNumber;
    private final String bundledSynchronyVersion;
    private final String zduMinVersion;

    private BuildInformation() {
        Properties properties = BuildInformation.getPropertiesFromClasspath(PROPERTIES_LOCATION);
        this.versionNumber = properties.getProperty("version.number");
        this.buildDate = this.convertToDateWithEnglishLocale(properties.getProperty("build.date"));
        this.buildTimestamp = this.convertToTimestampWithEnglishLocale(properties.getProperty("build.timestamp"));
        this.buildNumber = properties.getProperty("build.number");
        this.marketplaceBuildNumber = properties.getProperty("marketplace.build.number");
        this.bambooBuildKey = this.filterInvalidOrDefaultProperty(properties.getProperty("bamboo.build.key"));
        this.bambooBuildNumber = this.filterInvalidOrDefaultProperty(properties.getProperty("bamboo.build.number"));
        this.gitCommitHash = this.filterInvalidOrDefaultProperty(properties.getProperty("git.commit.hash"));
        this.buildYear = String.valueOf(this.getBuildDate().toInstant().atZone(ZoneId.systemDefault()).getYear());
        this.bundledSynchronyVersion = this.filterInvalidOrDefaultProperty(properties.getProperty("bundled.synchrony.version"));
        this.zduMinVersion = properties.getProperty("zdu.minVersion");
        this.toStringRepresentation = Suppliers.memoize(() -> String.format("%s [build %s%s] - synchrony version %s", this.versionNumber, this.buildNumber, StringUtils.isNotBlank((CharSequence)this.gitCommitHash) ? " based on commit hash " + this.gitCommitHash : "", this.bundledSynchronyVersion));
    }

    private static Properties getPropertiesFromClasspath(String location) {
        Properties properties;
        block9: {
            InputStream is = BuildInformation.class.getResourceAsStream(location);
            try {
                if (is == null) {
                    throw BuildInformation.logAndEscape(location, null);
                }
                Properties properties2 = new Properties();
                properties2.load(is);
                properties = properties2;
                if (is == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw BuildInformation.logAndEscape(location, e);
                }
            }
            is.close();
        }
        return properties;
    }

    private static RuntimeException logAndEscape(String location, @Nullable Exception e) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(String.format("Failed to load [%s]", location));
        ClassLoader currentClassLoader = BuildInformation.class.getClassLoader();
        if (currentClassLoader instanceof URLClassLoader) {
            messageBuilder.append(String.format(" from classpath [%s]", ToStringBuilder.reflectionToString((Object)((URLClassLoader)currentClassLoader).getURLs(), (ToStringStyle)ToStringStyle.SIMPLE_STYLE)));
        }
        if (e != null) {
            messageBuilder.append(", see cause");
        }
        messageBuilder.append(".");
        String message = messageBuilder.toString();
        LoggerFactory.getLogger((String)"ROOT").error(message, (Throwable)e);
        return new IllegalStateException(message, e);
    }

    private Date convertToDateWithEnglishLocale(String buildDateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        formatter.setLenient(false);
        try {
            return formatter.parse(buildDateString);
        }
        catch (ParseException e) {
            LoggerFactory.getLogger((String)"ROOT").info(e.getMessage());
            return Calendar.getInstance(Locale.ENGLISH).getTime();
        }
    }

    private Date convertToTimestampWithEnglishLocale(String tmstmp) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIMESTMP_FMT, Locale.ENGLISH);
        formatter.setLenient(false);
        try {
            return formatter.parse(tmstmp);
        }
        catch (ParseException e) {
            LoggerFactory.getLogger((String)"ROOT").info(e.getMessage());
            return Calendar.getInstance(Locale.ENGLISH).getTime();
        }
    }

    private String filterInvalidOrDefaultProperty(String propertyValue) {
        if (StringUtils.isBlank((CharSequence)propertyValue)) {
            return "";
        }
        if (propertyValue.startsWith("@") && propertyValue.endsWith("@")) {
            return "";
        }
        if (propertyValue.startsWith("${") && propertyValue.endsWith("}")) {
            return "";
        }
        if (DEFAULT_VALUE_NON_EMPTY.equals(propertyValue)) {
            return "";
        }
        return propertyValue;
    }

    public String getVersionNumber() {
        return this.versionNumber;
    }

    public @NonNull Date getBuildDate() {
        return new Date(this.buildDate.getTime());
    }

    public String getBuildYear() {
        return this.buildYear;
    }

    public @NonNull Date getBuildTimestamp() {
        return new Date(this.buildTimestamp.getTime());
    }

    public String getBuildNumber() {
        return this.buildNumber;
    }

    public String getGitCommitHash() {
        return this.gitCommitHash;
    }

    public String getBambooBuildKey() {
        return this.bambooBuildKey;
    }

    public String getBambooBuildNumber() {
        return this.bambooBuildNumber;
    }

    public String toString() {
        return (String)this.toStringRepresentation.get();
    }

    public String getMarketplaceBuildNumber() {
        return this.marketplaceBuildNumber;
    }

    public String getBundledSynchronyVersion() {
        return this.bundledSynchronyVersion;
    }

    public String getZduMinVersion() {
        return this.zduMinVersion;
    }
}

