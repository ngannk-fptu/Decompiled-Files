/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.lookup;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.text.lookup.BiFunctionStringLookup;
import org.apache.commons.text.lookup.BiStringLookup;
import org.apache.commons.text.lookup.ConstantStringLookup;
import org.apache.commons.text.lookup.DateStringLookup;
import org.apache.commons.text.lookup.DefaultStringLookup;
import org.apache.commons.text.lookup.DnsStringLookup;
import org.apache.commons.text.lookup.FileStringLookup;
import org.apache.commons.text.lookup.FunctionStringLookup;
import org.apache.commons.text.lookup.InterpolatorStringLookup;
import org.apache.commons.text.lookup.JavaPlatformStringLookup;
import org.apache.commons.text.lookup.LocalHostStringLookup;
import org.apache.commons.text.lookup.PropertiesStringLookup;
import org.apache.commons.text.lookup.ResourceBundleStringLookup;
import org.apache.commons.text.lookup.ScriptStringLookup;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.lookup.UrlDecoderStringLookup;
import org.apache.commons.text.lookup.UrlEncoderStringLookup;
import org.apache.commons.text.lookup.UrlStringLookup;
import org.apache.commons.text.lookup.XmlStringLookup;

public final class StringLookupFactory {
    public static final StringLookupFactory INSTANCE = new StringLookupFactory();
    static final FunctionStringLookup<String> INSTANCE_BASE64_DECODER = FunctionStringLookup.on(key -> new String(Base64.getDecoder().decode((String)key), StandardCharsets.ISO_8859_1));
    static final FunctionStringLookup<String> INSTANCE_BASE64_ENCODER = FunctionStringLookup.on(key -> Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.ISO_8859_1)));
    static final FunctionStringLookup<String> INSTANCE_ENVIRONMENT_VARIABLES = FunctionStringLookup.on(System::getenv);
    static final FunctionStringLookup<String> INSTANCE_NULL = FunctionStringLookup.on(key -> null);
    static final FunctionStringLookup<String> INSTANCE_SYSTEM_PROPERTIES = FunctionStringLookup.on(System::getProperty);
    public static final String KEY_BASE64_DECODER = "base64Decoder";
    public static final String KEY_BASE64_ENCODER = "base64Encoder";
    public static final String KEY_CONST = "const";
    public static final String KEY_DATE = "date";
    public static final String KEY_DNS = "dns";
    public static final String KEY_ENV = "env";
    public static final String KEY_FILE = "file";
    public static final String KEY_JAVA = "java";
    public static final String KEY_LOCALHOST = "localhost";
    public static final String KEY_PROPERTIES = "properties";
    public static final String KEY_RESOURCE_BUNDLE = "resourceBundle";
    public static final String KEY_SCRIPT = "script";
    public static final String KEY_SYS = "sys";
    public static final String KEY_URL = "url";
    public static final String KEY_URL_DECODER = "urlDecoder";
    public static final String KEY_URL_ENCODER = "urlEncoder";
    public static final String KEY_XML = "xml";
    public static final String DEFAULT_STRING_LOOKUPS_PROPERTY = "org.apache.commons.text.lookup.StringLookupFactory.defaultStringLookups";

    public static void clear() {
        ConstantStringLookup.clear();
    }

    static String toKey(String key) {
        return key.toLowerCase(Locale.ROOT);
    }

    static <K, V> Map<K, V> toMap(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    private StringLookupFactory() {
    }

    public void addDefaultStringLookups(Map<String, StringLookup> stringLookupMap) {
        if (stringLookupMap != null) {
            stringLookupMap.putAll(DefaultStringLookupsHolder.INSTANCE.getDefaultStringLookups());
        }
    }

    public StringLookup base64DecoderStringLookup() {
        return INSTANCE_BASE64_DECODER;
    }

    public StringLookup base64EncoderStringLookup() {
        return INSTANCE_BASE64_ENCODER;
    }

    @Deprecated
    public StringLookup base64StringLookup() {
        return INSTANCE_BASE64_DECODER;
    }

    public <R, U> BiStringLookup<U> biFunctionStringLookup(BiFunction<String, U, R> biFunction) {
        return BiFunctionStringLookup.on(biFunction);
    }

    public StringLookup constantStringLookup() {
        return ConstantStringLookup.INSTANCE;
    }

    public StringLookup dateStringLookup() {
        return DateStringLookup.INSTANCE;
    }

    public StringLookup dnsStringLookup() {
        return DnsStringLookup.INSTANCE;
    }

    public StringLookup environmentVariableStringLookup() {
        return INSTANCE_ENVIRONMENT_VARIABLES;
    }

    public StringLookup fileStringLookup() {
        return FileStringLookup.INSTANCE;
    }

    public <R> StringLookup functionStringLookup(Function<String, R> function) {
        return FunctionStringLookup.on(function);
    }

    public StringLookup interpolatorStringLookup() {
        return InterpolatorStringLookup.INSTANCE;
    }

    public StringLookup interpolatorStringLookup(Map<String, StringLookup> stringLookupMap, StringLookup defaultStringLookup, boolean addDefaultLookups) {
        return new InterpolatorStringLookup(stringLookupMap, defaultStringLookup, addDefaultLookups);
    }

    public <V> StringLookup interpolatorStringLookup(Map<String, V> map) {
        return new InterpolatorStringLookup(map);
    }

    public StringLookup interpolatorStringLookup(StringLookup defaultStringLookup) {
        return new InterpolatorStringLookup(defaultStringLookup);
    }

    public StringLookup javaPlatformStringLookup() {
        return JavaPlatformStringLookup.INSTANCE;
    }

    public StringLookup localHostStringLookup() {
        return LocalHostStringLookup.INSTANCE;
    }

    public <V> StringLookup mapStringLookup(Map<String, V> map) {
        return FunctionStringLookup.on(map);
    }

    public StringLookup nullStringLookup() {
        return INSTANCE_NULL;
    }

    public StringLookup propertiesStringLookup() {
        return PropertiesStringLookup.INSTANCE;
    }

    public StringLookup resourceBundleStringLookup() {
        return ResourceBundleStringLookup.INSTANCE;
    }

    public StringLookup resourceBundleStringLookup(String bundleName) {
        return new ResourceBundleStringLookup(bundleName);
    }

    public StringLookup scriptStringLookup() {
        return ScriptStringLookup.INSTANCE;
    }

    public StringLookup systemPropertyStringLookup() {
        return INSTANCE_SYSTEM_PROPERTIES;
    }

    public StringLookup urlDecoderStringLookup() {
        return UrlDecoderStringLookup.INSTANCE;
    }

    public StringLookup urlEncoderStringLookup() {
        return UrlEncoderStringLookup.INSTANCE;
    }

    public StringLookup urlStringLookup() {
        return UrlStringLookup.INSTANCE;
    }

    public StringLookup xmlStringLookup() {
        return XmlStringLookup.INSTANCE;
    }

    static final class DefaultStringLookupsHolder {
        static final DefaultStringLookupsHolder INSTANCE = new DefaultStringLookupsHolder(System.getProperties());
        private final Map<String, StringLookup> defaultStringLookups;

        private static void addLookup(DefaultStringLookup lookup, Map<String, StringLookup> map) {
            map.put(StringLookupFactory.toKey(lookup.getKey()), lookup.getStringLookup());
            if (DefaultStringLookup.BASE64_DECODER.equals((Object)lookup)) {
                map.put(StringLookupFactory.toKey("base64"), lookup.getStringLookup());
            }
        }

        private static Map<String, StringLookup> createDefaultStringLookups() {
            HashMap<String, StringLookup> lookupMap = new HashMap<String, StringLookup>();
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.BASE64_DECODER, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.BASE64_ENCODER, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.CONST, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.DATE, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.ENVIRONMENT, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.FILE, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.JAVA, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.LOCAL_HOST, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.PROPERTIES, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.RESOURCE_BUNDLE, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.SYSTEM_PROPERTIES, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.URL_DECODER, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.URL_ENCODER, lookupMap);
            DefaultStringLookupsHolder.addLookup(DefaultStringLookup.XML, lookupMap);
            return lookupMap;
        }

        private static Map<String, StringLookup> parseStringLookups(String str) {
            HashMap<String, StringLookup> lookupMap = new HashMap<String, StringLookup>();
            try {
                for (String lookupName : str.split("[\\s,]+")) {
                    if (lookupName.isEmpty()) continue;
                    DefaultStringLookupsHolder.addLookup(DefaultStringLookup.valueOf(lookupName.toUpperCase()), lookupMap);
                }
            }
            catch (IllegalArgumentException exc) {
                throw new IllegalArgumentException("Invalid default string lookups definition: " + str, exc);
            }
            return lookupMap;
        }

        DefaultStringLookupsHolder(Properties props) {
            Map<String, StringLookup> lookups = props.containsKey(StringLookupFactory.DEFAULT_STRING_LOOKUPS_PROPERTY) ? DefaultStringLookupsHolder.parseStringLookups(props.getProperty(StringLookupFactory.DEFAULT_STRING_LOOKUPS_PROPERTY)) : DefaultStringLookupsHolder.createDefaultStringLookups();
            this.defaultStringLookups = Collections.unmodifiableMap(lookups);
        }

        Map<String, StringLookup> getDefaultStringLookups() {
            return this.defaultStringLookups;
        }
    }
}

