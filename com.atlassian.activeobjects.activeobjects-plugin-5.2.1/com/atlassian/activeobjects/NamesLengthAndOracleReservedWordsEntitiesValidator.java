/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginException
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects;

import com.atlassian.activeobjects.EntitiesValidator;
import com.atlassian.activeobjects.external.IgnoreReservedKeyword;
import com.atlassian.plugin.PluginException;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.lang.reflect.Method;
import java.util.Set;
import net.java.ao.ActiveObjectsException;
import net.java.ao.Common;
import net.java.ao.Polymorphic;
import net.java.ao.RawEntity;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.Ignore;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.TableNameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NamesLengthAndOracleReservedWordsEntitiesValidator
implements EntitiesValidator {
    static final Set<String> RESERVED_WORDS = ImmutableSet.of((Object)"BLOB", (Object)"CLOB", (Object)"NUMBER", (Object)"ROWID", (Object)"TIMESTAMP", (Object)"VARCHAR2", (Object[])new String[0]);
    static final int MAX_NUMBER_OF_ENTITIES = 200;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Set<Class<? extends RawEntity<?>>> check(Set<Class<? extends RawEntity<?>>> entityClasses, NameConverters nameConverters) {
        if (entityClasses.size() > 200) {
            throw new PluginException("Plugins are allowed no more than 200 entities!");
        }
        for (Class<RawEntity<?>> clazz : entityClasses) {
            this.check(clazz, nameConverters);
        }
        return entityClasses;
    }

    void check(Class<? extends RawEntity<?>> entityClass, NameConverters nameConverters) {
        this.checkTableName(entityClass, nameConverters.getTableNameConverter());
        FieldNameConverter fieldNameConverter = nameConverters.getFieldNameConverter();
        for (Method method : entityClass.getMethods()) {
            this.checkColumnName(method, fieldNameConverter);
            this.checkPolymorphicColumnName(method, fieldNameConverter);
        }
    }

    void checkTableName(Class<? extends RawEntity<?>> entityClass, TableNameConverter tableNameConverter) {
        String tableName = tableNameConverter.getName(entityClass);
        if (this.isReservedWord(tableName)) {
            throw new ActiveObjectsException("Entity class' '" + entityClass.getName() + "' table name is " + tableName + " which is a reserved word!");
        }
    }

    void checkColumnName(Method method, FieldNameConverter fieldNameConverter) {
        String columnName;
        if ((Common.isAccessor(method) || Common.isMutator(method)) && !method.isAnnotationPresent(Ignore.class) && this.isReservedWord(columnName = fieldNameConverter.getName(method))) {
            if (method.isAnnotationPresent(IgnoreReservedKeyword.class)) {
                this.logger.warn("Method " + method + " is annotated with " + IgnoreReservedKeyword.class.getName() + ", it may cause issue on Oracle. You should change this column name to a non-reserved keyword! The list of reserved keywords is the following: " + RESERVED_WORDS);
            } else {
                throw new ActiveObjectsException("Method '" + method + "' column name is " + columnName + " which is a reserved word!");
            }
        }
    }

    private boolean isReservedWord(final String name) {
        return Iterables.any(RESERVED_WORDS, (Predicate)new Predicate<String>(){

            public boolean apply(String reservedWord) {
                return reservedWord.equalsIgnoreCase(name);
            }
        });
    }

    void checkPolymorphicColumnName(Method method, FieldNameConverter fieldNameConverter) {
        String polyTypeName;
        Class<?> attributeTypeFromMethod = Common.getAttributeTypeFromMethod(method);
        if (attributeTypeFromMethod != null && attributeTypeFromMethod.isAnnotationPresent(Polymorphic.class) && this.isReservedWord(polyTypeName = fieldNameConverter.getPolyTypeName(method))) {
            throw new ActiveObjectsException("Method '" + method + "' polymorphic column name is " + polyTypeName + " which is a reserved word!");
        }
    }
}

