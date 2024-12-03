/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableBiMap
 *  com.google.common.collect.ImmutableBiMap$Builder
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.ImmutableImportProcessorSummary;
import com.atlassian.confluence.importexport.ImportProcessorSummary;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DefaultImmutableImportProcessorSummary
implements ImmutableImportProcessorSummary {
    private static final Logger log = LoggerFactory.getLogger(DefaultImmutableImportProcessorSummary.class);
    private final ImmutableBiMap<ImmutableImportProcessorSummary.PersistedKey, ImmutableImportProcessorSummary.PersistedKey> originalToNewKeyMapping;

    DefaultImmutableImportProcessorSummary(ImmutableBiMap<ImmutableImportProcessorSummary.PersistedKey, ImmutableImportProcessorSummary.PersistedKey> originalToNewKeyMapping) {
        this.originalToNewKeyMapping = originalToNewKeyMapping;
    }

    @Override
    public Object getNewIdFor(Class clazz, Object originalId) {
        return this.getNewIdFor(new DefaultPersistedKey(clazz, originalId));
    }

    @Override
    public Object getNewIdFor(ImmutableImportProcessorSummary.PersistedKey oldKey) {
        return this.getNewIdFor(DefaultPersistedKey.toDefaultPersistedKey(oldKey));
    }

    private Object getNewIdFor(DefaultPersistedKey oldDefaultPersistedKey) {
        ImmutableImportProcessorSummary.PersistedKey newKey = (ImmutableImportProcessorSummary.PersistedKey)this.originalToNewKeyMapping.get((Object)oldDefaultPersistedKey);
        return newKey == null ? null : newKey.getPersistedId();
    }

    @Override
    public Object getOriginalIdFor(Class clazz, Object newId) {
        return this.getOriginalIdFor(new DefaultPersistedKey(clazz, newId));
    }

    @Override
    public Object getOriginalIdFor(ImmutableImportProcessorSummary.PersistedKey newKey) {
        DefaultPersistedKey newDefaultPersistedKey = DefaultPersistedKey.toDefaultPersistedKey(newKey);
        return this.getOriginalIdFor(newDefaultPersistedKey);
    }

    private Object getOriginalIdFor(DefaultPersistedKey newDefaultPersistedKey) {
        ImmutableImportProcessorSummary.PersistedKey originalKey = (ImmutableImportProcessorSummary.PersistedKey)this.originalToNewKeyMapping.inverse().get((Object)newDefaultPersistedKey);
        return originalKey == null ? null : originalKey.getPersistedId();
    }

    @Override
    public Set<ImmutableImportProcessorSummary.PersistedKey> getOriginalPersistedKeys() {
        return this.originalToNewKeyMapping.keySet();
    }

    @Override
    public Set<ImmutableImportProcessorSummary.PersistedKey> getNewPersistedKeys() {
        return this.originalToNewKeyMapping.inverse().keySet();
    }

    static DefaultImmutableImportProcessorSummary newInstance(ImportProcessorSummary source) {
        log.info("Summarizing the key mappings");
        HashSet<DefaultPersistedKey> oldKeys = new HashSet<DefaultPersistedKey>();
        HashSet<DefaultPersistedKey> newKeys = new HashSet<DefaultPersistedKey>();
        ImmutableBiMap.Builder builder = ImmutableBiMap.builder();
        for (TransientHibernateHandle handle : ((ImportProcessorSummary)Preconditions.checkNotNull((Object)source)).getPersistedUnmappedHandles()) {
            Class clazz = handle.getClazz();
            Serializable oldId = handle.getId();
            DefaultPersistedKey oldPersistedKey = new DefaultPersistedKey(clazz, oldId);
            Object newId = source.getIdMappingFor(handle);
            if (newId != null) {
                DefaultPersistedKey newPersistedKey = new DefaultPersistedKey(clazz, newId);
                if (!oldKeys.contains(oldPersistedKey) && !newKeys.contains(newPersistedKey)) {
                    builder.put((Object)oldPersistedKey, (Object)newPersistedKey);
                    oldKeys.add(oldPersistedKey);
                    newKeys.add(newPersistedKey);
                    continue;
                }
                log.warn("Cannot add old key = {} and new key = {} to the summary. One of them is a duplicate.", (Object)oldPersistedKey, (Object)newPersistedKey);
                continue;
            }
            log.warn("Cannot find the remapped id for " + oldPersistedKey);
        }
        log.info("Finished summarizing the key mappings.");
        return new DefaultImmutableImportProcessorSummary((ImmutableBiMap<ImmutableImportProcessorSummary.PersistedKey, ImmutableImportProcessorSummary.PersistedKey>)builder.build());
    }

    static class DefaultPersistedKey
    implements ImmutableImportProcessorSummary.PersistedKey {
        private final Class persistedClass;
        private final Object persistedId;
        private final LazyReference<Integer> hashCodeRef = new LazyReference<Integer>(){

            protected Integer create() throws Exception {
                return new HashCodeBuilder(37, 17).append((Object)persistedClass).append(persistedId).toHashCode();
            }
        };

        DefaultPersistedKey(Class persistedClass, Object persistedId) {
            this.persistedClass = (Class)Preconditions.checkNotNull((Object)persistedClass);
            this.persistedId = Preconditions.checkNotNull((Object)persistedId);
        }

        @Override
        public Class getPersistedClass() {
            return this.persistedClass;
        }

        @Override
        public Object getPersistedId() {
            return this.persistedId;
        }

        public int hashCode() {
            return (Integer)this.hashCodeRef.get();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            DefaultPersistedKey that = (DefaultPersistedKey)o;
            return new EqualsBuilder().append((Object)this.persistedClass, (Object)that.persistedClass).append(this.persistedId, that.persistedId).isEquals();
        }

        public String toString() {
            return new ToStringBuilder(null, ToStringStyle.SHORT_PREFIX_STYLE).append("class", (Object)this.persistedClass).append("id", this.persistedId).toString();
        }

        static DefaultPersistedKey toDefaultPersistedKey(ImmutableImportProcessorSummary.PersistedKey key) {
            if (Preconditions.checkNotNull((Object)key) instanceof DefaultPersistedKey) {
                return (DefaultPersistedKey)key;
            }
            return new DefaultPersistedKey(key.getPersistedClass(), key.getPersistedId());
        }
    }
}

