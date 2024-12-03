/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.host.util;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import org.apache.commons.lang3.StringUtils;

public class InstanceNameGenerator {
    public String generateInstanceName(String baseURL) throws MalformedURLException {
        String hostname = new URL(baseURL).getHost();
        if ("localhost".equals(hostname)) {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            }
            catch (UnknownHostException unknownHostException) {
                // empty catch block
            }
        }
        return StringUtils.split((String)hostname, (String)".")[0];
    }
}

