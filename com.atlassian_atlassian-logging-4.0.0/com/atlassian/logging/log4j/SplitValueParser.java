/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.status.StatusLogger
 */
package com.atlassian.logging.log4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.status.StatusLogger;

public class SplitValueParser {
    private final String separator;
    private final String prefix;

    public SplitValueParser(String separator, String prefix) {
        this.separator = separator;
        this.prefix = prefix;
    }

    public Set<String> parse(String inputSpec) {
        HashSet<String> values = new HashSet<String>();
        this.parseFilterSpec(inputSpec, values);
        return values;
    }

    private void parseFilterSpec(String inputSpec, Set<String> values) {
        if (StringUtils.isNotBlank((CharSequence)inputSpec)) {
            if (inputSpec.startsWith("@")) {
                this.parseFilterSpecViaClassPath(inputSpec.substring(1), values);
            } else {
                this.parseFilterSpecStrings(inputSpec, values);
            }
        }
    }

    private void parseFilterSpecStrings(String inputSpec, Set<String> values) {
        if (StringUtils.isNotBlank((CharSequence)inputSpec)) {
            String[] split;
            for (String filter : split = inputSpec.split(this.separator)) {
                String trimmed = StringUtils.trim((String)filter);
                if (!StringUtils.isNotBlank((CharSequence)trimmed)) continue;
                values.add(this.prefix + trimmed);
            }
        }
    }

    private void parseFilterSpecViaClassPath(String inputSpec, Set<String> values) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(inputSpec);
        if (stream != null) {
            this.parseAsPropertiesFile(inputSpec, stream, values);
        } else {
            StatusLogger.getLogger().error("Unable to load spec : " + inputSpec);
        }
    }

    private void parseAsPropertiesFile(String inputSpec, InputStream stream, Set<String> values) {
        Properties p = new Properties();
        try {
            p.load(stream);
            for (Object key : p.keySet()) {
                this.parseFilterSpecStrings(p.getProperty(key.toString()), values);
            }
        }
        catch (IOException e) {
            StatusLogger.getLogger().error("Unable to read spec input stream : " + inputSpec);
        }
    }
}

