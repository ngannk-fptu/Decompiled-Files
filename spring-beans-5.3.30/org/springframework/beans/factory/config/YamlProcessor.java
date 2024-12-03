/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.CollectionFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.yaml.snakeyaml.DumperOptions
 *  org.yaml.snakeyaml.LoaderOptions
 *  org.yaml.snakeyaml.Yaml
 *  org.yaml.snakeyaml.constructor.BaseConstructor
 *  org.yaml.snakeyaml.constructor.Constructor
 *  org.yaml.snakeyaml.reader.UnicodeReader
 *  org.yaml.snakeyaml.representer.Representer
 */
package org.springframework.beans.factory.config;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.CollectionFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

public abstract class YamlProcessor {
    private final Log logger = LogFactory.getLog(this.getClass());
    private ResolutionMethod resolutionMethod = ResolutionMethod.OVERRIDE;
    private Resource[] resources = new Resource[0];
    private List<DocumentMatcher> documentMatchers = Collections.emptyList();
    private boolean matchDefault = true;
    private Set<String> supportedTypes = Collections.emptySet();

    public void setDocumentMatchers(DocumentMatcher ... matchers) {
        this.documentMatchers = Arrays.asList(matchers);
    }

    public void setMatchDefault(boolean matchDefault) {
        this.matchDefault = matchDefault;
    }

    public void setResolutionMethod(ResolutionMethod resolutionMethod) {
        Assert.notNull((Object)((Object)resolutionMethod), (String)"ResolutionMethod must not be null");
        this.resolutionMethod = resolutionMethod;
    }

    public void setResources(Resource ... resources) {
        this.resources = resources;
    }

    public void setSupportedTypes(Class<?> ... supportedTypes) {
        if (ObjectUtils.isEmpty((Object[])supportedTypes)) {
            this.supportedTypes = Collections.emptySet();
        } else {
            Assert.noNullElements((Object[])supportedTypes, (String)"'supportedTypes' must not contain null elements");
            this.supportedTypes = Arrays.stream(supportedTypes).map(Class::getName).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
        }
    }

    protected void process(MatchCallback callback) {
        Yaml yaml = this.createYaml();
        for (Resource resource : this.resources) {
            boolean found = this.process(callback, yaml, resource);
            if (this.resolutionMethod != ResolutionMethod.FIRST_FOUND || !found) continue;
            return;
        }
    }

    protected Yaml createYaml() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(false);
        DumperOptions dumperOptions = new DumperOptions();
        return new Yaml((BaseConstructor)new FilteringConstructor(loaderOptions), new Representer(dumperOptions), dumperOptions, loaderOptions);
    }

    private boolean process(MatchCallback callback, Yaml yaml, Resource resource) {
        int count = 0;
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Loading from YAML: " + resource));
            }
            try (UnicodeReader reader = new UnicodeReader(resource.getInputStream());){
                for (Object object : yaml.loadAll((Reader)reader)) {
                    if (object == null || !this.process(this.asMap(object), callback)) continue;
                    ++count;
                    if (this.resolutionMethod != ResolutionMethod.FIRST_FOUND) continue;
                    break;
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Loaded " + count + " document" + (count > 1 ? "s" : "") + " from YAML resource: " + resource));
                }
            }
        }
        catch (IOException ex) {
            this.handleProcessError(resource, ex);
        }
        return count > 0;
    }

    private void handleProcessError(Resource resource, IOException ex) {
        if (this.resolutionMethod != ResolutionMethod.FIRST_FOUND && this.resolutionMethod != ResolutionMethod.OVERRIDE_AND_IGNORE) {
            throw new IllegalStateException(ex);
        }
        if (this.logger.isWarnEnabled()) {
            this.logger.warn((Object)("Could not load map from " + resource + ": " + ex.getMessage()));
        }
    }

    private Map<String, Object> asMap(Object object) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        if (!(object instanceof Map)) {
            result.put("document", object);
            return result;
        }
        Map map = (Map)object;
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                value = this.asMap(value);
            }
            if (key instanceof CharSequence) {
                result.put(key.toString(), value);
            } else {
                result.put("[" + key.toString() + "]", value);
            }
        });
        return result;
    }

    private boolean process(Map<String, Object> map, MatchCallback callback) {
        Properties properties = CollectionFactory.createStringAdaptingProperties();
        properties.putAll(this.getFlattenedMap(map));
        if (this.documentMatchers.isEmpty()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Merging document (no matchers set): " + map));
            }
            callback.process(properties, map);
            return true;
        }
        MatchStatus result = MatchStatus.ABSTAIN;
        for (DocumentMatcher matcher : this.documentMatchers) {
            MatchStatus match = matcher.matches(properties);
            result = MatchStatus.getMostSpecific(match, result);
            if (match != MatchStatus.FOUND) continue;
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Matched document with document matcher: " + properties));
            }
            callback.process(properties, map);
            return true;
        }
        if (result == MatchStatus.ABSTAIN && this.matchDefault) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Matched document with default matcher: " + map));
            }
            callback.process(properties, map);
            return true;
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Unmatched document: " + map));
        }
        return false;
    }

    protected final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        this.buildFlattenedMap(result, source, null);
        return result;
    }

    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, @Nullable String path) {
        source.forEach((key, value) -> {
            if (StringUtils.hasText((String)path)) {
                key = key.startsWith("[") ? path + key : path + '.' + key;
            }
            if (value instanceof String) {
                result.put((String)key, value);
            } else if (value instanceof Map) {
                Map map = (Map)value;
                this.buildFlattenedMap(result, map, (String)key);
            } else if (value instanceof Collection) {
                Collection collection = (Collection)value;
                if (collection.isEmpty()) {
                    result.put((String)key, "");
                } else {
                    int count = 0;
                    for (Object object : collection) {
                        this.buildFlattenedMap(result, Collections.singletonMap("[" + count++ + "]", object), (String)key);
                    }
                }
            } else {
                result.put((String)key, value != null ? value : "");
            }
        });
    }

    private class FilteringConstructor
    extends Constructor {
        FilteringConstructor(LoaderOptions loaderOptions) {
            super(loaderOptions);
        }

        protected Class<?> getClassForName(String name) throws ClassNotFoundException {
            Assert.state((boolean)YamlProcessor.this.supportedTypes.contains(name), () -> "Unsupported type encountered in YAML document: " + name);
            return super.getClassForName(name);
        }
    }

    public static enum ResolutionMethod {
        OVERRIDE,
        OVERRIDE_AND_IGNORE,
        FIRST_FOUND;

    }

    public static enum MatchStatus {
        FOUND,
        NOT_FOUND,
        ABSTAIN;


        public static MatchStatus getMostSpecific(MatchStatus a, MatchStatus b) {
            return a.ordinal() < b.ordinal() ? a : b;
        }
    }

    @FunctionalInterface
    public static interface DocumentMatcher {
        public MatchStatus matches(Properties var1);
    }

    @FunctionalInterface
    public static interface MatchCallback {
        public void process(Properties var1, Map<String, Object> var2);
    }
}

