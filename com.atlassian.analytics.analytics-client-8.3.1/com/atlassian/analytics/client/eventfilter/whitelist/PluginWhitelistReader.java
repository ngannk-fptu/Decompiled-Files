/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.JsonParser$Feature
 *  org.codehaus.jackson.map.MappingJsonFactory
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.Resource
 */
package com.atlassian.analytics.client.eventfilter.whitelist;

import com.atlassian.analytics.client.eventfilter.whitelist.FilteredEventAttributes;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class PluginWhitelistReader {
    private static final Logger log = LoggerFactory.getLogger(PluginWhitelistReader.class);
    private static final List<String> HASH_ATTRIBUTE_NAMES = ImmutableList.of((Object)"user", (Object)"username", (Object)"user.name");

    public Map<String, FilteredEventAttributes> read(InputStream inputStream) throws IOException {
        MappingJsonFactory jf = new MappingJsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper mapper = new ObjectMapper((JsonFactory)jf);
        return this.mapFilteredAttributes((Map)mapper.readValue(inputStream, (TypeReference)new TypeReference<Map<String, List<Object>>>(){}));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Map<String, FilteredEventAttributes> read(Resource resource) {
        try (InputStream inputStream = resource.getInputStream();){
            Map<String, FilteredEventAttributes> map = this.read(inputStream);
            return map;
        }
        catch (IOException e) {
            log.error("Failed to read the JSON whitelist with error message: {}", (Object)e.getMessage());
            return null;
        }
    }

    private Map<String, FilteredEventAttributes> mapFilteredAttributes(Map<String, List<Object>> attributes) {
        HashMap<String, FilteredEventAttributes> filteredEventAttributesMap = new HashMap<String, FilteredEventAttributes>();
        for (Map.Entry<String, List<Object>> eventAttributesMap : attributes.entrySet()) {
            String eventName = eventAttributesMap.getKey();
            FilteredEventAttributes filteredEventAttributes = (FilteredEventAttributes)filteredEventAttributesMap.get(eventName);
            if (filteredEventAttributes == null) {
                filteredEventAttributes = new FilteredEventAttributes();
                filteredEventAttributesMap.put(eventName, filteredEventAttributes);
            }
            this.setFilteredAttributes(filteredEventAttributes, eventAttributesMap.getValue());
        }
        return filteredEventAttributesMap;
    }

    private void setFilteredAttributes(FilteredEventAttributes filteredEventAttributes, List<Object> attributeValues) {
        ArrayList<String> hashAttributes = new ArrayList<String>();
        ArrayList<String> dictionaryAttributes = new ArrayList<String>();
        HashMap<String, List<String>> allowedPropertyValues = new HashMap<String, List<String>>();
        for (Object value : attributeValues) {
            if (value instanceof Map) {
                Map allowedValues = (Map)value;
                for (Map.Entry allowedValueList : allowedValues.entrySet()) {
                    String propertyName = (String)allowedValueList.getKey();
                    this.addPropertyToFilterList(propertyName, hashAttributes, dictionaryAttributes);
                    allowedPropertyValues.put(propertyName, (List<String>)allowedValueList.getValue());
                }
                continue;
            }
            if (!(value instanceof String)) continue;
            this.addPropertyToFilterList((String)value, hashAttributes, dictionaryAttributes);
        }
        attributeValues.removeAll(hashAttributes);
        if (!hashAttributes.isEmpty()) {
            filteredEventAttributes.setHashedAttributes(hashAttributes);
        }
        if (!attributeValues.isEmpty()) {
            filteredEventAttributes.setDictionaryFilteredAttributes(dictionaryAttributes);
        }
        if (!allowedPropertyValues.isEmpty()) {
            filteredEventAttributes.setAllowedPropertyValues(allowedPropertyValues);
        }
    }

    private void addPropertyToFilterList(String propertyName, List<String> hashAttributes, List<String> dictionaryAttributes) {
        if (HASH_ATTRIBUTE_NAMES.contains(propertyName)) {
            hashAttributes.add(propertyName);
        } else {
            dictionaryAttributes.add(propertyName);
        }
    }
}

