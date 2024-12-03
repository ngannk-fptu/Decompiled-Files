/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.tika.language.LanguageProfile;

@Deprecated
public class LanguageIdentifier {
    private static final Map<String, LanguageProfile> PROFILES = new HashMap<String, LanguageProfile>();
    private static final String PROFILE_SUFFIX = ".ngp";
    private static Properties props = new Properties();
    private static String errors = "";
    private static final String PROPERTIES_OVERRIDE_FILE = "tika.language.override.properties";
    private static final String PROPERTIES_FILE = "tika.language.properties";
    private static final String LANGUAGES_KEY = "languages";
    private static final double CERTAINTY_LIMIT = 0.022;
    private final String language;
    private final double distance;

    private static void addProfile(String language) throws Exception {
        try {
            LanguageProfile profile = new LanguageProfile();
            try (InputStream stream = LanguageIdentifier.class.getResourceAsStream(language + PROFILE_SUFFIX);){
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                String line = reader.readLine();
                while (line != null) {
                    if (line.length() > 0 && !line.startsWith("#")) {
                        int space = line.indexOf(32);
                        profile.add(line.substring(0, space), Long.parseLong(line.substring(space + 1)));
                    }
                    line = reader.readLine();
                }
            }
            LanguageIdentifier.addProfile(language, profile);
        }
        catch (Throwable t) {
            throw new Exception("Failed trying to load language profile for language \"" + language + "\". Error: " + t.getMessage());
        }
    }

    public static void addProfile(String language, LanguageProfile profile) {
        PROFILES.put(language, profile);
    }

    public LanguageIdentifier(LanguageProfile profile) {
        String minLanguage = "unknown";
        double minDistance = 1.0;
        for (Map.Entry<String, LanguageProfile> entry : PROFILES.entrySet()) {
            double distance = profile.distance(entry.getValue());
            if (!(distance < minDistance)) continue;
            minDistance = distance;
            minLanguage = entry.getKey();
        }
        this.language = minLanguage;
        this.distance = minDistance;
    }

    public LanguageIdentifier(String content) {
        this(new LanguageProfile(content));
    }

    public String getLanguage() {
        return this.language;
    }

    public boolean isReasonablyCertain() {
        return this.distance < 0.022;
    }

    public static void initProfiles() {
        String[] languages;
        LanguageIdentifier.clearProfiles();
        errors = "";
        InputStream stream = LanguageIdentifier.class.getResourceAsStream(PROPERTIES_OVERRIDE_FILE);
        if (stream == null) {
            stream = LanguageIdentifier.class.getResourceAsStream(PROPERTIES_FILE);
        }
        if (stream != null) {
            try {
                props = new Properties();
                props.load(stream);
            }
            catch (IOException e) {
                errors = errors + "IOException while trying to load property file. Message: " + e.getMessage() + "\n";
            }
        }
        for (String language : languages = props.getProperty(LANGUAGES_KEY).split(",")) {
            language = language.trim();
            String name = props.getProperty("name." + language, "Unknown");
            try {
                LanguageIdentifier.addProfile(language);
            }
            catch (Exception e) {
                errors = errors + "Language " + language + " (" + name + ") not initialized. Message: " + e.getMessage() + "\n";
            }
        }
    }

    public static void initProfiles(Map<String, LanguageProfile> profilesMap) {
        LanguageIdentifier.clearProfiles();
        for (Map.Entry<String, LanguageProfile> entry : profilesMap.entrySet()) {
            LanguageIdentifier.addProfile(entry.getKey(), entry.getValue());
        }
    }

    public static void clearProfiles() {
        PROFILES.clear();
    }

    public static boolean hasErrors() {
        return errors != "";
    }

    public static String getErrors() {
        return errors;
    }

    public static Set<String> getSupportedLanguages() {
        return PROFILES.keySet();
    }

    public String toString() {
        return this.language + " (" + this.distance + ")";
    }

    static {
        LanguageIdentifier.initProfiles();
    }
}

