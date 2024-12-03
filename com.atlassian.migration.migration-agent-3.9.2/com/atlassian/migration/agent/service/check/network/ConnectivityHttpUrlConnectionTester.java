/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.network;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.service.check.network.ConnectivityTester;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectivityHttpUrlConnectionTester
implements ConnectivityTester {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ConnectivityHttpUrlConnectionTester.class);
    public static final String GET = "GET";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isReachable(String url, int timeout, TimeUnit timeUnit) {
        HttpURLConnection connection = null;
        try {
            URL siteUrl = this.getUrl(url);
            connection = (HttpURLConnection)siteUrl.openConnection();
            connection.setRequestMethod(GET);
            connection.setConnectTimeout(Math.toIntExact(timeUnit.toMillis(timeout)));
            connection.connect();
            boolean bl = true;
            return bl;
        }
        catch (IOException e) {
            log.error("Error during check connectivity for domain: {}", (Object)url, (Object)e);
            boolean bl = false;
            return bl;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @NotNull
    @VisibleForTesting
    URL getUrl(String url) throws MalformedURLException {
        return new URL(url);
    }
}

