/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.SpoolingBlobInputStreamType
 *  com.atlassian.hibernate.BucketClobStringType
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Sets
 *  org.hibernate.HibernateException
 *  org.hibernate.persister.entity.EntityPersister
 *  org.hibernate.type.BinaryType
 *  org.hibernate.type.CustomType
 *  org.hibernate.type.LiteralType
 *  org.hibernate.type.StringRepresentableType
 *  org.hibernate.type.StringType
 *  org.hibernate.type.TextType
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.BodyTypeUserType;
import com.atlassian.confluence.core.persistence.hibernate.CustomClobType;
import com.atlassian.confluence.core.persistence.hibernate.InstantType;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.impl.hibernate.SpoolingBlobInputStreamType;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.importexport.xmlimport.persister.PersisterOperations;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.persistence.dao.hibernate.NamespaceUserType;
import com.atlassian.confluence.security.persistence.dao.hibernate.CryptographicKeyType;
import com.atlassian.confluence.security.persistence.dao.hibernate.KeyTransferBean;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.persistence.dao.hibernate.SpaceTypeUserType;
import com.atlassian.confluence.user.persistence.dao.hibernate.UserKeyUserType;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.hibernate.BucketClobStringType;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Sets;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.BinaryType;
import org.hibernate.type.CustomType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.type.StringType;
import org.hibernate.type.TextType;
import org.hibernate.type.Type;

@Deprecated
public abstract class AbstractObjectPersister
implements ObjectPersister {
    public static final String NAME_BUCKET_CLOB_STRING_TYPE = BucketClobStringType.class.getName();
    public static final String NAME_CUSTOM_CLOB_TYPE = CustomClobType.class.getName();
    public static final String NAME_NAMESPACE_USER_TYPE = NamespaceUserType.class.getName();
    public static final String NAME_CRYPTOGRAPHIC_KEY_TYPE = CryptographicKeyType.class.getName();
    public static final String NAME_SPOOLING_BLOB_INPUT_STREAM_TYPE = SpoolingBlobInputStreamType.class.getName();
    public static final String NAME_USER_KEY_TYPE = UserKeyUserType.class.getName();
    public static final String NAME_INSTANT_TYPE = InstantType.class.getName();
    protected ImportedObject importedObject;
    protected EntityPersister entityPersister;
    protected Set<TransientHibernateHandle> unsatisfiedObjectDependencies = Sets.newHashSetWithExpectedSize((int)0);
    protected PersisterOperations persisterOperations = new PersisterOperations();

    public Object getPrimitivePropertyValue(EntityPersister entityPersister, PrimitiveProperty property) throws HibernateException {
        String propertyName = property.getName();
        String propertyValueStr = property.getValue();
        Type type = this.hibernateTypeFromString(property.getType());
        if (type == null) {
            type = entityPersister.getPropertyType(propertyName);
        }
        if (type instanceof LiteralType && !(type instanceof CustomType)) {
            if (type instanceof StringType || type instanceof TextType) {
                propertyValueStr = GeneralUtil.unescapeCDATA(propertyValueStr);
            }
            return this.persisterOperations.literalTypeFromString(type, propertyValueStr);
        }
        if (type instanceof BinaryType) {
            return ((StringRepresentableType)type).fromStringValue(propertyValueStr);
        }
        String typeName = type.getName();
        if (typeName.equals(NAME_BUCKET_CLOB_STRING_TYPE)) {
            return GeneralUtil.unescapeCDATA(propertyValueStr);
        }
        if (typeName.equals(NAME_CUSTOM_CLOB_TYPE)) {
            return GeneralUtil.unescapeCDATA(propertyValueStr);
        }
        if (typeName.equals(NAME_NAMESPACE_USER_TYPE)) {
            String namespace = GeneralUtil.unescapeCDATA(propertyValueStr);
            return Namespace.getNamespace(namespace);
        }
        if (typeName.equals(SpaceTypeUserType.class.getName())) {
            return SpaceType.getSpaceType(propertyValueStr);
        }
        if (typeName.equals(BodyTypeUserType.class.getName())) {
            return BodyType.fromInt(Integer.parseInt(propertyValueStr));
        }
        if (typeName.equals(NAME_USER_KEY_TYPE)) {
            return new UserKey(GeneralUtil.unescapeCDATA(propertyValueStr));
        }
        if (typeName.equals(NAME_INSTANT_TYPE)) {
            return Instant.ofEpochMilli(Long.parseLong(GeneralUtil.unescapeCDATA(propertyValueStr)));
        }
        if (typeName.equals(NAME_CRYPTOGRAPHIC_KEY_TYPE)) {
            return new KeyTransferBean(KeyTransferBean.CDatafyString(propertyValueStr)).asKey();
        }
        if (typeName.equals(NAME_SPOOLING_BLOB_INPUT_STREAM_TYPE)) {
            if (propertyValueStr.isEmpty()) {
                return null;
            }
            String base64 = GeneralUtil.unescapeCDATA(propertyValueStr);
            byte[] bytes = Base64.getDecoder().decode(base64);
            return new ByteArrayInputStream(bytes);
        }
        throw new UnsupportedOperationException("Unknown type for restoring data: " + typeName);
    }

    private Type hibernateTypeFromString(String type) {
        if (type == null) {
            return null;
        }
        if (StringType.INSTANCE.getName().equals(type)) {
            return StringType.INSTANCE;
        }
        throw new IllegalArgumentException("Unsupported Hibernate primitive type in backup: " + type);
    }

    protected <E> E getReferencePropertyValue(Class<E> clazz, String idString, ImportProcessorContext context) throws Exception {
        EntityPersister persister = context.getPersister(clazz);
        TransientHibernateHandle propertyValueHandle = this.persisterOperations.readId(clazz, idString, persister);
        E propertyValue = null;
        if (context.isObjectAlreadyImported(propertyValueHandle)) {
            propertyValue = clazz.cast(context.lookupObjectByUnfixedHandle(propertyValueHandle));
        }
        if (propertyValue == null) {
            this.unsatisfiedObjectDependencies.add(propertyValueHandle);
        }
        return propertyValue;
    }
}

