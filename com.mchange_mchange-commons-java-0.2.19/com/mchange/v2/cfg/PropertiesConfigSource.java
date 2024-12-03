/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cfg;

import com.mchange.v2.cfg.DelayedLogItem;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;

public interface PropertiesConfigSource {
    public Parse propertiesFromSource(String var1) throws FileNotFoundException, Exception;

    public static class Parse {
        private Properties properties;
        private List<DelayedLogItem> parseMessages;

        public Properties getProperties() {
            return this.properties;
        }

        public List<DelayedLogItem> getDelayedLogItems() {
            return this.parseMessages;
        }

        public Parse(Properties properties, List<DelayedLogItem> list) {
            this.properties = properties;
            this.parseMessages = list;
        }
    }
}

