/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.java.ao.schema.index;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.java.ao.AnnotationDelegate;
import net.java.ao.Common;
import net.java.ao.DatabaseProvider;
import net.java.ao.Polymorphic;
import net.java.ao.RawEntity;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.Index;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.Indexes;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.SchemaGenerator;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLIndexField;
import net.java.ao.types.TypeManager;
import net.java.ao.util.StreamUtils;

public class IndexParser {
    private final DatabaseProvider databaseProvider;
    private final TableNameConverter tableNameConverter;
    private final FieldNameConverter fieldNameConverter;
    private final IndexNameConverter indexNameConverter;
    private final TypeManager typeManager;
    private Predicate<Class<?>> extendsRawEntity = RawEntity.class::isAssignableFrom;
    private Predicate<Class<?>> isPolymorphic = clazz -> clazz.isAnnotationPresent(Polymorphic.class);
    private Predicate<Class<?>> isRawEntity = RawEntity.class::equals;

    public IndexParser(DatabaseProvider databaseProvider, NameConverters nameConverters, TypeManager typeManager) {
        this.databaseProvider = databaseProvider;
        this.tableNameConverter = nameConverters.getTableNameConverter();
        this.fieldNameConverter = nameConverters.getFieldNameConverter();
        this.indexNameConverter = nameConverters.getIndexNameConverter();
        this.typeManager = typeManager;
    }

    public Set<DDLIndex> parseIndexes(Class<? extends RawEntity<?>> clazz) {
        String tableName = this.tableNameConverter.getName(clazz);
        Stream<DDLIndex> methodIndexes = Arrays.stream(clazz.getMethods()).filter(Common::isMutatorOrAccessor).filter(method -> this.isIndexed((Method)method) || this.attributeExtendsRawEntity((Method)method)).map(this::parseIndexField).map(indexField -> this.createIndex((DDLIndexField)indexField, tableName));
        Stream<DDLIndex> classIndexes = StreamUtils.ofNullable(clazz.getAnnotation(Indexes.class)).map(Indexes::value).flatMap(Stream::of).map(index -> this.parseCompositeIndex((Index)index, tableName, (Class<? extends RawEntity<?>>)clazz));
        Stream superInterfaceIndexes = Arrays.stream(clazz.getInterfaces()).filter(this.extendsRawEntity).filter(this.isRawEntity.negate()).filter(this.isPolymorphic.negate()).map(superInterface -> this.parseIndexes((Class<? extends RawEntity<?>>)superInterface)).flatMap(Collection::stream);
        return Stream.of(methodIndexes, classIndexes, superInterfaceIndexes).flatMap(Function.identity()).filter(this::hasFields).filter(this::hasOnlyValidFields).collect(Collectors.toSet());
    }

    private boolean isIndexed(Method method) {
        AnnotationDelegate annotations = Common.getAnnotationDelegate(this.fieldNameConverter, method);
        Indexed indexed = annotations.getAnnotation(Indexed.class);
        return indexed != null;
    }

    private boolean attributeExtendsRawEntity(Method method) {
        Class<?> type = Common.getAttributeTypeFromMethod(method);
        return type != null && this.extendsRawEntity.test(type);
    }

    @Nullable
    private DDLIndexField parseIndexField(@Nullable Method method) {
        if (method == null) {
            return null;
        }
        Class<?> type = Common.getAttributeTypeFromMethod(method);
        String attributeName = this.fieldNameConverter.getName(method);
        AnnotationDelegate annotations = Common.getAnnotationDelegate(this.fieldNameConverter, method);
        return DDLIndexField.builder().fieldName(attributeName).type(SchemaGenerator.getSQLTypeFromMethod(this.typeManager, type, method, annotations)).build();
    }

    private DDLIndex createIndex(DDLIndexField indexField, String tableName) {
        return this.createIndex(Stream.of(indexField), tableName, this.computeIndexName(tableName, indexField.getFieldName()));
    }

    private DDLIndex createIndex(Stream<DDLIndexField> indexFields, String tableName, String indexName) {
        return DDLIndex.builder().indexName(indexName).table(tableName).fields((DDLIndexField[])indexFields.toArray(DDLIndexField[]::new)).build();
    }

    private DDLIndex parseCompositeIndex(Index index, String tableName, Class<? extends RawEntity<?>> clazz) {
        Map methodsApplicableForIndexing = Stream.of(clazz.getMethods()).filter(Common::isMutatorOrAccessor).collect(Collectors.toMap(Method::getName, Function.identity()));
        Stream<DDLIndexField> indexFields = Stream.of(index.methodNames()).map(methodsApplicableForIndexing::get).map(this::parseIndexField);
        String indexName = this.computeIndexName(tableName, index.name());
        return this.createIndex(indexFields, tableName, indexName);
    }

    private String computeIndexName(String tableName, String indexName) {
        return this.indexNameConverter.getName(this.databaseProvider.shorten(tableName), this.databaseProvider.shorten(indexName));
    }

    private boolean hasFields(DDLIndex index) {
        return index.getFields().length > 0;
    }

    private boolean hasOnlyValidFields(DDLIndex index) {
        return Stream.of(index.getFields()).allMatch(Objects::nonNull);
    }
}

