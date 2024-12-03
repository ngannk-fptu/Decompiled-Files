/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.daemon.support;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;

public final class DaemonConfiguration {
    protected static final String DEFAULT_CONFIG = "daemon.properties";
    protected static final String PREFIX = "daemon.";
    private static final String BTOKEN = "${";
    private static final String ETOKEN = "}";
    private final Properties configurationProperties = new Properties();
    private final Properties systemProperties = System.getProperties();
    static final String[] EMPTY_STRING_ARRAY = new String[0];

    public boolean load(String fileName) {
        boolean bl;
        if (fileName == null) {
            fileName = DEFAULT_CONFIG;
        }
        FileInputStream inputStream = new FileInputStream(fileName);
        try {
            this.configurationProperties.clear();
            this.configurationProperties.load(inputStream);
            bl = true;
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)inputStream).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException ex) {
                return false;
            }
        }
        ((InputStream)inputStream).close();
        return bl;
    }

    private String expandProperty(String propValue) throws ParseException {
        int ctoken = 0;
        if (propValue == null) {
            return null;
        }
        StringBuilder expanded = new StringBuilder();
        int btoken = propValue.indexOf(BTOKEN);
        while (btoken != -1) {
            if (btoken > 0 && propValue.charAt(btoken - 1) == BTOKEN.charAt(0)) {
                expanded.append(propValue.substring(ctoken, btoken));
                ctoken = btoken + 1;
                btoken = propValue.indexOf(BTOKEN, btoken + BTOKEN.length());
                continue;
            }
            int etoken = propValue.indexOf(ETOKEN, btoken);
            if (etoken == -1) {
                throw new ParseException("Error while looking for teminating '}'", btoken);
            }
            String variable = propValue.substring(btoken + BTOKEN.length(), etoken);
            String sysvalue = this.systemProperties.getProperty(variable);
            if (sysvalue == null) {
                sysvalue = System.getenv(variable);
            }
            if (sysvalue != null) {
                String strtoken = propValue.substring(ctoken, btoken);
                expanded.append(strtoken);
                expanded.append(sysvalue);
                ctoken = etoken + ETOKEN.length();
            }
            btoken = propValue.indexOf(BTOKEN, etoken + ETOKEN.length());
        }
        expanded.append(propValue.substring(ctoken));
        return expanded.toString();
    }

    public String getProperty(String name) throws ParseException {
        if (name == null) {
            return null;
        }
        return this.expandProperty(this.configurationProperties.getProperty(PREFIX + name));
    }

    public String[] getPropertyArray(String name) throws ParseException {
        String args;
        ArrayList<String> list = new ArrayList<String>();
        while ((args = this.getProperty(name + "[" + list.size() + "]")) != null) {
            list.add(args);
        }
        return list.toArray(EMPTY_STRING_ARRAY);
    }
}

