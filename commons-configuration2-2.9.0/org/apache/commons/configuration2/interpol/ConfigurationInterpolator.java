/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringSubstitutor
 */
package org.apache.commons.configuration2.interpol;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import org.apache.commons.configuration2.interpol.DefaultLookups;
import org.apache.commons.configuration2.interpol.DummyLookup;
import org.apache.commons.configuration2.interpol.InterpolatorSpecification;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.text.StringSubstitutor;

public class ConfigurationInterpolator {
    public static final String DEFAULT_PREFIX_LOOKUPS_PROPERTY = "org.apache.commons.configuration2.interpol.ConfigurationInterpolator.defaultPrefixLookups";
    private static final char PREFIX_SEPARATOR = ':';
    private static final String VAR_START = "${";
    private static final int VAR_START_LENGTH = "${".length();
    private static final String VAR_END = "}";
    private static final int VAR_END_LENGTH = "}".length();
    private final Map<String, Lookup> prefixLookups;
    private final List<Lookup> defaultLookups;
    private final StringSubstitutor substitutor;
    private volatile ConfigurationInterpolator parentInterpolator;
    private volatile Function<Object, String> stringConverter = DefaultStringConverter.INSTANCE;

    public ConfigurationInterpolator() {
        this.prefixLookups = new ConcurrentHashMap<String, Lookup>();
        this.defaultLookups = new CopyOnWriteArrayList<Lookup>();
        this.substitutor = this.initSubstitutor();
    }

    private static ConfigurationInterpolator createInterpolator(InterpolatorSpecification spec) {
        ConfigurationInterpolator ci = new ConfigurationInterpolator();
        ci.addDefaultLookups(spec.getDefaultLookups());
        ci.registerLookups(spec.getPrefixLookups());
        ci.setParentInterpolator(spec.getParentInterpolator());
        ci.setStringConverter(spec.getStringConverter());
        return ci;
    }

    private static String extractVariableName(String strValue) {
        return strValue.substring(VAR_START_LENGTH, strValue.length() - VAR_END_LENGTH);
    }

    public static ConfigurationInterpolator fromSpecification(InterpolatorSpecification spec) {
        if (spec == null) {
            throw new IllegalArgumentException("InterpolatorSpecification must not be null!");
        }
        return spec.getInterpolator() != null ? spec.getInterpolator() : ConfigurationInterpolator.createInterpolator(spec);
    }

    public static Map<String, Lookup> getDefaultPrefixLookups() {
        return DefaultPrefixLookupsHolder.INSTANCE.getDefaultPrefixLookups();
    }

    public static Lookup nullSafeLookup(Lookup lookup) {
        if (lookup == null) {
            lookup = DummyLookup.INSTANCE;
        }
        return lookup;
    }

    public void addDefaultLookup(Lookup defaultLookup) {
        this.defaultLookups.add(defaultLookup);
    }

    public void addDefaultLookups(Collection<? extends Lookup> lookups) {
        if (lookups != null) {
            this.defaultLookups.addAll(lookups);
        }
    }

    public boolean deregisterLookup(String prefix) {
        return this.prefixLookups.remove(prefix) != null;
    }

    protected Lookup fetchLookupForPrefix(String prefix) {
        return ConfigurationInterpolator.nullSafeLookup(this.prefixLookups.get(prefix));
    }

    public List<Lookup> getDefaultLookups() {
        return new ArrayList<Lookup>(this.defaultLookups);
    }

    public Map<String, Lookup> getLookups() {
        return new HashMap<String, Lookup>(this.prefixLookups);
    }

    public ConfigurationInterpolator getParentInterpolator() {
        return this.parentInterpolator;
    }

    private StringSubstitutor initSubstitutor() {
        return new StringSubstitutor(key -> {
            Object value = this.resolve(key);
            return value != null ? this.stringConverter.apply(value) : null;
        });
    }

    public Object interpolate(Object value) {
        if (value instanceof String) {
            Object resolvedValue;
            String strValue = (String)value;
            if (this.isSingleVariable(strValue) && (resolvedValue = this.resolveSingleVariable(strValue)) != null && !(resolvedValue instanceof String)) {
                return resolvedValue;
            }
            return this.substitutor.replace(strValue);
        }
        return value;
    }

    public boolean isEnableSubstitutionInVariables() {
        return this.substitutor.isEnableSubstitutionInVariables();
    }

    public Function<Object, String> getStringConverter() {
        return this.stringConverter;
    }

    public void setStringConverter(Function<Object, String> stringConverter) {
        this.stringConverter = stringConverter != null ? stringConverter : DefaultStringConverter.INSTANCE;
    }

    private boolean isSingleVariable(String strValue) {
        return strValue.startsWith(VAR_START) && strValue.indexOf(VAR_END, VAR_START_LENGTH) == strValue.length() - VAR_END_LENGTH;
    }

    public Set<String> prefixSet() {
        return Collections.unmodifiableSet(this.prefixLookups.keySet());
    }

    public void registerLookup(String prefix, Lookup lookup) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix for lookup object must not be null!");
        }
        if (lookup == null) {
            throw new IllegalArgumentException("Lookup object must not be null!");
        }
        this.prefixLookups.put(prefix, lookup);
    }

    public void registerLookups(Map<String, ? extends Lookup> lookups) {
        if (lookups != null) {
            this.prefixLookups.putAll(lookups);
        }
    }

    public boolean removeDefaultLookup(Lookup lookup) {
        return this.defaultLookups.remove(lookup);
    }

    public Object resolve(String var) {
        Object value;
        if (var == null) {
            return null;
        }
        int prefixPos = var.indexOf(58);
        if (prefixPos >= 0) {
            String prefix = var.substring(0, prefixPos);
            String name = var.substring(prefixPos + 1);
            value = this.fetchLookupForPrefix(prefix).lookup(name);
            if (value != null) {
                return value;
            }
        }
        for (Lookup lookup : this.defaultLookups) {
            value = lookup.lookup(var);
            if (value == null) continue;
            return value;
        }
        ConfigurationInterpolator parent = this.getParentInterpolator();
        if (parent != null) {
            return this.getParentInterpolator().resolve(var);
        }
        return null;
    }

    private Object resolveSingleVariable(String strValue) {
        return this.resolve(ConfigurationInterpolator.extractVariableName(strValue));
    }

    public void setEnableSubstitutionInVariables(boolean f) {
        this.substitutor.setEnableSubstitutionInVariables(f);
    }

    public void setParentInterpolator(ConfigurationInterpolator parentInterpolator) {
        this.parentInterpolator = parentInterpolator;
    }

    private static final class DefaultStringConverter
    implements Function<Object, String> {
        static final DefaultStringConverter INSTANCE = new DefaultStringConverter();

        private DefaultStringConverter() {
        }

        @Override
        public String apply(Object obj) {
            return Objects.toString(this.extractSimpleValue(obj), null);
        }

        private Object extractSimpleValue(Object obj) {
            if (!(obj instanceof String)) {
                if (obj instanceof Iterable) {
                    return this.nextOrNull(((Iterable)obj).iterator());
                }
                if (obj instanceof Iterator) {
                    return this.nextOrNull((Iterator)obj);
                }
                if (obj.getClass().isArray()) {
                    return Array.getLength(obj) > 0 ? Array.get(obj, 0) : null;
                }
            }
            return obj;
        }

        private <T> T nextOrNull(Iterator<T> it) {
            return it.hasNext() ? (T)it.next() : null;
        }
    }

    static final class DefaultPrefixLookupsHolder {
        static final DefaultPrefixLookupsHolder INSTANCE = new DefaultPrefixLookupsHolder(System.getProperties());
        private final Map<String, Lookup> defaultLookups;

        DefaultPrefixLookupsHolder(Properties props) {
            Map<String, Lookup> lookups = props.containsKey(ConfigurationInterpolator.DEFAULT_PREFIX_LOOKUPS_PROPERTY) ? DefaultPrefixLookupsHolder.parseLookups(props.getProperty(ConfigurationInterpolator.DEFAULT_PREFIX_LOOKUPS_PROPERTY)) : DefaultPrefixLookupsHolder.createDefaultLookups();
            this.defaultLookups = Collections.unmodifiableMap(lookups);
        }

        Map<String, Lookup> getDefaultPrefixLookups() {
            return this.defaultLookups;
        }

        private static Map<String, Lookup> createDefaultLookups() {
            HashMap<String, Lookup> lookupMap = new HashMap<String, Lookup>();
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.BASE64_DECODER, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.BASE64_ENCODER, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.CONST, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.DATE, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.ENVIRONMENT, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.FILE, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.JAVA, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.LOCAL_HOST, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.PROPERTIES, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.RESOURCE_BUNDLE, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.SYSTEM_PROPERTIES, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.URL_DECODER, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.URL_ENCODER, lookupMap);
            DefaultPrefixLookupsHolder.addLookup(DefaultLookups.XML, lookupMap);
            return lookupMap;
        }

        private static Map<String, Lookup> parseLookups(String str) {
            HashMap<String, Lookup> lookupMap = new HashMap<String, Lookup>();
            try {
                for (String lookupName : str.split("[\\s,]+")) {
                    if (lookupName.isEmpty()) continue;
                    DefaultPrefixLookupsHolder.addLookup(DefaultLookups.valueOf(lookupName.toUpperCase()), lookupMap);
                }
            }
            catch (IllegalArgumentException exc) {
                throw new IllegalArgumentException("Invalid default lookups definition: " + str, exc);
            }
            return lookupMap;
        }

        private static void addLookup(DefaultLookups lookup, Map<String, Lookup> map) {
            map.put(lookup.getPrefix(), lookup.getLookup());
        }
    }
}

