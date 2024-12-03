/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.Analytics
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.api.annotations.Analytics;
import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.analytics.client.api.browser.BrowserEvent;
import com.atlassian.analytics.client.api.mobile.MobileEvent;
import com.atlassian.analytics.client.extractor.PropertyContributor;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyExtractorHelper {
    private static final Logger log = LoggerFactory.getLogger(PropertyExtractorHelper.class);
    protected final Iterable<PropertyContributor> propertyDecorators;
    protected final Set<String> excludeProperties;

    public PropertyExtractorHelper(Set<String> excludeProperties, PropertyContributor ... propertyContributors) {
        this.excludeProperties = excludeProperties;
        this.propertyDecorators = Arrays.asList(propertyContributors);
    }

    public Map<String, Object> extractProperty(String name, Object value) {
        ImmutableMap.Builder result = ImmutableMap.builder();
        if (value instanceof Optional) {
            value = ((Optional)value).orElse(null);
        }
        if (this.excludeProperties.contains(name) || value == null) {
            return Collections.emptyMap();
        }
        if (value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Enum) {
            result.put((Object)name, value);
        } else if (value instanceof Character) {
            result.put((Object)name, (Object)value.toString());
        } else if (value instanceof Date) {
            result.put((Object)name, (Object)this.formatDate((Date)value));
        }
        this.putNonCoreJavaTypes((ImmutableMap.Builder<String, Object>)result, name, value);
        if (value instanceof Collection) {
            Collection collection = (Collection)value;
            result.put((Object)(name + ".size"), (Object)String.valueOf(collection.size()));
            int index = 0;
            for (Object o : collection) {
                result.putAll(this.extractProperty(name + "[" + index++ + "]", o));
            }
        }
        if (value instanceof Map) {
            Map map = (Map)value;
            result.put((Object)(name + ".size"), (Object)String.valueOf(map.size()));
            for (Map.Entry entry : map.entrySet()) {
                result.putAll(this.extractProperty(name + "." + entry.getKey(), entry.getValue()));
            }
        }
        return result.build();
    }

    protected void putNonCoreJavaTypes(ImmutableMap.Builder<String, Object> result, String name, Object value) {
        for (PropertyContributor propertyContributor : this.propertyDecorators) {
            propertyContributor.contribute(result, name, value);
        }
    }

    public boolean isExcluded(String name) {
        return this.excludeProperties.contains(name);
    }

    public String extractSubProduct(Object event, String product) {
        if (event instanceof BrowserEvent) {
            return "browser";
        }
        if (event instanceof MobileEvent) {
            return "mobile";
        }
        String eventPackage = event.getClass().getPackage().getName().toLowerCase();
        StringBuilder subProduct = new StringBuilder();
        int wordNo = 0;
        int wordsOut = 0;
        boolean strippingPrefix = true;
        for (String word : eventPackage.split("\\.")) {
            boolean exclude = false;
            if (wordNo < 3 && strippingPrefix) {
                if (wordNo == 0 && word.equals("com") || wordNo == 1 && word.equals("atlassian") || wordNo == 2 && word.equals(product)) {
                    exclude = true;
                } else {
                    strippingPrefix = false;
                }
            }
            if (!exclude && (word.equals("event") || word.equals("events") || word.equals("plugin") || word.equals("plugins"))) {
                exclude = true;
            }
            if (!exclude) {
                if (wordsOut > 0) {
                    subProduct.append('.');
                }
                subProduct.append(word);
                ++wordsOut;
            }
            ++wordNo;
        }
        return subProduct.toString();
    }

    public String extractName(Object event) {
        Class<?> clazz = event.getClass();
        String nameFromMethod = this.extractNameFromMethodAnnotation(event, clazz);
        if (null != nameFromMethod) {
            return nameFromMethod;
        }
        String nameFromClass = this.extractNameFromClassAnnotation(clazz);
        if (null != nameFromClass) {
            return nameFromClass;
        }
        String nameFromClassAnalytics = this.extractNameFromClassAnnotationAnalytics(clazz);
        if (null != nameFromClassAnalytics) {
            return nameFromClassAnalytics;
        }
        return this.extractNameFromClassName(clazz);
    }

    @Nullable
    public String extractRequestCorrelationId(RequestInfo requestInfo) {
        return requestInfo.getB3TraceId();
    }

    private String extractNameFromMethodAnnotation(Object event, Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Collection annotatedMethods = Collections2.filter(Arrays.asList(methods), (Predicate)new Predicate<Method>(){

            public boolean apply(Method method) {
                try {
                    return null != method.getAnnotation(EventName.class);
                }
                catch (NoClassDefFoundError error) {
                    return false;
                }
            }
        });
        int numOfMethods = annotatedMethods.size();
        if (numOfMethods > 0) {
            if (numOfMethods > 1) {
                log.warn("More than one @EventName annotated methods found in class {}", (Object)clazz.getName());
            }
            Method method = (Method)annotatedMethods.iterator().next();
            try {
                Object result = method.invoke(event, new Object[0]);
                return result == null ? null : String.valueOf(result);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Failed to execute " + clazz.getName() + "." + method.getName() + " to calculate event name: " + e.getMessage(), (Throwable)e);
                return null;
            }
        }
        return null;
    }

    private String extractNameFromClassAnnotation(Class<?> clazz) {
        try {
            EventName eventName = clazz.getAnnotation(EventName.class);
            if (null != eventName && null != eventName.value() && !"".equals(eventName.value())) {
                return eventName.value();
            }
            return null;
        }
        catch (NoClassDefFoundError error) {
            return null;
        }
    }

    private String extractNameFromClassAnnotationAnalytics(Class<?> clazz) {
        Analytics analytics = clazz.getAnnotation(Analytics.class);
        if (null != analytics && null != analytics.value() && !"".equals(analytics.value())) {
            return analytics.value();
        }
        return null;
    }

    private String extractNameFromClassName(Class<?> clazz) {
        String result = clazz.getSimpleName().toLowerCase();
        if (result.endsWith("event")) {
            result = result.substring(0, result.length() - 5);
        }
        return result;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}

