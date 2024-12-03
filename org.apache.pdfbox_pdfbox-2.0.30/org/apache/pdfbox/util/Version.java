/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.pdfbox.io.IOUtils;

public final class Version {
    private static final String PDFBOX_VERSION_PROPERTIES = "/org/apache/pdfbox/resources/version.properties";

    private Version() {
    }

    public static String getVersion() {
        String string;
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(Version.class.getResourceAsStream(PDFBOX_VERSION_PROPERTIES));
            Properties properties = new Properties();
            properties.load(is);
            string = properties.getProperty("pdfbox.version", null);
        }
        catch (IOException io) {
            String string2;
            try {
                string2 = null;
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(is);
                throw throwable;
            }
            IOUtils.closeQuietly(is);
            return string2;
        }
        IOUtils.closeQuietly(is);
        return string;
    }
}

