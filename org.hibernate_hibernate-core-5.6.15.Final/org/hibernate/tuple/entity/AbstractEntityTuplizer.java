/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributesMetadata;
import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Assigned;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.tuple.IdentifierProperty;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.tuple.entity.VersionProperty;
import org.hibernate.type.AssociationType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public abstract class AbstractEntityTuplizer
implements EntityTuplizer {
    private final EntityMetamodel entityMetamodel;
    private final Getter idGetter;
    private final Setter idSetter;
    protected final Getter[] getters;
    protected final Setter[] setters;
    protected final int propertySpan;
    protected final boolean hasCustomAccessors;
    private final Instantiator instantiator;
    private final ProxyFactory proxyFactory;
    private final CompositeType identifierMapperType;
    private final MappedIdentifierValueMarshaller mappedIdentifierValueMarshaller;

    public Type getIdentifierMapperType() {
        return this.identifierMapperType;
    }

    protected abstract Getter buildPropertyGetter(Property var1, PersistentClass var2);

    protected abstract Setter buildPropertySetter(Property var1, PersistentClass var2);

    protected abstract Instantiator buildInstantiator(EntityMetamodel var1, PersistentClass var2);

    protected abstract ProxyFactory buildProxyFactory(PersistentClass var1, Getter var2, Setter var3);

    public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
        Component mapper;
        this.entityMetamodel = entityMetamodel;
        if (!entityMetamodel.getIdentifierProperty().isVirtual()) {
            this.idGetter = this.buildPropertyGetter(mappingInfo.getIdentifierProperty(), mappingInfo);
            this.idSetter = this.buildPropertySetter(mappingInfo.getIdentifierProperty(), mappingInfo);
        } else {
            this.idGetter = null;
            this.idSetter = null;
        }
        this.propertySpan = entityMetamodel.getPropertySpan();
        this.getters = new Getter[this.propertySpan];
        this.setters = new Setter[this.propertySpan];
        Iterator itr = mappingInfo.getPropertyClosureIterator();
        boolean foundCustomAccessor = false;
        int i = 0;
        while (itr.hasNext()) {
            Property property = (Property)itr.next();
            this.getters[i] = this.buildPropertyGetter(property, mappingInfo);
            this.setters[i] = this.buildPropertySetter(property, mappingInfo);
            if (!property.isBasicPropertyAccessor()) {
                foundCustomAccessor = true;
            }
            ++i;
        }
        this.hasCustomAccessors = foundCustomAccessor;
        this.instantiator = this.buildInstantiator(entityMetamodel, mappingInfo);
        if (entityMetamodel.isLazy()) {
            this.proxyFactory = this.buildProxyFactory(mappingInfo, this.idGetter, this.idSetter);
            if (this.proxyFactory == null) {
                entityMetamodel.setLazy(false);
            }
        } else {
            this.proxyFactory = null;
        }
        if ((mapper = mappingInfo.getIdentifierMapper()) == null) {
            this.identifierMapperType = null;
            this.mappedIdentifierValueMarshaller = null;
        } else {
            this.identifierMapperType = (CompositeType)mapper.getType();
            KeyValue identifier = mappingInfo.getIdentifier();
            this.mappedIdentifierValueMarshaller = AbstractEntityTuplizer.buildMappedIdentifierValueMarshaller(this.getEntityName(), this.getFactory(), (ComponentType)entityMetamodel.getIdentifierProperty().getType(), (ComponentType)this.identifierMapperType, identifier);
        }
    }

    protected String getEntityName() {
        return this.entityMetamodel.getName();
    }

    protected Set getSubclassEntityNames() {
        return this.entityMetamodel.getSubclassEntityNames();
    }

    @Override
    public Serializable getIdentifier(Object entity) throws HibernateException {
        return this.getIdentifier(entity, null);
    }

    @Override
    public Serializable getIdentifier(Object entity, SharedSessionContractImplementor session) {
        Object id;
        if (this.entityMetamodel.getIdentifierProperty().isEmbedded()) {
            id = entity;
        } else if (HibernateProxy.class.isInstance(entity)) {
            id = ((HibernateProxy)entity).getHibernateLazyInitializer().getInternalIdentifier();
        } else if (this.idGetter == null) {
            if (this.identifierMapperType == null) {
                throw new HibernateException("The class has no identifier property: " + this.getEntityName());
            }
            id = this.mappedIdentifierValueMarshaller.getIdentifier(entity, this.getEntityMode(), session);
        } else {
            id = this.idGetter.get(entity);
        }
        try {
            return (Serializable)id;
        }
        catch (ClassCastException cce) {
            StringBuilder msg = new StringBuilder("Identifier classes must be serializable. ");
            if (id != null) {
                msg.append(id.getClass().getName()).append(" is not serializable. ");
            }
            if (cce.getMessage() != null) {
                msg.append(cce.getMessage());
            }
            throw new ClassCastException(msg.toString());
        }
    }

    @Override
    public void setIdentifier(Object entity, Serializable id) throws HibernateException {
        this.setIdentifier(entity, id, null);
    }

    @Override
    public void setIdentifier(Object entity, Serializable id, SharedSessionContractImplementor session) {
        if (this.entityMetamodel.getIdentifierProperty().isEmbedded()) {
            if (entity != id) {
                CompositeType copier = (CompositeType)this.entityMetamodel.getIdentifierProperty().getType();
                copier.setPropertyValues(entity, copier.getPropertyValues((Object)id, this.getEntityMode()), this.getEntityMode());
            }
        } else if (this.idSetter != null) {
            this.idSetter.set(entity, id, this.getFactory());
        } else if (this.identifierMapperType != null) {
            this.mappedIdentifierValueMarshaller.setIdentifier(entity, id, this.getEntityMode(), session);
        }
    }

    private static MappedIdentifierValueMarshaller buildMappedIdentifierValueMarshaller(String entityName, SessionFactoryImplementor sessionFactory, ComponentType mappedIdClassComponentType, ComponentType virtualIdComponent, KeyValue identifier) {
        boolean wereAllEquivalent = true;
        for (int i = 0; i < virtualIdComponent.getSubtypes().length; ++i) {
            if (!virtualIdComponent.getSubtypes()[i].isEntityType() || mappedIdClassComponentType.getSubtypes()[i].isEntityType()) continue;
            wereAllEquivalent = false;
            break;
        }
        return wereAllEquivalent ? new NormalMappedIdentifierValueMarshaller(virtualIdComponent, mappedIdClassComponentType) : new IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(entityName, sessionFactory, virtualIdComponent, mappedIdClassComponentType, identifier);
    }

    private static Serializable determineEntityId(Object entity, AssociationType associationType, SharedSessionContractImplementor session, SessionFactoryImplementor sessionFactory) {
        EntityEntry pcEntry;
        if (entity == null) {
            return null;
        }
        if (HibernateProxy.class.isInstance(entity)) {
            return ((HibernateProxy)entity).getHibernateLazyInitializer().getInternalIdentifier();
        }
        if (session != null && (pcEntry = session.getPersistenceContextInternal().getEntry(entity)) != null) {
            return pcEntry.getId();
        }
        EntityPersister persister = AbstractEntityTuplizer.resolveEntityPersister(entity, associationType, session, sessionFactory);
        return persister.getIdentifier(entity, session);
    }

    private static EntityPersister resolveEntityPersister(Object entity, AssociationType associationType, SharedSessionContractImplementor session, SessionFactoryImplementor sessionFactory) {
        EntityNameResolver entityNameResolver;
        assert (sessionFactory != null);
        if (session != null) {
            return session.getEntityPersister(associationType.getAssociatedEntityName(sessionFactory), entity);
        }
        String entityName = null;
        MetamodelImplementor metamodel = sessionFactory.getMetamodel();
        Iterator<EntityNameResolver> iterator = metamodel.getEntityNameResolvers().iterator();
        while (iterator.hasNext() && (entityName = (entityNameResolver = iterator.next()).resolveEntityName(entity)) == null) {
        }
        if (entityName == null) {
            entityName = entity.getClass().getName();
        }
        return metamodel.entityPersister(entityName);
    }

    @Override
    public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion) {
        this.resetIdentifier(entity, currentId, currentVersion, null);
    }

    @Override
    public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SharedSessionContractImplementor session) {
        IdentifierProperty identifierProperty = this.entityMetamodel.getIdentifierProperty();
        if (!(identifierProperty.getIdentifierGenerator() instanceof Assigned)) {
            Serializable result = identifierProperty.getUnsavedValue().getDefaultValue(currentId);
            this.setIdentifier(entity, result, session);
            VersionProperty versionProperty = this.entityMetamodel.getVersionProperty();
            if (this.entityMetamodel.isVersioned()) {
                this.setPropertyValue(entity, this.entityMetamodel.getVersionPropertyIndex(), versionProperty.getUnsavedValue().getDefaultValue(currentVersion));
            }
        }
    }

    @Override
    public Object getVersion(Object entity) throws HibernateException {
        if (!this.entityMetamodel.isVersioned()) {
            return null;
        }
        return this.getters[this.entityMetamodel.getVersionPropertyIndex()].get(entity);
    }

    protected boolean shouldGetAllProperties(Object entity) {
        BytecodeEnhancementMetadata bytecodeEnhancementMetadata = this.getEntityMetamodel().getBytecodeEnhancementMetadata();
        if (!bytecodeEnhancementMetadata.isEnhancedForLazyLoading()) {
            return true;
        }
        return !bytecodeEnhancementMetadata.hasUnFetchedAttributes(entity);
    }

    @Override
    public Object[] getPropertyValues(Object entity) {
        BytecodeEnhancementMetadata enhancementMetadata = this.entityMetamodel.getBytecodeEnhancementMetadata();
        LazyAttributesMetadata lazyAttributesMetadata = enhancementMetadata.getLazyAttributesMetadata();
        int span = this.entityMetamodel.getPropertySpan();
        String[] propertyNames = this.entityMetamodel.getPropertyNames();
        Object[] result = new Object[span];
        for (int j = 0; j < span; ++j) {
            String propertyName = propertyNames[j];
            result[j] = !lazyAttributesMetadata.isLazyAttribute(propertyName) || enhancementMetadata.isAttributeLoaded(entity, propertyName) ? this.getters[j].get(entity) : LazyPropertyInitializer.UNFETCHED_PROPERTY;
        }
        return result;
    }

    @Override
    public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SharedSessionContractImplementor session) {
        int span = this.entityMetamodel.getPropertySpan();
        Object[] result = new Object[span];
        for (int j = 0; j < span; ++j) {
            result[j] = this.getters[j].getForInsert(entity, mergeMap, session);
        }
        return result;
    }

    @Override
    public Object getPropertyValue(Object entity, int i) throws HibernateException {
        return this.getters[i].get(entity);
    }

    @Override
    public Object getPropertyValue(Object entity, String propertyPath) throws HibernateException {
        int loc = propertyPath.indexOf(46);
        String basePropertyName = loc > 0 ? propertyPath.substring(0, loc) : propertyPath;
        Integer index = this.entityMetamodel.getPropertyIndexOrNull(basePropertyName);
        if (index == null) {
            propertyPath = "_identifierMapper." + propertyPath;
            loc = propertyPath.indexOf(46);
            basePropertyName = loc > 0 ? propertyPath.substring(0, loc) : propertyPath;
        }
        index = this.entityMetamodel.getPropertyIndexOrNull(basePropertyName);
        Object baseValue = this.getPropertyValue(entity, index);
        if (loc > 0) {
            if (baseValue == null) {
                return null;
            }
            return this.getComponentValue((ComponentType)this.entityMetamodel.getPropertyTypes()[index], baseValue, propertyPath.substring(loc + 1));
        }
        return baseValue;
    }

    protected Object getComponentValue(ComponentType type, Object component, String propertyPath) {
        int loc = propertyPath.indexOf(46);
        String basePropertyName = loc > 0 ? propertyPath.substring(0, loc) : propertyPath;
        int index = this.findSubPropertyIndex(type, basePropertyName);
        Object baseValue = type.getPropertyValue(component, index);
        if (loc > 0) {
            if (baseValue == null) {
                return null;
            }
            return this.getComponentValue((ComponentType)type.getSubtypes()[index], baseValue, propertyPath.substring(loc + 1));
        }
        return baseValue;
    }

    private int findSubPropertyIndex(ComponentType type, String subPropertyName) {
        String[] propertyNames = type.getPropertyNames();
        for (int index = 0; index < propertyNames.length; ++index) {
            if (!subPropertyName.equals(propertyNames[index])) continue;
            return index;
        }
        throw new MappingException("component property not found: " + subPropertyName);
    }

    @Override
    public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
        boolean setAll = !this.entityMetamodel.hasLazyProperties();
        SessionFactoryImplementor factory = this.getFactory();
        for (int j = 0; j < this.entityMetamodel.getPropertySpan(); ++j) {
            if (!setAll && values[j] == LazyPropertyInitializer.UNFETCHED_PROPERTY) continue;
            this.setters[j].set(entity, values[j], factory);
        }
    }

    @Override
    public void setPropertyValue(Object entity, int i, Object value) throws HibernateException {
        this.setters[i].set(entity, value, this.getFactory());
    }

    @Override
    public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException {
        this.setters[this.entityMetamodel.getPropertyIndex(propertyName)].set(entity, value, this.getFactory());
    }

    @Override
    public final Object instantiate(Serializable id) throws HibernateException {
        return this.instantiate(id, null);
    }

    @Override
    public final Object instantiate(Serializable id, SharedSessionContractImplementor session) {
        Object result = this.getInstantiator().instantiate(id);
        this.linkToSession(result, session);
        if (id != null) {
            this.setIdentifier(result, id, session);
        }
        return result;
    }

    protected void linkToSession(Object entity, SharedSessionContractImplementor session) {
        if (session == null) {
            return;
        }
        ManagedTypeHelper.processIfPersistentAttributeInterceptable(entity, this::setSession, session);
    }

    private void setSession(PersistentAttributeInterceptable entity, SharedSessionContractImplementor session) {
        BytecodeLazyAttributeInterceptor interceptor = this.getEntityMetamodel().getBytecodeEnhancementMetadata().extractLazyInterceptor(entity);
        if (interceptor != null) {
            interceptor.setSession(session);
        }
    }

    @Override
    public final Object instantiate() throws HibernateException {
        return this.instantiate(null, null);
    }

    @Override
    public void afterInitialize(Object entity, SharedSessionContractImplementor session) {
    }

    @Override
    public final boolean isInstance(Object object) {
        return this.getInstantiator().isInstance(object);
    }

    @Override
    public boolean hasProxy() {
        return this.entityMetamodel.isLazy() && !this.entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();
    }

    @Override
    public final Object createProxy(Serializable id, SharedSessionContractImplementor session) {
        return this.getProxyFactory().getProxy(id, session);
    }

    @Override
    public boolean isLifecycleImplementor() {
        return false;
    }

    protected final EntityMetamodel getEntityMetamodel() {
        return this.entityMetamodel;
    }

    protected final SessionFactoryImplementor getFactory() {
        return this.entityMetamodel.getSessionFactory();
    }

    protected final Instantiator getInstantiator() {
        return this.instantiator;
    }

    @Override
    public final ProxyFactory getProxyFactory() {
        return this.proxyFactory;
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.getEntityMetamodel().getName() + ')';
    }

    @Override
    public Getter getIdentifierGetter() {
        return this.idGetter;
    }

    @Override
    public Getter getVersionGetter() {
        EntityMetamodel entityMetamodel = this.getEntityMetamodel();
        if (entityMetamodel.isVersioned()) {
            return this.getGetter(entityMetamodel.getVersionPropertyIndex());
        }
        return null;
    }

    @Override
    public Getter getGetter(int i) {
        return this.getters[i];
    }

    private static class IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller
    implements MappedIdentifierValueMarshaller {
        private final String entityName;
        private final SessionFactoryImplementor sessionFactory;
        private final ComponentType virtualIdComponent;
        private final ComponentType mappedIdentifierType;
        private final KeyValue identifier;

        private IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(String entityName, SessionFactoryImplementor sessionFactory, ComponentType virtualIdComponent, ComponentType mappedIdentifierType, KeyValue identifier) {
            this.sessionFactory = sessionFactory;
            this.entityName = entityName;
            this.virtualIdComponent = virtualIdComponent;
            this.mappedIdentifierType = mappedIdentifierType;
            this.identifier = identifier;
        }

        @Override
        public Object getIdentifier(Object entity, EntityMode entityMode, SharedSessionContractImplementor session) {
            Object id = this.mappedIdentifierType.instantiate(entityMode);
            Object[] propertyValues = this.virtualIdComponent.getPropertyValues(entity, entityMode);
            Type[] subTypes = this.virtualIdComponent.getSubtypes();
            Type[] copierSubTypes = this.mappedIdentifierType.getSubtypes();
            int length = subTypes.length;
            for (int i = 0; i < length; ++i) {
                Type subType = subTypes[i];
                if (propertyValues[i] == null) {
                    if (subType.isAssociationType()) {
                        throw new HibernateException("No part of a composite identifier may be null");
                    }
                    Property p = ((Component)this.identifier).getProperty(i);
                    SimpleValue v = (SimpleValue)p.getValue();
                    if (v.getIdentifierGenerator() == null) {
                        throw new HibernateException("No part of a composite identifier may be null");
                    }
                }
                if (!subType.isAssociationType() || copierSubTypes[i].isAssociationType()) continue;
                propertyValues[i] = AbstractEntityTuplizer.determineEntityId(propertyValues[i], (AssociationType)subType, session, this.sessionFactory);
            }
            this.mappedIdentifierType.setPropertyValues(id, propertyValues, entityMode);
            return id;
        }

        @Override
        public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SharedSessionContractImplementor session) {
            Object[] extractedValues = this.mappedIdentifierType.getPropertyValues((Object)id, entityMode);
            Object[] injectionValues = new Object[extractedValues.length];
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            MetamodelImplementor metamodel = this.sessionFactory.getMetamodel();
            for (int i = 0; i < this.virtualIdComponent.getSubtypes().length; ++i) {
                Type virtualPropertyType = this.virtualIdComponent.getSubtypes()[i];
                Type idClassPropertyType = this.mappedIdentifierType.getSubtypes()[i];
                if (virtualPropertyType.isEntityType() && !idClassPropertyType.isEntityType()) {
                    if (session == null) {
                        throw new AssertionError((Object)"Deprecated version of getIdentifier (no session) was used but session was required");
                    }
                    String associatedEntityName = ((EntityType)virtualPropertyType).getAssociatedEntityName();
                    EntityKey entityKey = session.generateEntityKey((Serializable)extractedValues[i], metamodel.entityPersister(associatedEntityName));
                    Object association = persistenceContext.getProxy(entityKey);
                    if (association == null && (association = persistenceContext.getEntity(entityKey)) == null) {
                        association = metamodel.entityPersister(this.entityName).getPropertyValue(entity, this.virtualIdComponent.getPropertyNames()[i]);
                    }
                    injectionValues[i] = association;
                    continue;
                }
                injectionValues[i] = extractedValues[i];
            }
            this.virtualIdComponent.setPropertyValues(entity, injectionValues, entityMode);
        }
    }

    private static class NormalMappedIdentifierValueMarshaller
    implements MappedIdentifierValueMarshaller {
        private final ComponentType virtualIdComponent;
        private final ComponentType mappedIdentifierType;

        private NormalMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
            this.virtualIdComponent = virtualIdComponent;
            this.mappedIdentifierType = mappedIdentifierType;
        }

        @Override
        public Object getIdentifier(Object entity, EntityMode entityMode, SharedSessionContractImplementor session) {
            Object id = this.mappedIdentifierType.instantiate(entityMode);
            Object[] propertyValues = this.virtualIdComponent.getPropertyValues(entity, entityMode);
            this.mappedIdentifierType.setPropertyValues(id, propertyValues, entityMode);
            return id;
        }

        @Override
        public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SharedSessionContractImplementor session) {
            this.virtualIdComponent.setPropertyValues(entity, this.mappedIdentifierType.getPropertyValues((Object)id, session), entityMode);
        }
    }

    private static interface MappedIdentifierValueMarshaller {
        public Object getIdentifier(Object var1, EntityMode var2, SharedSessionContractImplementor var3);

        public void setIdentifier(Object var1, Serializable var2, EntityMode var3, SharedSessionContractImplementor var4);
    }
}

