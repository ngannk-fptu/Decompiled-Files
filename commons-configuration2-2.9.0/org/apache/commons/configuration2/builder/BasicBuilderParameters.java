/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.beanutils.BeanHelper;
import org.apache.commons.configuration2.builder.BasicBuilderProperties;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.convert.ConversionHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.InterpolatorSpecification;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.sync.Synchronizer;

public class BasicBuilderParameters
implements Cloneable,
BuilderParameters,
BasicBuilderProperties<BasicBuilderParameters> {
    private static final String PROP_THROW_EXCEPTION_ON_MISSING = "throwExceptionOnMissing";
    private static final String PROP_LIST_DELIMITER_HANDLER = "listDelimiterHandler";
    private static final String PROP_LOGGER = "logger";
    private static final String PROP_INTERPOLATOR = "interpolator";
    private static final String PROP_PREFIX_LOOKUPS = "prefixLookups";
    private static final String PROP_DEFAULT_LOOKUPS = "defaultLookups";
    private static final String PROP_PARENT_INTERPOLATOR = "parentInterpolator";
    private static final String PROP_SYNCHRONIZER = "synchronizer";
    private static final String PROP_CONVERSION_HANDLER = "conversionHandler";
    private static final String PROP_CONFIGURATION_DECODER = "configurationDecoder";
    private static final String PROP_BEAN_HELPER = "config-BeanHelper";
    private Map<String, Object> properties = new HashMap<String, Object>();

    @Override
    public Map<String, Object> getParameters() {
        HashMap<String, Object> result = new HashMap<String, Object>(this.properties);
        if (result.containsKey(PROP_INTERPOLATOR)) {
            result.remove(PROP_PREFIX_LOOKUPS);
            result.remove(PROP_DEFAULT_LOOKUPS);
            result.remove(PROP_PARENT_INTERPOLATOR);
        }
        BasicBuilderParameters.createDefensiveCopies(result);
        return result;
    }

    @Override
    public BasicBuilderParameters setLogger(ConfigurationLogger log) {
        return this.setProperty(PROP_LOGGER, log);
    }

    @Override
    public BasicBuilderParameters setThrowExceptionOnMissing(boolean b) {
        return this.setProperty(PROP_THROW_EXCEPTION_ON_MISSING, b);
    }

    @Override
    public BasicBuilderParameters setListDelimiterHandler(ListDelimiterHandler handler) {
        return this.setProperty(PROP_LIST_DELIMITER_HANDLER, handler);
    }

    @Override
    public BasicBuilderParameters setInterpolator(ConfigurationInterpolator ci) {
        return this.setProperty(PROP_INTERPOLATOR, ci);
    }

    @Override
    public BasicBuilderParameters setPrefixLookups(Map<String, ? extends Lookup> lookups) {
        if (lookups == null) {
            this.properties.remove(PROP_PREFIX_LOOKUPS);
            return this;
        }
        return this.setProperty(PROP_PREFIX_LOOKUPS, new HashMap<String, Lookup>(lookups));
    }

    @Override
    public BasicBuilderParameters setDefaultLookups(Collection<? extends Lookup> lookups) {
        if (lookups == null) {
            this.properties.remove(PROP_DEFAULT_LOOKUPS);
            return this;
        }
        return this.setProperty(PROP_DEFAULT_LOOKUPS, new ArrayList<Lookup>(lookups));
    }

    @Override
    public BasicBuilderParameters setParentInterpolator(ConfigurationInterpolator parent) {
        return this.setProperty(PROP_PARENT_INTERPOLATOR, parent);
    }

    @Override
    public BasicBuilderParameters setSynchronizer(Synchronizer sync) {
        return this.setProperty(PROP_SYNCHRONIZER, sync);
    }

    @Override
    public BasicBuilderParameters setConversionHandler(ConversionHandler handler) {
        return this.setProperty(PROP_CONVERSION_HANDLER, handler);
    }

    @Override
    public BasicBuilderParameters setBeanHelper(BeanHelper beanHelper) {
        return this.setProperty(PROP_BEAN_HELPER, beanHelper);
    }

    @Override
    public BasicBuilderParameters setConfigurationDecoder(ConfigurationDecoder decoder) {
        return this.setProperty(PROP_CONFIGURATION_DECODER, decoder);
    }

    public void merge(BuilderParameters p) {
        if (p == null) {
            throw new IllegalArgumentException("Parameters to merge must not be null!");
        }
        p.getParameters().forEach((k, v) -> {
            if (!this.properties.containsKey(k) && !k.startsWith("config-")) {
                this.storeProperty((String)k, v);
            }
        });
    }

    public void inheritFrom(Map<String, ?> source) {
        if (source == null) {
            throw new IllegalArgumentException("Source properties must not be null!");
        }
        this.copyPropertiesFrom(source, PROP_BEAN_HELPER, PROP_CONFIGURATION_DECODER, PROP_CONVERSION_HANDLER, PROP_LIST_DELIMITER_HANDLER, PROP_LOGGER, PROP_SYNCHRONIZER, PROP_THROW_EXCEPTION_ON_MISSING);
    }

    public static InterpolatorSpecification fetchInterpolatorSpecification(Map<String, Object> params) {
        BasicBuilderParameters.checkParameters(params);
        return new InterpolatorSpecification.Builder().withInterpolator(BasicBuilderParameters.fetchParameter(params, PROP_INTERPOLATOR, ConfigurationInterpolator.class)).withParentInterpolator(BasicBuilderParameters.fetchParameter(params, PROP_PARENT_INTERPOLATOR, ConfigurationInterpolator.class)).withPrefixLookups(BasicBuilderParameters.fetchAndCheckPrefixLookups(params)).withDefaultLookups(BasicBuilderParameters.fetchAndCheckDefaultLookups(params)).create();
    }

    public static BeanHelper fetchBeanHelper(Map<String, Object> params) {
        BasicBuilderParameters.checkParameters(params);
        return (BeanHelper)params.get(PROP_BEAN_HELPER);
    }

    public BasicBuilderParameters clone() {
        try {
            BasicBuilderParameters copy = (BasicBuilderParameters)super.clone();
            copy.properties = this.getParameters();
            return copy;
        }
        catch (CloneNotSupportedException cnex) {
            throw new AssertionError((Object)cnex);
        }
    }

    protected void storeProperty(String key, Object value) {
        if (value == null) {
            this.properties.remove(key);
        } else {
            this.properties.put(key, value);
        }
    }

    protected Object fetchProperty(String key) {
        return this.properties.get(key);
    }

    protected void copyPropertiesFrom(Map<String, ?> source, String ... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value == null) continue;
            this.storeProperty(key, value);
        }
    }

    private BasicBuilderParameters setProperty(String key, Object value) {
        this.storeProperty(key, value);
        return this;
    }

    private static void createDefensiveCopies(HashMap<String, Object> params) {
        Collection<? extends Lookup> defLookups;
        Map<String, ? extends Lookup> prefixLookups = BasicBuilderParameters.fetchPrefixLookups(params);
        if (prefixLookups != null) {
            params.put(PROP_PREFIX_LOOKUPS, new HashMap<String, Lookup>(prefixLookups));
        }
        if ((defLookups = BasicBuilderParameters.fetchDefaultLookups(params)) != null) {
            params.put(PROP_DEFAULT_LOOKUPS, new ArrayList<Lookup>(defLookups));
        }
    }

    private static Map<String, ? extends Lookup> fetchPrefixLookups(Map<String, Object> params) {
        Map prefixLookups = (Map)params.get(PROP_PREFIX_LOOKUPS);
        return prefixLookups;
    }

    private static Map<String, ? extends Lookup> fetchAndCheckPrefixLookups(Map<String, Object> params) {
        Map prefixes = BasicBuilderParameters.fetchParameter(params, PROP_PREFIX_LOOKUPS, Map.class);
        if (prefixes == null) {
            return null;
        }
        prefixes.forEach((k, v) -> {
            if (!(k instanceof String) || !(v instanceof Lookup)) {
                throw new IllegalArgumentException("Map with prefix lookups contains invalid data: " + prefixes);
            }
        });
        return BasicBuilderParameters.fetchPrefixLookups(params);
    }

    private static Collection<? extends Lookup> fetchDefaultLookups(Map<String, Object> params) {
        Collection defLookups = (Collection)params.get(PROP_DEFAULT_LOOKUPS);
        return defLookups;
    }

    private static Collection<? extends Lookup> fetchAndCheckDefaultLookups(Map<String, Object> params) {
        Collection col = BasicBuilderParameters.fetchParameter(params, PROP_DEFAULT_LOOKUPS, Collection.class);
        if (col == null) {
            return null;
        }
        if (col.stream().noneMatch(o -> o instanceof Lookup)) {
            throw new IllegalArgumentException("Collection with default lookups contains invalid data: " + col);
        }
        return BasicBuilderParameters.fetchDefaultLookups(params);
    }

    private static <T> T fetchParameter(Map<String, Object> params, String key, Class<T> expClass) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        if (!expClass.isInstance(value)) {
            throw new IllegalArgumentException(String.format("Parameter %s is not of type %s!", key, expClass.getSimpleName()));
        }
        return expClass.cast(value);
    }

    private static void checkParameters(Map<String, Object> params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters map must not be null!");
        }
    }
}

