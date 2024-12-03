/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.Closeables
 *  com.google.common.io.Resources
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.google.common.io.Closeables;
import com.google.common.io.Resources;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class MavenUtils {
    private static final Logger logger = LoggerFactory.getLogger(MavenUtils.class);
    private static final String UNKNOWN_VERSION = "unknown";

    MavenUtils() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String getVersion(String groupId, String artifactId) {
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = MavenUtils.getPomInputStreamUrl(groupId, artifactId).openStream();
            props.load(is);
            String string = props.getProperty("version", UNKNOWN_VERSION);
            return string;
        }
        catch (Exception e) {
            logger.debug("Could not find version for maven artifact {}:{}", (Object)groupId, (Object)artifactId);
            logger.debug("Got the following exception:", (Throwable)e);
            String string = UNKNOWN_VERSION;
            return string;
        }
        finally {
            try {
                Closeables.close((Closeable)is, (boolean)true);
            }
            catch (IOException e) {
                logger.debug("Could not find version for maven artifact {}:{}", (Object)groupId, (Object)artifactId);
                logger.debug("IOException should not have been thrown.", (Throwable)e);
            }
        }
    }

    private static URL getPomInputStreamUrl(String groupId, String artifactId) {
        return Resources.getResource(MavenUtils.class, (String)MavenUtils.getPomFilePath(groupId, artifactId));
    }

    private static String getPomFilePath(String groupId, String artifactId) {
        return String.format("/META-INF/maven/%s/%s/pom.properties", groupId, artifactId);
    }
}

