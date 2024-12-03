/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import org.apache.commons.text.lookup.StringLookup;

@Deprecated
public abstract class StrLookup<V>
implements StringLookup {
    private static final StrLookup<String> NONE_LOOKUP = new MapStrLookup<String>(null);
    private static final StrLookup<String> SYSTEM_PROPERTIES_LOOKUP = new SystemPropertiesStrLookup();

    public static <V> StrLookup<V> mapLookup(Map<String, V> map) {
        return new MapStrLookup<V>(map);
    }

    public static StrLookup<?> noneLookup() {
        return NONE_LOOKUP;
    }

    public static StrLookup<String> resourceBundleLookup(ResourceBundle resourceBundle) {
        return new ResourceBundleLookup(resourceBundle);
    }

    public static StrLookup<String> systemPropertiesLookup() {
        return SYSTEM_PROPERTIES_LOOKUP;
    }

    protected StrLookup() {
    }

    private static final class SystemPropertiesStrLookup
    extends StrLookup<String> {
        private SystemPropertiesStrLookup() {
        }

        @Override
        public String lookup(String key) {
            if (!key.isEmpty()) {
                try {
                    return System.getProperty(key);
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
            }
            return null;
        }
    }

    private static final class ResourceBundleLookup
    extends StrLookup<String> {
        private final ResourceBundle resourceBundle;

        private ResourceBundleLookup(ResourceBundle resourceBundle) {
            this.resourceBundle = resourceBundle;
        }

        @Override
        public String lookup(String key) {
            if (this.resourceBundle == null || key == null || !this.resourceBundle.containsKey(key)) {
                return null;
            }
            return this.resourceBundle.getString(key);
        }

        public String toString() {
            return super.toString() + " [resourceBundle=" + this.resourceBundle + "]";
        }
    }

    static class MapStrLookup<V>
    extends StrLookup<V> {
        private final Map<String, V> map;

        MapStrLookup(Map<String, V> map) {
            this.map = map != null ? map : Collections.emptyMap();
        }

        @Override
        public String lookup(String key) {
            return Objects.toString(this.map.get(key), null);
        }

        public String toString() {
            return super.toString() + " [map=" + this.map + "]";
        }
    }
}

