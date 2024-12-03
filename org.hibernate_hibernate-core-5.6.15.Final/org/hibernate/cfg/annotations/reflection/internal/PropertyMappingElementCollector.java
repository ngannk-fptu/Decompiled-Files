/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.annotations.reflection.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.hibernate.boot.jaxb.mapping.spi.AttributesContainer;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributes;
import org.hibernate.boot.jaxb.mapping.spi.JaxbBasic;
import org.hibernate.boot.jaxb.mapping.spi.JaxbElementCollection;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbedded;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbeddedId;
import org.hibernate.boot.jaxb.mapping.spi.JaxbId;
import org.hibernate.boot.jaxb.mapping.spi.JaxbManyToMany;
import org.hibernate.boot.jaxb.mapping.spi.JaxbManyToOne;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOneToMany;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOneToOne;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostLoad;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostPersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostUpdate;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPrePersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreUpdate;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTransient;
import org.hibernate.boot.jaxb.mapping.spi.JaxbVersion;
import org.hibernate.boot.jaxb.mapping.spi.LifecycleCallback;
import org.hibernate.boot.jaxb.mapping.spi.LifecycleCallbackContainer;
import org.hibernate.boot.jaxb.mapping.spi.PersistentAttribute;

public final class PropertyMappingElementCollector {
    public static final Function<PersistentAttribute, String> PERSISTENT_ATTRIBUTE_NAME = PersistentAttribute::getName;
    public static final Function<JaxbTransient, String> JAXB_TRANSIENT_NAME = JaxbTransient::getName;
    static final Function<LifecycleCallback, String> LIFECYCLE_CALLBACK_NAME = LifecycleCallback::getMethodName;
    private final String propertyName;
    private List<JaxbId> id;
    private List<JaxbEmbeddedId> embeddedId;
    private List<JaxbBasic> basic;
    private List<JaxbVersion> version;
    private List<JaxbManyToOne> manyToOne;
    private List<JaxbOneToMany> oneToMany;
    private List<JaxbOneToOne> oneToOne;
    private List<JaxbManyToMany> manyToMany;
    private List<JaxbElementCollection> elementCollection;
    private List<JaxbEmbedded> embedded;
    private List<JaxbTransient> _transient;
    private List<JaxbPrePersist> prePersist;
    private List<JaxbPostPersist> postPersist;
    private List<JaxbPreRemove> preRemove;
    private List<JaxbPostRemove> postRemove;
    private List<JaxbPreUpdate> preUpdate;
    private List<JaxbPostUpdate> postUpdate;
    private List<JaxbPostLoad> postLoad;

    public PropertyMappingElementCollector(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isEmpty() {
        return this.allNullOrEmpty(this.id, this.embeddedId, this.basic, this.version, this.manyToOne, this.oneToMany, this.oneToOne, this.manyToMany, this.elementCollection, this.embedded, this._transient, this.prePersist, this.postPersist, this.preRemove, this.postRemove, this.preUpdate, this.postUpdate, this.postLoad);
    }

    private boolean allNullOrEmpty(List<?> ... lists) {
        for (List<?> list : lists) {
            if (list == null || list.isEmpty()) continue;
            return false;
        }
        return true;
    }

    private <T> List<T> defaultToEmpty(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public void collectPersistentAttributesIfMatching(AttributesContainer container) {
        if (container instanceof JaxbAttributes) {
            JaxbAttributes jaxbAttributes = (JaxbAttributes)container;
            this.id = this.collectIfMatching((List)this.id, (Object)jaxbAttributes.getId(), (Function)PERSISTENT_ATTRIBUTE_NAME);
            this.embeddedId = this.collectIfMatching(this.embeddedId, jaxbAttributes.getEmbeddedId(), PERSISTENT_ATTRIBUTE_NAME);
            this.version = this.collectIfMatching((List)this.version, (Object)jaxbAttributes.getVersion(), (Function)PERSISTENT_ATTRIBUTE_NAME);
        }
        this.basic = this.collectIfMatching((List)this.basic, (Object)container.getBasic(), (Function)PERSISTENT_ATTRIBUTE_NAME);
        this.manyToOne = this.collectIfMatching((List)this.manyToOne, (Object)container.getManyToOne(), (Function)PERSISTENT_ATTRIBUTE_NAME);
        this.oneToMany = this.collectIfMatching((List)this.oneToMany, (Object)container.getOneToMany(), (Function)PERSISTENT_ATTRIBUTE_NAME);
        this.oneToOne = this.collectIfMatching((List)this.oneToOne, (Object)container.getOneToOne(), (Function)PERSISTENT_ATTRIBUTE_NAME);
        this.manyToMany = this.collectIfMatching((List)this.manyToMany, (Object)container.getManyToMany(), (Function)PERSISTENT_ATTRIBUTE_NAME);
        this.elementCollection = this.collectIfMatching((List)this.elementCollection, (Object)container.getElementCollection(), (Function)PERSISTENT_ATTRIBUTE_NAME);
        this.embedded = this.collectIfMatching((List)this.embedded, (Object)container.getEmbedded(), (Function)PERSISTENT_ATTRIBUTE_NAME);
        this._transient = this.collectIfMatching((List)this._transient, (Object)container.getTransient(), (Function)JAXB_TRANSIENT_NAME);
    }

    public void collectLifecycleCallbacksIfMatching(LifecycleCallbackContainer container) {
        this.prePersist = this.collectIfMatching(this.prePersist, container.getPrePersist(), LIFECYCLE_CALLBACK_NAME);
        this.postPersist = this.collectIfMatching(this.postPersist, container.getPostPersist(), LIFECYCLE_CALLBACK_NAME);
        this.preRemove = this.collectIfMatching(this.preRemove, container.getPreRemove(), LIFECYCLE_CALLBACK_NAME);
        this.postRemove = this.collectIfMatching(this.postRemove, container.getPostRemove(), LIFECYCLE_CALLBACK_NAME);
        this.preUpdate = this.collectIfMatching(this.preUpdate, container.getPreUpdate(), LIFECYCLE_CALLBACK_NAME);
        this.postUpdate = this.collectIfMatching(this.postUpdate, container.getPostUpdate(), LIFECYCLE_CALLBACK_NAME);
        this.postLoad = this.collectIfMatching(this.postLoad, container.getPostLoad(), LIFECYCLE_CALLBACK_NAME);
    }

    private <T> List<T> collectIfMatching(List<T> collected, List<T> candidates, Function<? super T, String> nameGetter) {
        List<Object> result = collected;
        for (T candidate : candidates) {
            result = this.collectIfMatching(result, candidate, nameGetter);
        }
        return result;
    }

    private <T> List<T> collectIfMatching(List<T> collected, T candidate, Function<? super T, String> nameGetter) {
        List<T> result = collected;
        if (candidate != null && this.propertyName.equals(nameGetter.apply(candidate))) {
            if (result == null) {
                result = new ArrayList<T>();
            }
            result.add(candidate);
        }
        return result;
    }

    public List<JaxbId> getId() {
        return this.defaultToEmpty(this.id);
    }

    public List<JaxbEmbeddedId> getEmbeddedId() {
        return this.defaultToEmpty(this.embeddedId);
    }

    public List<JaxbBasic> getBasic() {
        return this.defaultToEmpty(this.basic);
    }

    public List<JaxbVersion> getVersion() {
        return this.defaultToEmpty(this.version);
    }

    public List<JaxbManyToOne> getManyToOne() {
        return this.defaultToEmpty(this.manyToOne);
    }

    public List<JaxbOneToMany> getOneToMany() {
        return this.defaultToEmpty(this.oneToMany);
    }

    public List<JaxbOneToOne> getOneToOne() {
        return this.defaultToEmpty(this.oneToOne);
    }

    public List<JaxbManyToMany> getManyToMany() {
        return this.defaultToEmpty(this.manyToMany);
    }

    public List<JaxbElementCollection> getElementCollection() {
        return this.defaultToEmpty(this.elementCollection);
    }

    public List<JaxbEmbedded> getEmbedded() {
        return this.defaultToEmpty(this.embedded);
    }

    public List<JaxbTransient> getTransient() {
        return this.defaultToEmpty(this._transient);
    }

    public List<JaxbPrePersist> getPrePersist() {
        return this.defaultToEmpty(this.prePersist);
    }

    public List<JaxbPostPersist> getPostPersist() {
        return this.defaultToEmpty(this.postPersist);
    }

    public List<JaxbPreRemove> getPreRemove() {
        return this.defaultToEmpty(this.preRemove);
    }

    public List<JaxbPostRemove> getPostRemove() {
        return this.defaultToEmpty(this.postRemove);
    }

    public List<JaxbPreUpdate> getPreUpdate() {
        return this.defaultToEmpty(this.preUpdate);
    }

    public List<JaxbPostUpdate> getPostUpdate() {
        return this.defaultToEmpty(this.postUpdate);
    }

    public List<JaxbPostLoad> getPostLoad() {
        return this.defaultToEmpty(this.postLoad);
    }
}

