/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.interpol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.Lookup;

public final class InterpolatorSpecification {
    private final ConfigurationInterpolator interpolator;
    private final ConfigurationInterpolator parentInterpolator;
    private final Map<String, Lookup> prefixLookups;
    private final Collection<Lookup> defaultLookups;
    private final Function<Object, String> stringConverter;

    private InterpolatorSpecification(Builder builder) {
        this.interpolator = builder.interpolator;
        this.parentInterpolator = builder.parentInterpolator;
        this.prefixLookups = Collections.unmodifiableMap(new HashMap(builder.prefixLookups));
        this.defaultLookups = Collections.unmodifiableCollection(new ArrayList(builder.defLookups));
        this.stringConverter = builder.stringConverter;
    }

    public ConfigurationInterpolator getInterpolator() {
        return this.interpolator;
    }

    public ConfigurationInterpolator getParentInterpolator() {
        return this.parentInterpolator;
    }

    public Map<String, Lookup> getPrefixLookups() {
        return this.prefixLookups;
    }

    public Collection<Lookup> getDefaultLookups() {
        return this.defaultLookups;
    }

    public Function<Object, String> getStringConverter() {
        return this.stringConverter;
    }

    public static class Builder {
        private final Map<String, Lookup> prefixLookups = new HashMap<String, Lookup>();
        private final Collection<Lookup> defLookups = new LinkedList<Lookup>();
        private ConfigurationInterpolator interpolator;
        private ConfigurationInterpolator parentInterpolator;
        private Function<Object, String> stringConverter;

        public Builder withPrefixLookup(String prefix, Lookup lookup) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix must not be null!");
            }
            Builder.checkLookup(lookup);
            this.prefixLookups.put(prefix, lookup);
            return this;
        }

        public Builder withPrefixLookups(Map<String, ? extends Lookup> lookups) {
            if (lookups != null) {
                lookups.forEach(this::withPrefixLookup);
            }
            return this;
        }

        public Builder withDefaultLookup(Lookup lookup) {
            Builder.checkLookup(lookup);
            this.defLookups.add(lookup);
            return this;
        }

        public Builder withDefaultLookups(Collection<? extends Lookup> lookups) {
            if (lookups != null) {
                lookups.forEach(this::withDefaultLookup);
            }
            return this;
        }

        public Builder withInterpolator(ConfigurationInterpolator ci) {
            this.interpolator = ci;
            return this;
        }

        public Builder withParentInterpolator(ConfigurationInterpolator parent) {
            this.parentInterpolator = parent;
            return this;
        }

        public Builder withStringConverter(Function<Object, String> fn) {
            this.stringConverter = fn;
            return this;
        }

        public InterpolatorSpecification create() {
            InterpolatorSpecification spec = new InterpolatorSpecification(this);
            this.reset();
            return spec;
        }

        public void reset() {
            this.interpolator = null;
            this.parentInterpolator = null;
            this.prefixLookups.clear();
            this.defLookups.clear();
            this.stringConverter = null;
        }

        private static void checkLookup(Lookup lookup) {
            if (lookup == null) {
                throw new IllegalArgumentException("Lookup must not be null!");
            }
        }
    }
}

