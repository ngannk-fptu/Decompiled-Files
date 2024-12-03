/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.querydsl.core.types.Path
 *  com.querydsl.core.types.PathMetadataFactory
 *  com.querydsl.core.types.dsl.BooleanPath
 *  com.querydsl.core.types.dsl.DatePath
 *  com.querydsl.core.types.dsl.DateTimePath
 *  com.querydsl.core.types.dsl.EnumPath
 *  com.querydsl.core.types.dsl.NumberPath
 *  com.querydsl.core.types.dsl.StringPath
 *  com.querydsl.core.types.dsl.TimePath
 *  com.querydsl.sql.ColumnMetadata
 *  com.querydsl.sql.PrimaryKey
 *  com.querydsl.sql.RelationalPathBase
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.addonengine.addons.analytics.store.server.querydsl;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.TimePath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPathBase;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EnhancedRelationalPathBase<T>
extends RelationalPathBase<T> {
    private static final Logger log = LoggerFactory.getLogger(EnhancedRelationalPathBase.class);
    private static final String DEFAULT_SCHEMA = "";
    private static final int ENUM_JAVA_TYPE = 12;

    public EnhancedRelationalPathBase(Class<? extends T> type, String logicalTableName) {
        super(type, PathMetadataFactory.forVariable((String)logicalTableName), DEFAULT_SCHEMA, logicalTableName);
    }

    public EnhancedRelationalPathBase(Class<? extends T> type, String logicalTableName, String tableAlias) {
        super(type, PathMetadataFactory.forVariable((String)tableAlias), DEFAULT_SCHEMA, logicalTableName);
    }

    protected BooleanPath createBoolean(String columnName) {
        BooleanPath path = super.createBoolean(columnName);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(16));
        return path;
    }

    protected <A extends Comparable> DatePath<A> createDate(String columnName, Class<? super A> type) {
        DatePath path = super.createDate(columnName, type);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(91));
        return path;
    }

    protected <A extends Comparable> DateTimePath<A> createDateTime(String columnName, Class<? super A> type) {
        DateTimePath path = super.createDateTime(columnName, type);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(93));
        return path;
    }

    protected <A extends Number> NumberPath<A> createNumber(String columnName, Class<? super A> type) {
        NumberPath path = super.createNumber(columnName, type);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(this.mapJavaNumberType(type)));
        return path;
    }

    protected NumberPath<Integer> createInteger(String columnName) {
        NumberPath path = super.createNumber(columnName, Integer.class);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(4));
        return path;
    }

    protected NumberPath<Long> createLong(String columnName) {
        NumberPath path = super.createNumber(columnName, Long.class);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(-5));
        return path;
    }

    protected NumberPath<Double> createDouble(String columnName) {
        NumberPath path = super.createNumber(columnName, Double.class);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(8));
        return path;
    }

    protected NumberPath<BigDecimal> createBigDecimal(String columnName) {
        NumberPath path = super.createNumber(columnName, BigDecimal.class);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(3));
        return path;
    }

    protected NumberPath<Float> createFloat(String columnName) {
        NumberPath path = super.createNumber(columnName, Float.class);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(3));
        return path;
    }

    protected StringPath createString(String columnName) {
        StringPath path = super.createString(columnName);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(12));
        return path;
    }

    protected <A extends Comparable> TimePath<A> createTime(String columnName, Class<? super A> type) {
        TimePath path = super.createTime(columnName, type);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(92));
        return path;
    }

    protected <A extends Enum<A>> EnumPath<A> createEnum(String columnName, Class<A> type) {
        EnumPath path = super.createEnum(columnName, type);
        this.addMetadata((Path)path, ColumnMetadata.named((String)columnName).ofType(12));
        return path;
    }

    protected ColumnWithMetadataBuilder<BooleanPath> createBooleanCol(String columnName) {
        BooleanPath path = super.createBoolean(columnName);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(16));
    }

    protected <A extends Comparable> ColumnWithMetadataBuilder<DatePath<A>> createDateCol(String columnName, Class<? super A> type) {
        DatePath path = super.createDate(columnName, type);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(91));
    }

    protected <A extends Comparable> ColumnWithMetadataBuilder<DateTimePath<A>> createDateTimeCol(String columnName, Class<? super A> type) {
        DateTimePath path = super.createDateTime(columnName, type);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(93));
    }

    protected <A extends Number> ColumnWithMetadataBuilder<NumberPath<A>> createNumberCol(String columnName, Class<? super A> type) {
        NumberPath path = super.createNumber(columnName, type);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(this.mapJavaNumberType(type)));
    }

    protected ColumnWithMetadataBuilder<NumberPath<Integer>> createIntegerCol(String columnName) {
        NumberPath path = super.createNumber(columnName, Integer.class);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(4));
    }

    protected ColumnWithMetadataBuilder<NumberPath<Long>> createLongCol(String columnName) {
        NumberPath path = super.createNumber(columnName, Long.class);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(-5));
    }

    protected ColumnWithMetadataBuilder<NumberPath<Double>> createDoubleCol(String columnName) {
        NumberPath path = super.createNumber(columnName, Double.class);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(8));
    }

    protected ColumnWithMetadataBuilder<NumberPath<BigDecimal>> createBigDecimalCol(String columnName) {
        NumberPath path = super.createNumber(columnName, BigDecimal.class);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(3));
    }

    protected ColumnWithMetadataBuilder<NumberPath<Float>> createFloatCol(String columnName) {
        NumberPath path = super.createNumber(columnName, Float.class);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(3));
    }

    protected ColumnWithMetadataBuilder<StringPath> createStringCol(String columnName) {
        StringPath path = super.createString(columnName);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(12));
    }

    protected <A extends Comparable> ColumnWithMetadataBuilder<TimePath<A>> createTimeCol(String columnName, Class<? super A> type) {
        TimePath path = super.createTime(columnName, type);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(92));
    }

    protected <A extends Enum<A>> ColumnWithMetadataBuilder<EnumPath<A>> createEnumCol(String columnName, Class<A> type) {
        EnumPath path = super.createEnum(columnName, type);
        return new ColumnWithMetadataBuilder(this, (Path)path, ColumnMetadata.named((String)columnName).ofType(12));
    }

    public Path<?>[] getAllNonPrimaryKeyColumns() {
        PrimaryKey primaryKey = this.getPrimaryKey();
        List pkColumns = primaryKey != null && primaryKey.getLocalColumns() != null ? primaryKey.getLocalColumns() : Collections.emptyList();
        ArrayList columns = Lists.newArrayList((Iterable)this.getColumns().stream().filter(input -> {
            for (Path pkColumn : pkColumns) {
                if (!pkColumn.equals(input)) continue;
                return false;
            }
            return true;
        }).collect(Collectors.toList()));
        return Lists.newArrayList((Iterable)columns).toArray(new Path[columns.size()]);
    }

    private int mapJavaNumberType(Class<?> javaType) {
        if (javaType.equals(Integer.class) || javaType.equals(Integer.TYPE)) {
            return 4;
        }
        if (javaType.equals(Long.class) || javaType.equals(Long.TYPE)) {
            return -5;
        }
        if (javaType.equals(Double.class) || javaType.equals(Double.TYPE)) {
            return 8;
        }
        if (javaType.equals(Float.class) || javaType.equals(Float.TYPE)) {
            return 3;
        }
        throw new UnsupportedOperationException("Unable to map number class " + javaType + " to JDBC type");
    }

    public static class ColumnWithMetadataBuilder<P extends Path<?>> {
        private final P path;
        private ColumnMetadata metadata;
        private boolean asPK = false;
        final /* synthetic */ EnhancedRelationalPathBase this$0;

        public ColumnWithMetadataBuilder(P path, ColumnMetadata startingMetadata) {
            this.this$0 = this$0;
            this.path = path;
            this.metadata = startingMetadata;
        }

        public ColumnWithMetadataBuilder<P> asPrimaryKey() {
            this.asPK = true;
            this.metadata = this.metadata.notNull();
            return this;
        }

        public ColumnWithMetadataBuilder<P> notNull() {
            this.metadata = this.metadata.notNull();
            return this;
        }

        public ColumnWithMetadataBuilder<P> ofType(int jdbcType) {
            this.metadata = this.metadata.ofType(jdbcType);
            return this;
        }

        public ColumnWithMetadataBuilder<P> withIndex(int index) {
            this.metadata = this.metadata.withIndex(index);
            return this;
        }

        public ColumnWithMetadataBuilder<P> withSize(int size) {
            this.metadata = this.metadata.withSize(size);
            return this;
        }

        public ColumnWithMetadataBuilder<P> withDigits(int decimalDigits) {
            this.metadata = this.metadata.withDigits(decimalDigits);
            return this;
        }

        public P build() {
            this.this$0.addMetadata(this.path, this.metadata);
            if (this.asPK) {
                PrimaryKey currentPK = this.this$0.getPrimaryKey();
                if (currentPK != null) {
                    throw new IllegalStateException("You have already set a primary key.  I am not sure you know what you are doing");
                }
                this.this$0.createPrimaryKey(new Path[]{this.path});
            }
            return this.path;
        }
    }
}

