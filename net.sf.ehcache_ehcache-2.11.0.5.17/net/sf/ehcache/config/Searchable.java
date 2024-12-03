/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.attribute.KeyObjectAttributeExtractor;
import net.sf.ehcache.search.attribute.ValueObjectAttributeExtractor;

public class Searchable {
    public static final boolean KEYS_DEFAULT = true;
    public static final boolean VALUES_DEFAULT = true;
    public static final boolean DYNAMIC_INDEXING_DEFAULT = false;
    private final Map<String, SearchAttribute> searchAttributes = new HashMap<String, SearchAttribute>();
    private boolean frozen;
    private boolean keys;
    private boolean values;
    private boolean allowDynamicIndexing = false;

    public Searchable() {
        this.setKeys(true);
        this.setValues(true);
    }

    public void addSearchAttribute(SearchAttribute searchAttribute) throws InvalidConfigurationException {
        this.checkDynamicChange();
        String attributeName = searchAttribute.getName();
        if (attributeName == null) {
            throw new InvalidConfigurationException("Search attribute has null name");
        }
        this.disallowBuiltins(attributeName);
        if (this.searchAttributes.containsKey(attributeName)) {
            throw new InvalidConfigurationException("Repeated searchAttribute name: " + attributeName);
        }
        this.searchAttributes.put(attributeName, searchAttribute);
    }

    private void disallowBuiltins(String attributeName) {
        if (Query.KEY.getAttributeName().equals(attributeName) || Query.VALUE.getAttributeName().equals(attributeName)) {
            throw new InvalidConfigurationException("\"" + attributeName + "\" is a reserved attribute name");
        }
    }

    private void checkDynamicChange() {
        if (this.frozen) {
            throw new CacheException("Dynamic configuration changes are disabled for this cache");
        }
    }

    public Map<String, SearchAttribute> getSearchAttributes() {
        return Collections.unmodifiableMap(this.searchAttributes);
    }

    public Searchable searchAttribute(SearchAttribute searchAttribute) {
        this.addSearchAttribute(searchAttribute);
        return this;
    }

    public void freezeConfiguration() {
        this.frozen = true;
    }

    public Map<String, SearchAttribute> getUserDefinedSearchAttributes() {
        HashMap<String, SearchAttribute> copy = new HashMap<String, SearchAttribute>(this.searchAttributes);
        copy.remove(Query.KEY.getAttributeName());
        copy.remove(Query.VALUE.getAttributeName());
        return copy;
    }

    public boolean keys() {
        return this.keys;
    }

    public boolean values() {
        return this.values;
    }

    public boolean isDynamicIndexingAllowed() {
        return this.allowDynamicIndexing;
    }

    public void values(boolean b) {
        this.setValues(b);
    }

    public void keys(boolean b) {
        this.setKeys(b);
    }

    public void setKeys(boolean keys) {
        this.checkDynamicChange();
        this.keys = keys;
        if (!keys) {
            this.searchAttributes.remove(Query.KEY.getAttributeName());
        } else {
            String keyAttr = Query.KEY.getAttributeName();
            this.searchAttributes.put(keyAttr, new SearchAttribute().name(keyAttr).className(KeyObjectAttributeExtractor.class.getName()));
        }
    }

    public void setValues(boolean values) {
        this.checkDynamicChange();
        this.values = values;
        if (!values) {
            this.searchAttributes.remove(Query.VALUE.getAttributeName());
        } else {
            String valueAttr = Query.VALUE.getAttributeName();
            this.searchAttributes.put(valueAttr, new SearchAttribute().name(valueAttr).className(ValueObjectAttributeExtractor.class.getName()));
        }
    }

    public void setAllowDynamicIndexing(boolean allow) {
        this.checkDynamicChange();
        this.allowDynamicIndexing = allow;
    }

    public void allowDynamicIndexing(boolean allow) {
        this.setAllowDynamicIndexing(allow);
    }
}

