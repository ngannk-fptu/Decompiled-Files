/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementHelper;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeDescriptor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.persister.spi.PersisterCreationContext;

public class LazyAttributesMetadata
implements Serializable {
    private final String entityName;
    private final Map<String, LazyAttributeDescriptor> lazyAttributeDescriptorMap;
    private final Map<String, Set<String>> fetchGroupToAttributeMap;
    private final Set<String> fetchGroupNames;
    private final Set<String> lazyAttributeNames;

    public static LazyAttributesMetadata from(PersistentClass mappedEntity, boolean isEnhanced, boolean collectionsInDefaultFetchGroupEnabled, PersisterCreationContext creationContext) {
        LinkedHashMap<String, LazyAttributeDescriptor> lazyAttributeDescriptorMap = new LinkedHashMap<String, LazyAttributeDescriptor>();
        HashMap<String, Set> fetchGroupToAttributesMap = new HashMap<String, Set>();
        int i = -1;
        int x = 0;
        Iterator itr = mappedEntity.getPropertyClosureIterator();
        while (itr.hasNext()) {
            ++i;
            Property property = (Property)itr.next();
            boolean bl = !EnhancementHelper.includeInBaseFetchGroup(property, isEnhanced, entityName -> {
                MetadataImplementor metadata = creationContext.getMetadata();
                PersistentClass entityBinding = metadata.getEntityBinding(entityName);
                assert (entityBinding != null);
                return entityBinding.hasSubclasses();
            }, collectionsInDefaultFetchGroupEnabled);
            if (!bl) continue;
            LazyAttributeDescriptor lazyAttributeDescriptor = LazyAttributeDescriptor.from(property, i, x++);
            lazyAttributeDescriptorMap.put(lazyAttributeDescriptor.getName(), lazyAttributeDescriptor);
            Set attributeSet = fetchGroupToAttributesMap.computeIfAbsent(lazyAttributeDescriptor.getFetchGroupName(), k -> new LinkedHashSet());
            attributeSet.add(lazyAttributeDescriptor.getName());
        }
        if (lazyAttributeDescriptorMap.isEmpty()) {
            return new LazyAttributesMetadata(mappedEntity.getEntityName());
        }
        for (Map.Entry entry : fetchGroupToAttributesMap.entrySet()) {
            entry.setValue(Collections.unmodifiableSet((Set)entry.getValue()));
        }
        return new LazyAttributesMetadata(mappedEntity.getEntityName(), Collections.unmodifiableMap(lazyAttributeDescriptorMap), Collections.unmodifiableMap(fetchGroupToAttributesMap));
    }

    public static LazyAttributesMetadata nonEnhanced(String entityName) {
        return new LazyAttributesMetadata(entityName);
    }

    public LazyAttributesMetadata(String entityName) {
        this(entityName, Collections.emptyMap(), Collections.emptyMap());
    }

    public LazyAttributesMetadata(String entityName, Map<String, LazyAttributeDescriptor> lazyAttributeDescriptorMap, Map<String, Set<String>> fetchGroupToAttributeMap) {
        this.entityName = entityName;
        this.lazyAttributeDescriptorMap = lazyAttributeDescriptorMap;
        this.fetchGroupToAttributeMap = fetchGroupToAttributeMap;
        this.fetchGroupNames = Collections.unmodifiableSet(fetchGroupToAttributeMap.keySet());
        this.lazyAttributeNames = Collections.unmodifiableSet(lazyAttributeDescriptorMap.keySet());
    }

    public String getEntityName() {
        return this.entityName;
    }

    public boolean hasLazyAttributes() {
        return !this.lazyAttributeDescriptorMap.isEmpty();
    }

    public int lazyAttributeCount() {
        return this.lazyAttributeDescriptorMap.size();
    }

    public Set<String> getLazyAttributeNames() {
        return this.lazyAttributeNames;
    }

    public Set<String> getFetchGroupNames() {
        return this.fetchGroupNames;
    }

    public boolean isLazyAttribute(String attributeName) {
        return this.lazyAttributeDescriptorMap.containsKey(attributeName);
    }

    public String getFetchGroupName(String attributeName) {
        return this.lazyAttributeDescriptorMap.get(attributeName).getFetchGroupName();
    }

    public Set<String> getAttributesInFetchGroup(String fetchGroupName) {
        return this.fetchGroupToAttributeMap.get(fetchGroupName);
    }

    public List<LazyAttributeDescriptor> getFetchGroupAttributeDescriptors(String groupName) {
        ArrayList<LazyAttributeDescriptor> list = new ArrayList<LazyAttributeDescriptor>();
        for (String attributeName : this.fetchGroupToAttributeMap.get(groupName)) {
            list.add(this.lazyAttributeDescriptorMap.get(attributeName));
        }
        return list;
    }

    @Deprecated
    public Set<String> getAttributesInSameFetchGroup(String attributeName) {
        String fetchGroupName = this.getFetchGroupName(attributeName);
        return this.getAttributesInFetchGroup(fetchGroupName);
    }
}

