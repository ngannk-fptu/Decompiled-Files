/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.core.header.LanguageTag;
import com.sun.jersey.core.util.FeaturesAndProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

public abstract class ResourceConfig
extends Application
implements FeaturesAndProperties {
    private static final Logger LOGGER = Logger.getLogger(ResourceConfig.class.getName());
    public static final String FEATURE_NORMALIZE_URI = "com.sun.jersey.config.feature.NormalizeURI";
    public static final String FEATURE_CANONICALIZE_URI_PATH = "com.sun.jersey.config.feature.CanonicalizeURIPath";
    public static final String FEATURE_REDIRECT = "com.sun.jersey.config.feature.Redirect";
    public static final String FEATURE_MATCH_MATRIX_PARAMS = "com.sun.jersey.config.feature.IgnoreMatrixParams";
    public static final String FEATURE_IMPLICIT_VIEWABLES = "com.sun.jersey.config.feature.ImplicitViewables";
    public static final String FEATURE_DISABLE_WADL = "com.sun.jersey.config.feature.DisableWADL";
    public static final String FEATURE_TRACE = "com.sun.jersey.config.feature.Trace";
    public static final String FEATURE_TRACE_PER_REQUEST = "com.sun.jersey.config.feature.TracePerRequest";
    public static final String PROPERTY_MEDIA_TYPE_MAPPINGS = "com.sun.jersey.config.property.MediaTypeMappings";
    public static final String PROPERTY_LANGUAGE_MAPPINGS = "com.sun.jersey.config.property.LanguageMappings";
    public static final String PROPERTY_DEFAULT_RESOURCE_COMPONENT_PROVIDER_FACTORY_CLASS = "com.sun.jersey.config.property.DefaultResourceComponentProviderFactoryClass";
    public static final String PROPERTY_CONTAINER_NOTIFIER = "com.sun.jersey.spi.container.ContainerNotifier";
    public static final String PROPERTY_CONTAINER_REQUEST_FILTERS = "com.sun.jersey.spi.container.ContainerRequestFilters";
    public static final String PROPERTY_CONTAINER_RESPONSE_FILTERS = "com.sun.jersey.spi.container.ContainerResponseFilters";
    public static final String PROPERTY_RESOURCE_FILTER_FACTORIES = "com.sun.jersey.spi.container.ResourceFilters";
    public static final String PROPERTY_WADL_GENERATOR_CONFIG = "com.sun.jersey.config.property.WadlGeneratorConfig";
    public static final String COMMON_DELIMITERS = " ,;\n";

    @Override
    public abstract Map<String, Boolean> getFeatures();

    @Override
    public abstract boolean getFeature(String var1);

    @Override
    public abstract Map<String, Object> getProperties();

    @Override
    public abstract Object getProperty(String var1);

    public Map<String, MediaType> getMediaTypeMappings() {
        return Collections.emptyMap();
    }

    public Map<String, String> getLanguageMappings() {
        return Collections.emptyMap();
    }

    public Map<String, Object> getExplicitRootResources() {
        return Collections.emptyMap();
    }

    public void validate() {
        Iterator<Class<?>> i = this.getClasses().iterator();
        while (i.hasNext()) {
            Class<?> c = i.next();
            for (Object o : this.getSingletons()) {
                if (!c.isInstance(o)) continue;
                i.remove();
                LOGGER.log(Level.WARNING, "Class " + c.getName() + " is ignored as an instance is registered in the set of singletons");
            }
        }
        HashSet objectClassSet = new HashSet();
        HashSet conflictSet = new HashSet();
        for (Object object : this.getSingletons()) {
            if (!object.getClass().isAnnotationPresent(Path.class)) continue;
            if (objectClassSet.contains(object.getClass())) {
                conflictSet.add(object.getClass());
                continue;
            }
            objectClassSet.add(object.getClass());
        }
        if (!conflictSet.isEmpty()) {
            for (Class clazz : conflictSet) {
                LOGGER.log(Level.SEVERE, "Root resource class " + clazz.getName() + " is instantiated more than once in the set of registered singletons");
            }
            throw new IllegalArgumentException("The set of registered singletons contains more than one instance of the same root resource class");
        }
        this.parseAndValidateMappings(PROPERTY_MEDIA_TYPE_MAPPINGS, this.getMediaTypeMappings(), new TypeParser<MediaType>(){

            @Override
            public MediaType valueOf(String value) {
                return MediaType.valueOf(value);
            }
        });
        this.parseAndValidateMappings(PROPERTY_LANGUAGE_MAPPINGS, this.getLanguageMappings(), new TypeParser<String>(){

            @Override
            public String valueOf(String value) {
                return LanguageTag.valueOf(value).toString();
            }
        });
        this.encodeKeys(this.getMediaTypeMappings());
        this.encodeKeys(this.getLanguageMappings());
    }

    private <T> void parseAndValidateMappings(String property, Map<String, T> mappingsMap, TypeParser<T> parser) {
        Object mappings = this.getProperty(property);
        if (mappings == null) {
            return;
        }
        if (mappings instanceof String) {
            this.parseMappings(property, (String)mappings, mappingsMap, parser);
        } else if (mappings instanceof String[]) {
            String[] mappingsArray = (String[])mappings;
            for (int i = 0; i < mappingsArray.length; ++i) {
                this.parseMappings(property, mappingsArray[i], mappingsMap, parser);
            }
        } else {
            throw new IllegalArgumentException("Provided " + property + " mappings is invalid. Acceptable types are String and String[].");
        }
    }

    private <T> void parseMappings(String property, String mappings, Map<String, T> mappingsMap, TypeParser<T> parser) {
        if (mappings == null) {
            return;
        }
        String[] records = mappings.split(",");
        for (int i = 0; i < records.length; ++i) {
            String[] record = records[i].split(":");
            if (record.length != 2) {
                throw new IllegalArgumentException("Provided " + property + " mapping \"" + mappings + "\" is invalid. It should contain two parts, key and value, separated by ':'.");
            }
            String trimmedSegment = record[0].trim();
            String trimmedValue = record[1].trim();
            if (trimmedSegment.length() == 0) {
                throw new IllegalArgumentException("The key in " + property + " mappings record \"" + records[i] + "\" is empty.");
            }
            if (trimmedValue.length() == 0) {
                throw new IllegalArgumentException("The value in " + property + " mappings record \"" + records[i] + "\" is empty.");
            }
            mappingsMap.put(trimmedSegment, parser.valueOf(trimmedValue));
        }
    }

    private <T> void encodeKeys(Map<String, T> map) {
        HashMap<String, T> tempMap = new HashMap<String, T>();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            tempMap.put(UriComponent.contextualEncode(entry.getKey(), UriComponent.Type.PATH_SEGMENT), entry.getValue());
        }
        map.clear();
        map.putAll(tempMap);
    }

    public Set<Class<?>> getRootResourceClasses() {
        LinkedHashSet s = new LinkedHashSet();
        for (Class<?> c : this.getClasses()) {
            if (!ResourceConfig.isRootResourceClass(c)) continue;
            s.add(c);
        }
        return s;
    }

    public Set<Class<?>> getProviderClasses() {
        LinkedHashSet s = new LinkedHashSet();
        for (Class<?> c : this.getClasses()) {
            if (ResourceConfig.isRootResourceClass(c)) continue;
            s.add(c);
        }
        return s;
    }

    public Set<Object> getRootResourceSingletons() {
        LinkedHashSet<Object> s = new LinkedHashSet<Object>();
        for (Object o : this.getSingletons()) {
            if (!ResourceConfig.isRootResourceClass(o.getClass())) continue;
            s.add(o);
        }
        return s;
    }

    public Set<Object> getProviderSingletons() {
        LinkedHashSet<Object> s = new LinkedHashSet<Object>();
        for (Object o : this.getSingletons()) {
            if (ResourceConfig.isRootResourceClass(o.getClass())) continue;
            s.add(o);
        }
        return s;
    }

    public static boolean isRootResourceClass(Class<?> c) {
        if (c == null) {
            return false;
        }
        if (c.isAnnotationPresent(Path.class)) {
            return true;
        }
        for (Class<?> i : c.getInterfaces()) {
            if (!i.isAnnotationPresent(Path.class)) continue;
            return true;
        }
        return false;
    }

    public static boolean isProviderClass(Class<?> c) {
        return c != null && c.isAnnotationPresent(Provider.class);
    }

    public List getContainerRequestFilters() {
        return this.getFilterList(PROPERTY_CONTAINER_REQUEST_FILTERS);
    }

    public List getContainerResponseFilters() {
        return this.getFilterList(PROPERTY_CONTAINER_RESPONSE_FILTERS);
    }

    public List getResourceFilterFactories() {
        return this.getFilterList(PROPERTY_RESOURCE_FILTER_FACTORIES);
    }

    private List getFilterList(String propertyName) {
        Object o = this.getProperty(propertyName);
        if (o == null) {
            ArrayList l = new ArrayList();
            this.getProperties().put(propertyName, l);
            return l;
        }
        if (o instanceof List) {
            return (List)o;
        }
        ArrayList<Object> l = new ArrayList<Object>();
        l.add(o);
        this.getProperties().put(propertyName, l);
        return l;
    }

    public void setPropertiesAndFeatures(Map<String, Object> entries) {
        for (Map.Entry<String, Object> e : entries.entrySet()) {
            if (!this.getProperties().containsKey(e.getKey())) {
                this.getProperties().put(e.getKey(), e.getValue());
            }
            if (this.getFeatures().containsKey(e.getKey())) continue;
            Object v = e.getValue();
            if (v instanceof String) {
                String sv = ((String)v).trim();
                if (sv.equalsIgnoreCase("true")) {
                    this.getFeatures().put(e.getKey(), true);
                    continue;
                }
                if (!sv.equalsIgnoreCase("false")) continue;
                this.getFeatures().put(e.getKey(), false);
                continue;
            }
            if (!(v instanceof Boolean)) continue;
            this.getFeatures().put(e.getKey(), (Boolean)v);
        }
    }

    public void add(Application app) {
        if (app.getClasses() != null) {
            this.addAllFirst(this.getClasses(), app.getClasses());
        }
        if (app.getSingletons() != null) {
            this.addAllFirst(this.getSingletons(), app.getSingletons());
        }
        if (app instanceof ResourceConfig) {
            ResourceConfig rc = (ResourceConfig)app;
            this.getExplicitRootResources().putAll(rc.getExplicitRootResources());
            this.getLanguageMappings().putAll(rc.getLanguageMappings());
            this.getMediaTypeMappings().putAll(rc.getMediaTypeMappings());
            this.getFeatures().putAll(rc.getFeatures());
            this.getProperties().putAll(rc.getProperties());
        }
    }

    private <T> void addAllFirst(Set<T> a, Set<T> b) {
        LinkedHashSet<T> x = new LinkedHashSet<T>();
        x.addAll(b);
        x.addAll(a);
        a.clear();
        a.addAll(x);
    }

    public ResourceConfig clone() {
        DefaultResourceConfig that = new DefaultResourceConfig();
        ((Application)that).getClasses().addAll(this.getClasses());
        ((Application)that).getSingletons().addAll(this.getSingletons());
        ((ResourceConfig)that).getExplicitRootResources().putAll(this.getExplicitRootResources());
        ((ResourceConfig)that).getLanguageMappings().putAll(this.getLanguageMappings());
        ((ResourceConfig)that).getMediaTypeMappings().putAll(this.getMediaTypeMappings());
        ((ResourceConfig)that).getFeatures().putAll(this.getFeatures());
        ((ResourceConfig)that).getProperties().putAll(this.getProperties());
        return that;
    }

    public static String[] getElements(String[] elements) {
        return ResourceConfig.getElements(elements, ";");
    }

    public static String[] getElements(String[] elements, String delimiters) {
        LinkedList<String> es = new LinkedList<String>();
        for (String element : elements) {
            if (element == null || (element = element.trim()).length() == 0) continue;
            for (String subElement : ResourceConfig.getElements(element, delimiters)) {
                if (subElement == null || subElement.length() == 0) continue;
                es.add(subElement);
            }
        }
        return es.toArray(new String[es.size()]);
    }

    private static String[] getElements(String elements, String delimiters) {
        String regex = "[";
        for (char c : delimiters.toCharArray()) {
            regex = regex + Pattern.quote(String.valueOf(c));
        }
        regex = regex + "]";
        String[] es = elements.split(regex);
        for (int i = 0; i < es.length; ++i) {
            es[i] = es[i].trim();
        }
        return es;
    }

    private static interface TypeParser<T> {
        public T valueOf(String var1);
    }
}

