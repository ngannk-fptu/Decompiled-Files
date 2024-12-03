/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.setup.velocity;

import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface VelocityContextItemProvider {
    public @NonNull Map<String, Object> getContextMap();

    @Deprecated(forRemoval=true)
    public static enum VelocityContextItemKeys {
        HTML_UTIL("htmlUtil"),
        GENERAL_UTIL("generalUtil"),
        TEXT_UTIL("textUtil"),
        FILE_UTIL("fileUtil"),
        SERAPH_UTIL("seraph"),
        BOOTSTRAP_MANAGER("bootstrap"),
        CONFLUENCE_SETUP("setup"),
        SETUP_PERSISTER("setupPersister"),
        STRING_UTILS("stringUtils"),
        SPACE_UTILS("spaceUtils"),
        SYSTEM_PROPERTIES("systemProperties"),
        BUILD_INFO("buildInfo"),
        DECORATOR_UTIL("decoratorUtil"),
        WEBWORK("webwork");

        private final String key;

        private VelocityContextItemKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}

