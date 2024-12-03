/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CleanLogging {
    private Map<String, Function<String, String>> environmentMapForCopy = Collections.emptyMap();
    private Map<String, Function<String, String>> environmentMapForSplit = Collections.emptyMap();
    private Set<String> suppressed = Collections.emptySet();

    public void setEnvironmentConfigFilename(String filename) {
        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream(filename);){
            String copy = "copy-";
            String split = "split-";
            String blacklist = "blacklist";
            Properties p = new Properties();
            p.load(stream);
            LinkedHashMap<String, Function<String, String>> copyConfig = new LinkedHashMap<String, Function<String, String>>();
            LinkedHashMap<String, Function<String, String>> splitConfig = new LinkedHashMap<String, Function<String, String>>();
            for (String prefix : p.stringPropertyNames()) {
                boolean splitEnv;
                if (Objects.equals(prefix, "blacklist")) {
                    this.suppressed = Arrays.stream(p.getProperty("blacklist").split(",")).map(String::trim).collect(Collectors.toSet());
                    continue;
                }
                boolean copyEnv = prefix.startsWith("copy-") && this.updateConfig(prefix.substring("copy-".length()), p.getProperty(prefix), copyConfig);
                boolean bl = splitEnv = prefix.startsWith("split-") && this.updateConfig(prefix.substring("split-".length()), p.getProperty(prefix), splitConfig);
                if (copyEnv || splitEnv) continue;
                System.err.println("Did not understand property in : " + filename + " : " + prefix);
            }
            this.environmentMapForCopy = Collections.unmodifiableMap(copyConfig);
            this.environmentMapForSplit = Collections.unmodifiableMap(splitConfig);
        }
        catch (IOException e) {
            System.err.println("Unable to read env overrides from input stream : ");
            e.printStackTrace(System.err);
        }
    }

    public Optional<String> getEnvironmentSuffixForSplit(String loggerName) {
        return this.getEnvironmentSuffix(this.environmentMapForSplit, loggerName);
    }

    public Optional<String> getEnvironmentSuffixForCopy(String loggerName) {
        return this.getEnvironmentSuffix(this.environmentMapForCopy, loggerName);
    }

    public boolean isSuppressed(String property) {
        return this.suppressed.contains(property);
    }

    private Optional<String> getEnvironmentSuffix(Map<String, Function<String, String>> mapping, String loggerName) {
        return mapping.values().stream().map(f -> (String)f.apply(loggerName)).filter(Objects::nonNull).findFirst();
    }

    private boolean updateConfig(String key, String value, Map<String, Function<String, String>> config) {
        String prefix = "prefix";
        String suffix = "suffix";
        int dot = key.indexOf(46);
        String mode = key.substring(0, dot);
        if (prefix.equals(mode) || suffix.equals(mode)) {
            config.put(key.substring(dot + 1), this.matcher(key.substring(dot + 1), value, prefix.equals(mode)));
            return true;
        }
        return false;
    }

    private Function<String, String> matcher(String pattern, String value, boolean prefix) {
        if (prefix) {
            return key -> key.startsWith(pattern) ? value : null;
        }
        return key -> key.endsWith(pattern) ? value : null;
    }
}

