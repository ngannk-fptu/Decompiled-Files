/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import org.codehaus.groovy.tools.shell.IO;

public class Preferences {
    private static final java.util.prefs.Preferences STORE = java.util.prefs.Preferences.userRoot().node("/org/codehaus/groovy/tools/shell");
    public static IO.Verbosity verbosity;
    public static final String VERBOSITY_KEY = "verbosity";
    public static final String SHOW_LAST_RESULT_KEY = "show-last-result";
    public static final String SANITIZE_STACK_TRACE_KEY = "sanitize-stack-trace";
    public static final String EDITOR_KEY = "editor";
    public static final String PARSER_FLAVOR_KEY = "parser-flavor";
    public static final String PARSER_RIGID = "rigid";
    public static final String PARSER_RELAXED = "relaxed";

    public static boolean getShowLastResult() {
        return STORE.getBoolean(SHOW_LAST_RESULT_KEY, true);
    }

    public static boolean getSanitizeStackTrace() {
        return STORE.getBoolean(SANITIZE_STACK_TRACE_KEY, true);
    }

    public static String getEditor() {
        return STORE.get(EDITOR_KEY, System.getenv("EDITOR"));
    }

    public static String getParserFlavor() {
        return STORE.get(PARSER_FLAVOR_KEY, PARSER_RIGID);
    }

    public static String[] keys() throws BackingStoreException {
        return STORE.keys();
    }

    public static String get(String name, String defaultValue) {
        return STORE.get(name, defaultValue);
    }

    public static String get(String name) {
        return Preferences.get(name, null);
    }

    public static void put(String name, String value) {
        STORE.put(name, value);
    }

    public static void clear() throws BackingStoreException {
        STORE.clear();
    }

    public static void addChangeListener(PreferenceChangeListener listener) {
        STORE.addPreferenceChangeListener(listener);
    }

    static {
        String tmp = STORE.get(VERBOSITY_KEY, IO.Verbosity.INFO.name);
        try {
            verbosity = IO.Verbosity.forName(tmp);
        }
        catch (IllegalArgumentException e) {
            verbosity = IO.Verbosity.INFO;
            STORE.remove(VERBOSITY_KEY);
        }
        Preferences.addChangeListener(new PreferenceChangeListener(){

            @Override
            public void preferenceChange(PreferenceChangeEvent event) {
                if (event.getKey().equals(Preferences.VERBOSITY_KEY)) {
                    String name = event.getNewValue();
                    if (name == null) {
                        name = IO.Verbosity.INFO.name;
                    }
                    try {
                        verbosity = IO.Verbosity.forName(name);
                    }
                    catch (Exception e) {
                        event.getNode().put(event.getKey(), Preferences.verbosity.name);
                    }
                }
            }
        });
    }
}

