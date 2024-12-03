/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.reflect.ConstructorUtils
 *  org.hibernate.HibernateException
 *  org.hibernate.persister.entity.EntityPersister
 *  org.hibernate.type.CustomType
 *  org.hibernate.type.DateType
 *  org.hibernate.type.StringRepresentableType
 *  org.hibernate.type.TimestampType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.BackupParserUtil;
import java.io.Serializable;
import java.text.ParseException;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.hibernate.HibernateException;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.CustomType;
import org.hibernate.type.DateType;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class PersisterOperations {
    private static final Logger log = LoggerFactory.getLogger(PersisterOperations.class);

    public Object literalTypeFromString(Type type, String str) throws HibernateException {
        if (str == null || str.length() == 0) {
            return null;
        }
        str = str.trim();
        if (type instanceof TimestampType || type instanceof DateType) {
            try {
                if (str == null || str.length() == 0) {
                    return null;
                }
                if (type instanceof TimestampType) {
                    return BackupParserUtil.parseTimestamp(str);
                }
                return BackupParserUtil.parseDate(str);
            }
            catch (ParseException e) {
                log.warn("Could not parse " + str + " as type " + type);
                return null;
            }
        }
        if (type instanceof CustomType) {
            try {
                return ConstructorUtils.invokeConstructor((Class)type.getReturnedClass(), (Object[])new Object[]{str});
            }
            catch (NoSuchMethodException ex) {
                return ((StringRepresentableType)type).fromStringValue(str);
            }
            catch (Exception ex) {
                throw new HibernateException((Throwable)ex);
            }
        }
        return ((StringRepresentableType)type).fromStringValue(str);
    }

    public TransientHibernateHandle readId(Class clazz, String idString, EntityPersister persister) throws HibernateException {
        Type idType = persister.getIdentifierType();
        Object id = this.literalTypeFromString(idType, idString);
        return TransientHibernateHandle.create(clazz, (Serializable)id);
    }
}

