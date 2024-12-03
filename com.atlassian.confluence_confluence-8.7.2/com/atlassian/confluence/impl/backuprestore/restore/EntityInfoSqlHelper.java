/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.SpoolingBlobInputStreamType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.hibernate.BucketClobStringType
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.type.BigIntegerType
 *  org.hibernate.type.BooleanType
 *  org.hibernate.type.CharacterType
 *  org.hibernate.type.ComponentType
 *  org.hibernate.type.CustomType
 *  org.hibernate.type.DoubleType
 *  org.hibernate.type.InstantType
 *  org.hibernate.type.IntegerType
 *  org.hibernate.type.LongType
 *  org.hibernate.type.StringType
 *  org.hibernate.type.TextType
 *  org.hibernate.type.TimestampType
 *  org.hibernate.type.TrueFalseType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore;

import com.atlassian.confluence.core.BodyTypeUserType;
import com.atlassian.confluence.core.persistence.hibernate.CustomClobType;
import com.atlassian.confluence.core.persistence.hibernate.InstantType;
import com.atlassian.confluence.impl.backuprestore.helpers.TableAndFieldNameValidator;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.hibernate.SpoolingBlobInputStreamType;
import com.atlassian.confluence.labels.persistence.dao.hibernate.NamespaceUserType;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.persistence.dao.hibernate.CryptographicKeyType;
import com.atlassian.confluence.security.persistence.dao.hibernate.KeyTransferBean;
import com.atlassian.confluence.spaces.persistence.dao.hibernate.SpaceStatusUserType;
import com.atlassian.confluence.spaces.persistence.dao.hibernate.SpaceTypeUserType;
import com.atlassian.confluence.user.persistence.dao.hibernate.UserKeyUserType;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.hibernate.BucketClobStringType;
import com.atlassian.sal.api.user.UserKey;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.CharacterType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.CustomType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TextType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.TrueFalseType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityInfoSqlHelper {
    private static final Logger log = LoggerFactory.getLogger(EntityInfoSqlHelper.class);
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private final TableAndFieldNameValidator tableAndFieldNameValidator = new TableAndFieldNameValidator();
    private final Map<Class<?>, String> insertStatementByEntityClass = new ConcurrentHashMap();

    public String getInsertQuery(ExportableEntityInfo entityInfo) {
        return this.insertStatementByEntityClass.computeIfAbsent(entityInfo.getEntityClass(), aClass -> this.buildSqlInsertStatement(entityInfo));
    }

    private String buildSqlInsertStatement(ExportableEntityInfo entityInfo) {
        String discriminatorColumnName;
        ArrayList<String> columnNames = new ArrayList<String>();
        if (entityInfo.getId() != null) {
            columnNames.addAll(entityInfo.getId().getColumnNames());
        }
        if ((discriminatorColumnName = entityInfo.getDiscriminatorColumnName()) != null) {
            columnNames.add(discriminatorColumnName);
        }
        for (HibernateField field : entityInfo.getPersistableFields()) {
            columnNames.addAll(field.getColumnNames());
        }
        StringBuilder columnNamesBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();
        for (int i = 0; i < columnNames.size(); ++i) {
            if (i > 0) {
                columnNamesBuilder.append(", ");
                valuesBuilder.append(", ");
            }
            columnNamesBuilder.append(TableAndFieldNameValidator.checkNameDoesNotHaveSqlInjections((String)columnNames.get(i)));
            valuesBuilder.append(":");
            valuesBuilder.append(TableAndFieldNameValidator.checkNameDoesNotHaveSqlInjections(EntityInfoSqlHelper.stripQuotesFromName((String)columnNames.get(i))));
        }
        String sql = "INSERT INTO " + TableAndFieldNameValidator.checkNameDoesNotHaveSqlInjections(entityInfo.getTableName()) + " (" + columnNamesBuilder.toString() + ") VALUES (" + valuesBuilder.toString() + " )";
        log.trace("Sql insert script for entity {}: {}", (Object)entityInfo.getEntityClass().getSimpleName(), (Object)sql);
        return sql;
    }

    public Map<String, Object> createValuesForInsert(ImportedObjectV2 importedObject) {
        List<HibernateField> fields = importedObject.getEntityInfo().getPersistableFields();
        HashMap<String, Object> valuesForOneRecord = new HashMap<String, Object>();
        valuesForOneRecord.putAll(this.generateIdValuesForInsert(importedObject));
        String discriminatorColumnName = importedObject.getEntityInfo().getDiscriminatorColumnName();
        if (discriminatorColumnName != null) {
            valuesForOneRecord.put(discriminatorColumnName, importedObject.getEntityInfo().getDiscriminatorValue());
        }
        for (HibernateField field : fields) {
            String propertyName = field.getPropertyName();
            field.getColumnNames().forEach(columnName -> {
                Object dbReadyValue = EntityInfoSqlHelper.getDbReadyValueFromProperty(importedObject.getFieldValue(propertyName));
                if (dbReadyValue instanceof KeyTransferBean) {
                    dbReadyValue = this.getKeyTransferBeanColumnValues((String)columnName, dbReadyValue);
                }
                valuesForOneRecord.put((String)columnName, dbReadyValue);
            });
        }
        return valuesForOneRecord.entrySet().stream().collect(HashMap::new, (m, v) -> m.put(EntityInfoSqlHelper.stripQuotesFromName((String)v.getKey()), v.getValue()), HashMap::putAll);
    }

    private Object getKeyTransferBeanColumnValues(String columnName, Object dbReadyValue) {
        switch (columnName) {
            case "TYPE": {
                return ((KeyTransferBean)dbReadyValue).getKeyType();
            }
            case "ALGORITHM": {
                return ((KeyTransferBean)dbReadyValue).getAlgorithm();
            }
            case "KEYSPEC": {
                return ((KeyTransferBean)dbReadyValue).getEncodedKey();
            }
        }
        return dbReadyValue;
    }

    private Map<String, Object> generateIdValuesForInsert(ImportedObjectV2 importedObject) {
        if (importedObject.getId() == null) {
            return Collections.emptyMap();
        }
        HashMap<String, Object> valuesForId = new HashMap<String, Object>();
        List<String> columnNames = importedObject.getEntityInfo().getId().getColumnNames();
        List<Object> idValues = importedObject.getEntityInfo().getId().getTypes().size() > 1 ? (List<Object>)importedObject.getId() : Collections.singletonList(importedObject.getId());
        if (columnNames.size() != idValues.size()) {
            throw new IllegalStateException(String.format("Unequal number of composite id column names and id values. Id values: %s, Column names: %s", idValues, columnNames));
        }
        for (int i = 0; i < columnNames.size(); ++i) {
            valuesForId.put(columnNames.get(i), EntityInfoSqlHelper.getDbReadyValueFromProperty(idValues.get(i)));
        }
        return valuesForId;
    }

    public Object convertToDbReadyValue(Type type, String value) {
        if (StringUtils.isEmpty((CharSequence)value)) {
            return null;
        }
        if (type instanceof CustomType) {
            return this.convertCustomTypeToDbReadyValue((CustomType)type, value);
        }
        if (type instanceof ComponentType) {
            return this.convertComponentTypeToDbReadyValue((ComponentType)type, value);
        }
        try {
            if (type instanceof IntegerType) {
                return Integer.parseInt(value);
            }
            if (type instanceof LongType) {
                return Long.parseLong(value);
            }
            if (type instanceof BigIntegerType) {
                return Long.parseLong(value);
            }
            if (type instanceof StringType) {
                return GeneralUtil.unescapeCDATA(value);
            }
            if (type instanceof TextType) {
                return GeneralUtil.unescapeCDATA(value);
            }
            if (type instanceof DoubleType) {
                return Double.parseDouble(value);
            }
            if (type instanceof TimestampType) {
                return this.convertToTimestamp(value);
            }
            if (type instanceof org.hibernate.type.InstantType) {
                return this.convertInstantToTimestamp(value);
            }
            if (type instanceof BooleanType) {
                return Boolean.parseBoolean(value);
            }
            if (type instanceof CharacterType) {
                return value;
            }
            if (type instanceof TrueFalseType) {
                return String.valueOf(value.toUpperCase().charAt(0));
            }
            log.warn("Unexpected type came for parsing. Null value will be used instead. Type: {}, value: {}", (Object)type, (Object)value);
            return null;
        }
        catch (IllegalArgumentException | ParseException e) {
            log.warn("Unable to parse value, null value will be used instead. Type: {}, value: {}. Exception: {}", new Object[]{type, value, e.toString()});
            return null;
        }
    }

    public Object convertCustomTypeToDbReadyValue(CustomType customType, String value) {
        if (customType.getUserType().getClass().equals(UserKeyUserType.class)) {
            return GeneralUtil.unescapeCDATA(value);
        }
        if (customType.getUserType().getClass().equals(SpaceTypeUserType.class)) {
            return value;
        }
        if (customType.getUserType().getClass().equals(SpaceStatusUserType.class)) {
            return value;
        }
        if (customType.getUserType().getClass().equals(BucketClobStringType.class)) {
            return GeneralUtil.unescapeCDATA(value);
        }
        if (customType.getUserType().getClass().equals(CustomClobType.class)) {
            return GeneralUtil.unescapeCDATA(value);
        }
        if (customType.getUserType().getClass().equals(NamespaceUserType.class)) {
            return GeneralUtil.unescapeCDATA(value);
        }
        if (customType.getUserType().getClass().equals(BodyTypeUserType.class)) {
            return Integer.parseInt(value);
        }
        if (customType.getUserType().getClass().equals(SpoolingBlobInputStreamType.class)) {
            return GeneralUtil.unescapeCDATA(value).getBytes(StandardCharsets.UTF_8);
        }
        if (customType.getUserType().getClass().equals(CryptographicKeyType.class)) {
            return new KeyTransferBean(KeyTransferBean.CDatafyString(value));
        }
        if (customType.getUserType().getClass().equals(InstantType.class)) {
            return Long.parseLong(value);
        }
        throw new IllegalArgumentException("Unexpected custom type: " + customType + " with user type " + customType.getUserType());
    }

    private Object convertComponentTypeToDbReadyValue(ComponentType type, String value) {
        if (type.getPropertyNames().length > 1) {
            throw new IllegalArgumentException(String.format("Unexpected ComponentType with multiple columns. Type: %s, Value: %s", type, value));
        }
        if (type.getReturnedClass().equals(PasswordCredential.class)) {
            return value;
        }
        log.warn("Unexpected Component property type came for parsing. Null value will be used instead. Type: {}, value: {}", (Object)type, (Object)value);
        return null;
    }

    private Timestamp convertToTimestamp(String dateAsString) throws ParseException {
        Date parsedDate = new SimpleDateFormat(DATE_FORMAT).parse(dateAsString);
        return new Timestamp(parsedDate.getTime());
    }

    private Date convertInstantToTimestamp(String instantAsString) throws DateTimeParseException {
        if (instantAsString.contains("Z")) {
            Instant parsedInstant = Instant.parse(instantAsString);
            return Timestamp.from(parsedInstant);
        }
        String newString = instantAsString.replace(" ", "T");
        newString = newString.concat("Z");
        Instant parsedInstant = Instant.parse(newString);
        return Timestamp.from(parsedInstant);
    }

    public static Object getDbReadyValueFromProperty(Object property) {
        if (property instanceof UserKey) {
            return ((UserKey)property).getStringValue();
        }
        if (property instanceof ContentTypeEnum) {
            return ((ContentTypeEnum)((Object)property)).getRepresentation();
        }
        return property;
    }

    protected static String stripQuotesFromName(String name) {
        return StringUtils.strip((String)name, (String)"\"'");
    }
}

