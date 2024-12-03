/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  com.atlassian.crowd.model.user.InternalUser
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.Hibernate
 *  org.hibernate.HibernateException
 *  org.hibernate.collection.spi.PersistentCollection
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.metadata.ClassMetadata
 *  org.hibernate.persister.collection.CollectionPersister
 *  org.hibernate.persister.entity.EntityPersister
 *  org.hibernate.proxy.HibernateProxy
 *  org.hibernate.type.CollectionType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.follow.Connection;
import com.atlassian.confluence.importexport.impl.CollectionUpdateOperation;
import com.atlassian.confluence.importexport.impl.ObjectUpdateOperation;
import com.atlassian.confluence.importexport.impl.StorageFormatUserRewriter;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.Operation;
import com.atlassian.confluence.importexport.xmlimport.model.CollectionProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ComponentProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ContentTypeEnumProperty;
import com.atlassian.confluence.importexport.xmlimport.model.EnumProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ReferenceProperty;
import com.atlassian.confluence.importexport.xmlimport.persister.AbstractObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.persister.XHtmlBodyContentPropertyUserRewriter;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.like.LikeEntity;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.persistence.dao.hibernate.UserLoginInfo;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.model.application.ApplicationImpl;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ReflectiveObjectPersister
extends AbstractObjectPersister
implements ObjectPersister {
    public static final Logger log = LoggerFactory.getLogger(ReflectiveObjectPersister.class);
    private final LabelManager labelManager;
    private final ConfluenceUserDao confluenceUserDao;
    private final StorageFormatUserRewriter storageFormatUserRewriter;
    private ImportedObject importedObject;
    private Class classToPersist;
    private EntityPersister entityPersister;
    private Object objectToPersist;
    private Map<TransientHibernateHandle, Set<Operation>> deferredOperations = new HashMap<TransientHibernateHandle, Set<Operation>>();
    private final Map<String, Map<Class<?>, String>> usernameToClassUserFieldMapping = ImmutableMap.builder().put((Object)"creatorName", (Object)ImmutableMap.of(ConfluenceEntityObject.class, (Object)"creator")).put((Object)"followee", (Object)ImmutableMap.of(Connection.class, (Object)"followee")).put((Object)"follower", (Object)ImmutableMap.of(Connection.class, (Object)"follower")).put((Object)"lastModifierName", (Object)ImmutableMap.of(ConfluenceEntityObject.class, (Object)"lastModifier")).put((Object)"owner", (Object)ImmutableMap.of(Label.class, (Object)"owningUser")).put((Object)"user", (Object)ImmutableMap.of(Labelling.class, (Object)"owningUser")).put((Object)"username", (Object)ImmutableMap.of(PersonalInformation.class, (Object)"user", LikeEntity.class, (Object)"user")).put((Object)"userName", (Object)ImmutableMap.of(ContentPermission.class, (Object)"userSubject", Notification.class, (Object)"receiver", SpacePermission.class, (Object)"userSubject", UserLoginInfo.class, (Object)"user")).build();
    private final Map<String, Map<Class<?>, ContentPropertyData>> fieldToContentPropertyMapping = ImmutableMap.builder().put((Object)"contentType", (Object)ImmutableMap.of(Attachment.class, (Object)new ContentPropertyData("MEDIA_TYPE", String.class))).put((Object)"fileSize", (Object)ImmutableMap.of(Attachment.class, (Object)new ContentPropertyData("FILESIZE", Long.class))).put((Object)"minorEdit", (Object)ImmutableMap.of(Attachment.class, (Object)new ContentPropertyData("MINOR_EDIT", Boolean.class))).put((Object)"hidden", (Object)ImmutableMap.of(Attachment.class, (Object)new ContentPropertyData("HIDDEN", Boolean.class))).build();
    private final Map<String, Map<Class<?>, String>> renamedPrimitiveFieldMapping = ImmutableMap.builder().put((Object)"fileName", (Object)ImmutableMap.of(Attachment.class, (Object)"title")).put((Object)"attachmentVersion", (Object)ImmutableMap.of(Attachment.class, (Object)"version")).put((Object)"comment", (Object)ImmutableMap.of(Attachment.class, (Object)"versionComment")).build();
    private final Map<String, Map<Class<?>, String>> renamedReferencePropertyMapping = ImmutableMap.builder().put((Object)"owner", (Object)ImmutableMap.of(Comment.class, (Object)"containerContent")).put((Object)"content", (Object)ImmutableMap.of(Attachment.class, (Object)"containerContent")).put((Object)"attachment", (Object)ImmutableMap.of(Labelling.class, (Object)"content")).build();

    public ReflectiveObjectPersister(LabelManager labelManager, ConfluenceUserDao confluenceUserDao, StorageFormatUserRewriter storageFormatUserRewriter) {
        this.labelManager = labelManager;
        this.confluenceUserDao = confluenceUserDao;
        this.storageFormatUserRewriter = storageFormatUserRewriter;
    }

    @Override
    public List<TransientHibernateHandle> persist(ImportProcessorContext context, ImportedObject importedObject) throws Exception {
        this.importedObject = importedObject;
        this.classToPersist = this.getClassForElement(importedObject.getClassName(), importedObject.getPackageName());
        this.entityPersister = context.getPersister(this.classToPersist);
        this.initialiseObjectToPersist();
        for (ImportedProperty importedProperty : importedObject.getProperties()) {
            this.setProperty(importedProperty, context);
        }
        if (this.unsatisfiedObjectDependencies.isEmpty()) {
            Label existingLabel;
            Attachment attachment;
            Serializable id = this.getCurrentObjectId();
            TransientHibernateHandle unfixedHandle = this.getCurrentObjectHandle();
            if (this.classToPersist.equals(Attachment.class) && StringUtils.isEmpty((CharSequence)(attachment = (Attachment)this.objectToPersist).getFileName())) {
                attachment.setFileName("");
            }
            if (this.classToPersist.equals(Label.class) && (existingLabel = this.labelManager.getLabel((Label)this.objectToPersist)) != null) {
                context.addExplicitIdMapping(unfixedHandle, Long.valueOf(existingLabel.getId()));
                return Collections.singletonList(unfixedHandle);
            }
            if (this.classToPersist.equals(LikeEntity.class) && ((LikeEntity)this.objectToPersist).getContent() == null) {
                log.info("Like entity (id: " + id + ") with null content entity was not imported");
                return Collections.emptyList();
            }
            context.saveObject(id, this.classToPersist, this.objectToPersist);
            context.deferOperations(importedObject.getIdProperty(), this.deferredOperations);
            return Collections.singletonList(unfixedHandle);
        }
        context.addUnsatisfiedObjectDependencies(this.unsatisfiedObjectDependencies, importedObject);
        return Collections.emptyList();
    }

    private TransientHibernateHandle getCurrentObjectHandle() throws HibernateException {
        return TransientHibernateHandle.create(this.classToPersist, this.getCurrentObjectId());
    }

    private Serializable getCurrentObjectId() throws HibernateException {
        Type idType = this.entityPersister.getIdentifierType();
        return (Serializable)this.persisterOperations.literalTypeFromString(idType, this.importedObject.getIdPropertyStr());
    }

    private void initialiseObjectToPersist() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        for (Constructor<?> constructor : this.classToPersist.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length != 0) continue;
            constructor.setAccessible(true);
            this.objectToPersist = constructor.newInstance(new Object[0]);
            return;
        }
        throw new InstantiationException("No no-arg constructor found for " + this.classToPersist);
    }

    private void setProperty(ImportedProperty importedProperty, ImportProcessorContext context) throws Exception {
        Object propertyValue;
        if (importedProperty instanceof PrimitiveId) {
            return;
        }
        if (ContentEntityObject.class.isAssignableFrom(this.classToPersist) && !Attachment.class.isAssignableFrom(this.classToPersist) && "content".equals(importedProperty.getName())) {
            this.migrateToBodyContent(context, (PrimitiveProperty)importedProperty);
        }
        if (Comment.class.equals((Object)this.classToPersist) && "page".equals(importedProperty.getName())) {
            importedProperty = new ReferenceProperty("owner", ((ReferenceProperty)importedProperty).getPackageName(), ((ReferenceProperty)importedProperty).getClassName(), ((ReferenceProperty)importedProperty).getId());
        }
        if ((propertyValue = this.getPropertyValue(importedProperty = this.upgradeProperty(importedProperty, context), null, context)) != null) {
            ((ClassMetadata)this.entityPersister).setPropertyValue(this.objectToPersist, importedProperty.getName(), propertyValue);
        }
        if (propertyValue instanceof Collection && importedProperty instanceof CollectionProperty) {
            this.addContentsToCollection((CollectionProperty)importedProperty, (Collection)propertyValue, context);
        }
        if (propertyValue instanceof Map && importedProperty instanceof CollectionProperty) {
            this.addContentsToMap((CollectionProperty)importedProperty, (Map)propertyValue, context);
        }
    }

    private void addContentsToMap(CollectionProperty collectionProperty, Map map, ImportProcessorContext context) throws Exception {
        for (ImportedProperty property : collectionProperty.getValues()) {
            Object propertyValue = this.getPropertyValue(property = this.upgradeProperty(property, context), collectionProperty, context);
            if (propertyValue == null) continue;
            map.put(property.getName(), propertyValue);
        }
    }

    private void addContentsToCollection(CollectionProperty collectionProperty, Collection collection, ImportProcessorContext context) throws Exception {
        for (ImportedProperty property : collectionProperty.getValues()) {
            Object propertyValue = this.getPropertyValue(property = this.upgradeProperty(property, context), collectionProperty, context);
            if (propertyValue == null) continue;
            collection.add(propertyValue);
        }
    }

    private Object getPropertyValue(ImportedProperty importedProperty, CollectionProperty containingCollection, ImportProcessorContext context) throws Exception {
        Object propertyValue;
        if (importedProperty == null) {
            return null;
        }
        if (containingCollection == null && !ArrayUtils.contains((Object[])this.entityPersister.getPropertyNames(), (Object)importedProperty.getName())) {
            log.debug("Unable to set property '" + importedProperty.getName() + "' on instance of '" + this.objectToPersist.getClass() + "' with id " + this.importedObject.getIdPropertyStr() + ". This is probably due to internal data structure changes and doesn't cause any data loss.");
            return null;
        }
        if (importedProperty instanceof PrimitiveProperty) {
            propertyValue = this.getPrimitivePropertyValue(this.entityPersister, (PrimitiveProperty)importedProperty);
        } else if (importedProperty instanceof ReferenceProperty) {
            propertyValue = containingCollection == null ? this.getReferencePropertyValue((ReferenceProperty)importedProperty, context, this.isPropertyNullable(this.classToPersist, importedProperty.getName(), context)) : this.getCollectionReferencePropertyValue((ReferenceProperty)importedProperty, context, containingCollection.getName());
        } else if (importedProperty instanceof CollectionProperty) {
            propertyValue = this.getCollectionPropertyValue((CollectionProperty)importedProperty, context);
        } else if (importedProperty instanceof EnumProperty) {
            EnumProperty enumProperty = (EnumProperty)importedProperty;
            propertyValue = enumProperty.getEnumValue();
        } else if (importedProperty instanceof ContentTypeEnumProperty) {
            ContentTypeEnumProperty contentTypeEnumProperty = (ContentTypeEnumProperty)importedProperty;
            propertyValue = contentTypeEnumProperty.getEnumValueByRepresentation();
        } else if (importedProperty instanceof ComponentProperty) {
            propertyValue = this.getComponentPropertyValue((ComponentProperty)importedProperty, context);
        } else {
            throw new IllegalArgumentException("Unknown property type in backup: " + importedProperty.getClass() + ": " + importedProperty);
        }
        return propertyValue;
    }

    private Object getComponentPropertyValue(ComponentProperty componentProperty, ImportProcessorContext context) {
        if ((InternalUser.class.equals((Object)this.classToPersist) || ApplicationImpl.class.equals((Object)this.classToPersist)) && "credential".equals(componentProperty.getName())) {
            return PasswordCredential.unencrypted((String)componentProperty.getPropertyStringValue("credential"));
        }
        throw new UnsupportedOperationException("Unknown component type for component " + componentProperty.getName() + " on class " + this.classToPersist.getName());
    }

    private void migrateToBodyContent(ImportProcessorContext context, PrimitiveProperty importedProperty) throws HibernateException, SQLException {
        Object id = context.generateNewIdFor(BodyContent.class, new BodyContent());
        ArrayList<ImportedProperty> props = new ArrayList<ImportedProperty>(3);
        props.add(new PrimitiveProperty("body", importedProperty.getValue(), null));
        props.add(new PrimitiveId("id", id.toString()));
        props.add(new ReferenceProperty("content", this.importedObject.getPackageName(), this.importedObject.getClassName(), this.importedObject.getIdProperty()));
        TransientHibernateHandle currentObjectHandle = this.getCurrentObjectHandle();
        context.addUnsatisfiedObjectDependencies(Collections.singleton(currentObjectHandle), new ImportedObject("BodyContent", BodyContent.class.getPackage().getName(), props, null));
    }

    private ImportedProperty upgradeProperty(ImportedProperty importedProperty, ImportProcessorContext context) throws Exception {
        XHtmlBodyContentPropertyUserRewriter userContentRewriter;
        if (Attachment.class.equals((Object)this.classToPersist) && "page".equals(importedProperty.getName())) {
            return new ReferenceProperty("content", Attachment.class.getPackage().getName(), "Attachment", ((ReferenceProperty)importedProperty).getId());
        }
        if (LikeEntity.class.equals((Object)this.classToPersist) && "contentId".equals(importedProperty.getName())) {
            long unfixedContentId = Long.parseLong(((PrimitiveProperty)importedProperty).getValue());
            ContentEntityObject obj = (ContentEntityObject)context.polyMorphicLookupByUnfixedId(unfixedContentId, Page.class, BlogPost.class, Comment.class);
            if (obj != null) {
                Class clazz = Hibernate.getClass((Object)obj);
                PrimitiveId primitiveId = new PrimitiveId("content", ((PrimitiveProperty)importedProperty).getValue());
                return new ReferenceProperty("content", clazz.getPackage().getName(), clazz.getSimpleName(), primitiveId);
            }
            log.debug("Entity not upgraded : " + unfixedContentId);
            this.unsatisfiedObjectDependencies.add(TransientHibernateHandle.create(ContentEntityObject.class, Long.valueOf(unfixedContentId)));
            return null;
        }
        if (importedProperty instanceof PrimitiveProperty) {
            Map<Class<?>, ContentPropertyData> cpMapping;
            Map<Class<?>, String> primitiveFieldMapping;
            Map<Class<?>, String> classUserFieldMapping = this.usernameToClassUserFieldMapping.get(importedProperty.getName());
            if (classUserFieldMapping != null) {
                for (Map.Entry<Class<?>, String> entry : classUserFieldMapping.entrySet()) {
                    if (!entry.getKey().isAssignableFrom(this.classToPersist)) continue;
                    return this.createUserReferenceProperty((PrimitiveProperty)importedProperty, context, (String)entry.getValue());
                }
            }
            if ((primitiveFieldMapping = this.renamedPrimitiveFieldMapping.get(importedProperty.getName())) != null) {
                for (Map.Entry entry : primitiveFieldMapping.entrySet()) {
                    if (!((Class)entry.getKey()).isAssignableFrom(this.classToPersist)) continue;
                    return new PrimitiveProperty((String)entry.getValue(), ((PrimitiveProperty)importedProperty).getType(), ((PrimitiveProperty)importedProperty).getValue());
                }
            }
            if ((cpMapping = this.fieldToContentPropertyMapping.get(importedProperty.getName())) != null) {
                for (Map.Entry<Class<?>, ContentPropertyData> entry : cpMapping.entrySet()) {
                    if (!entry.getKey().isAssignableFrom(this.classToPersist)) continue;
                    return this.contentPropertyFromMapping((PrimitiveProperty)importedProperty, entry.getValue());
                }
            }
        } else if (importedProperty instanceof ReferenceProperty) {
            ReferenceProperty importedRefProperty = (ReferenceProperty)importedProperty;
            Map<Class<?>, String> containerFieldMapping = this.renamedReferencePropertyMapping.get(importedProperty.getName());
            if (containerFieldMapping != null) {
                for (Map.Entry<Class<?>, String> entry : containerFieldMapping.entrySet()) {
                    if (!entry.getKey().isAssignableFrom(this.classToPersist)) continue;
                    return new ReferenceProperty(entry.getValue(), importedRefProperty.getPackageName(), importedRefProperty.getClassName(), importedRefProperty.getId());
                }
            }
        }
        if (!context.isPreserveIds() && (userContentRewriter = new XHtmlBodyContentPropertyUserRewriter(context, this.storageFormatUserRewriter, this.importedObject, this.getCurrentObjectHandle())).canHandle(importedProperty)) {
            return userContentRewriter.translateBodyContentXhtmlProperty((PrimitiveProperty)importedProperty);
        }
        return importedProperty;
    }

    private ImportedProperty contentPropertyFromMapping(PrimitiveProperty importedProperty, ContentPropertyData cpData) {
        String propertyValue = importedProperty.getValue();
        ContentProperties properties = ((ContentEntityObject)this.objectToPersist).getProperties();
        if (cpData.clazz == Long.class) {
            properties.setLongProperty(cpData.name, Long.parseLong(propertyValue));
        } else if (cpData.clazz == Boolean.class) {
            properties.setLongProperty(cpData.name, Boolean.parseBoolean(propertyValue) ? 1L : 0L);
        } else if (cpData.clazz == String.class) {
            properties.setStringProperty(cpData.name, propertyValue);
        }
        return null;
    }

    private ImportedProperty createUserReferenceProperty(PrimitiveProperty importedProperty, ImportProcessorContext context, String userFieldName) throws Exception {
        TransientHibernateHandle handle;
        String username = importedProperty.getValue();
        if (StringUtils.isBlank((CharSequence)username)) {
            return null;
        }
        ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
        if (user == null) {
            log.debug("No user of name {} was found. Creating.", (Object)username);
            user = new ConfluenceUserImpl((User)new DefaultUser(username));
            context.saveObject(user);
            handle = TransientHibernateHandle.create(ConfluenceUserImpl.class, (Serializable)user.getKey());
        } else {
            handle = TransientHibernateHandle.create(ConfluenceUserImpl.class, (Serializable)user.getKey());
        }
        context.addExplicitIdMapping(handle, handle.getId());
        context.objectImported(handle);
        return new ReferenceProperty(userFieldName, ConfluenceUserImpl.class.getPackage().getName(), ConfluenceUserImpl.class.getSimpleName(), new PrimitiveId("key", user.getKey().getStringValue()));
    }

    private Object getCollectionPropertyValue(CollectionProperty collectionProperty, ImportProcessorContext context) throws Exception {
        if (this.isDeletedCollection(collectionProperty.getName()) || this.isIgnorableCollection(collectionProperty.getName())) {
            return null;
        }
        AbstractCollection collection = this.entityPersister.getPropertyValue(this.objectToPersist, collectionProperty.getName());
        if (collection == null) {
            CollectionPersister collectionPersister;
            SessionImplementor session = context.getSession();
            Type type = this.entityPersister.getPropertyType(collectionProperty.getName());
            PersistentCollection persistentCollection = ((CollectionType)type).instantiate((SharedSessionContractImplementor)session, collectionPersister = session.getFactory().getCollectionPersister(((CollectionType)type).getRole()), (Serializable)((Object)collectionProperty.getName()));
            collection = persistentCollection instanceof Set ? Sets.newHashSetWithExpectedSize((int)0) : (persistentCollection instanceof List ? Lists.newArrayListWithExpectedSize((int)0) : persistentCollection);
        }
        return collection;
    }

    private boolean isIgnorableCollection(String propertyName) {
        if (Page.class.equals((Object)this.classToPersist) && "children".equals(propertyName)) {
            return true;
        }
        return Page.class.equals((Object)this.classToPersist) && "ancestors".equals(propertyName);
    }

    private boolean isDeletedCollection(String propertyName) {
        if (Space.class.equals((Object)this.classToPersist) && ("pages".equals(propertyName) || "blogPosts".equals(propertyName) || "mail".equals(propertyName))) {
            return true;
        }
        if (Label.class.equals((Object)this.classToPersist) && "labellings".equals(propertyName)) {
            return true;
        }
        if (Page.class.equals((Object)this.classToPersist) && "permissions".equals(propertyName)) {
            return true;
        }
        return Versioned.class.isAssignableFrom(this.classToPersist) && "previousVersions".equals(propertyName);
    }

    private Object getCollectionReferencePropertyValue(ReferenceProperty referenceProperty, ImportProcessorContext context, String collectionName) throws Exception {
        Class propertyClass = this.getClassForElement(referenceProperty.getClassName(), referenceProperty.getPackageName());
        TransientHibernateHandle key = this.persisterOperations.readId(propertyClass, referenceProperty.getId().getValue(), this.entityPersister);
        Object propertyValue = null;
        if (context.isObjectAlreadyImported(key)) {
            propertyValue = context.lookupObjectByUnfixedHandle(key);
        }
        if (propertyValue == null) {
            this.deferAddToCollection(context, this.persisterOperations.readId(this.classToPersist, this.importedObject.getIdPropertyStr(), context.getPersister(this.classToPersist)), collectionName, this.persisterOperations.readId(propertyClass, referenceProperty.getId().getValue(), context.getPersister(this.classToPersist)));
        }
        return propertyValue;
    }

    private Object getReferencePropertyValue(ReferenceProperty referenceProperty, ImportProcessorContext context, boolean nullable) throws Exception {
        Class propertyClass = this.getClassForElement(referenceProperty.getClassName(), referenceProperty.getPackageName());
        TransientHibernateHandle propertyValueHandle = this.persisterOperations.readId(propertyClass, referenceProperty.getId().getValue(), context.getPersister(propertyClass));
        Object propertyValue = null;
        if (context.isObjectAlreadyImported(propertyValueHandle)) {
            propertyValue = context.lookupObjectByUnfixedHandle(propertyValueHandle);
        }
        if (propertyValue == null) {
            if (!nullable) {
                this.unsatisfiedObjectDependencies.add(propertyValueHandle);
            } else {
                this.addDeferSetProperty(context, this.persisterOperations.readId(this.classToPersist, this.importedObject.getIdPropertyStr(), this.entityPersister), referenceProperty.getName(), propertyValueHandle);
            }
        }
        return propertyValue;
    }

    private void addDeferredOperation(TransientHibernateHandle key, Operation operation) {
        if (!this.deferredOperations.containsKey(key)) {
            this.deferredOperations.put(key, new HashSet(0));
        }
        this.deferredOperations.get(key).add(operation);
    }

    private void deferAddToCollection(ImportProcessorContext context, TransientHibernateHandle collectionOwnerKey, String collectionName, TransientHibernateHandle memberKey) {
        this.addDeferredOperation(memberKey, new CollectionUpdateOperation(context, collectionOwnerKey, collectionName, memberKey));
    }

    private void addDeferSetProperty(ImportProcessorContext context, TransientHibernateHandle objectHandle, String propertyName, TransientHibernateHandle propertyValueHandle) {
        this.addDeferredOperation(propertyValueHandle, new ObjectUpdateOperation(context, objectHandle, propertyName, propertyValueHandle));
    }

    private boolean isPropertyNullable(Class<?> classToPersist, String propertyName, ImportProcessorContext context) throws HibernateException {
        ClassMetadata md = context.getClassMetadata(classToPersist);
        boolean[] nullable = md.getPropertyNullability();
        String[] names = md.getPropertyNames();
        for (int i = 0; i < nullable.length; ++i) {
            if (!names[i].equals(propertyName)) continue;
            return nullable[i];
        }
        return true;
    }

    private Class getClassForElement(String className, String packageName) throws ClassNotFoundException {
        if ("com.atlassian.confluence.security.persistence.dao.hibernate".equals(packageName) && "HibernateKey".equals(className)) {
            packageName = "com.atlassian.confluence.security.persistence.dao.hibernate.legacy";
            className = "HibernateKey";
        }
        String fullyQualifiedClassName = packageName + "." + className;
        return Class.forName(fullyQualifiedClassName);
    }

    public static Object unproxyIfRequired(Object object) throws HibernateException {
        if (object instanceof HibernateProxy) {
            Hibernate.initialize((Object)object);
            return ((HibernateProxy)object).getHibernateLazyInitializer().getImplementation();
        }
        return object;
    }

    private static final class ContentPropertyData {
        public final String name;
        public final Class<?> clazz;

        private ContentPropertyData(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }
    }
}

