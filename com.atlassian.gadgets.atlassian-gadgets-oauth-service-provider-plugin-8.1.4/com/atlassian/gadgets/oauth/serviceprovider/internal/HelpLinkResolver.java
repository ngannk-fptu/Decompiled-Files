/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.oauth.serviceprovider.internal;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public final class HelpLinkResolver {
    private final Properties properties;
    private final String baseUrl;

    public HelpLinkResolver() {
        this("/com/atlassian/gadgets/oauth/serviceprovider/internal/help-links.properties");
    }

    public HelpLinkResolver(String fileName) {
        this(HelpLinkResolver.loadProperties(fileName));
    }

    public HelpLinkResolver(InputStream is) {
        this(HelpLinkResolver.loadProperties(is));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Properties loadProperties(String fileName) {
        InputStream is = HelpLinkResolver.class.getResourceAsStream(fileName);
        try {
            Properties properties = HelpLinkResolver.loadProperties(is);
            return properties;
        }
        finally {
            try {
                is.close();
            }
            catch (IOException iOException) {}
        }
    }

    private static Properties loadProperties(InputStream is) {
        Properties properties = new Properties();
        try {
            properties.load(is);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    public HelpLinkResolver(Properties properties) {
        this.properties = properties;
        this.baseUrl = properties.getProperty("base.url");
    }

    public String getLink(String name) {
        Preconditions.checkNotNull((Object)name, (Object)"name");
        String key = this.properties.containsKey(name) ? name : "default.page";
        return this.baseUrl + this.properties.getProperty(key);
    }
}

