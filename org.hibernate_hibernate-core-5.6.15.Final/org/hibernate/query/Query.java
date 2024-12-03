/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.LockModeType
 *  javax.persistence.Parameter
 *  javax.persistence.TemporalType
 *  javax.persistence.TypedQuery
 */
package org.hibernate.query;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.query.CommonQueryContract;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.QueryProducer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.BinaryType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.ByteType;
import org.hibernate.type.CharacterType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LocaleType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TextType;
import org.hibernate.type.TimeType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

public interface Query<R>
extends TypedQuery<R>,
org.hibernate.Query<R>,
CommonQueryContract {
    public QueryProducer getProducer();

    public Optional<R> uniqueResultOptional();

    public Stream<R> stream();

    public Query<R> applyGraph(RootGraph var1, GraphSemantic var2);

    default public Query<R> applyFetchGraph(RootGraph graph) {
        return this.applyGraph(graph, GraphSemantic.FETCH);
    }

    default public Query<R> applyLoadGraph(RootGraph graph) {
        return this.applyGraph(graph, GraphSemantic.LOAD);
    }

    @Override
    public Query<R> setParameter(Parameter<Instant> var1, Instant var2, TemporalType var3);

    @Override
    public Query<R> setParameter(Parameter<LocalDateTime> var1, LocalDateTime var2, TemporalType var3);

    @Override
    public Query<R> setParameter(Parameter<ZonedDateTime> var1, ZonedDateTime var2, TemporalType var3);

    @Override
    public Query<R> setParameter(Parameter<OffsetDateTime> var1, OffsetDateTime var2, TemporalType var3);

    @Override
    public Query<R> setParameter(String var1, Instant var2, TemporalType var3);

    @Override
    public Query<R> setParameter(String var1, LocalDateTime var2, TemporalType var3);

    @Override
    public Query<R> setParameter(String var1, ZonedDateTime var2, TemporalType var3);

    @Override
    public Query<R> setParameter(String var1, OffsetDateTime var2, TemporalType var3);

    @Override
    public Query<R> setParameter(int var1, Instant var2, TemporalType var3);

    @Override
    public Query<R> setParameter(int var1, LocalDateTime var2, TemporalType var3);

    @Override
    public Query<R> setParameter(int var1, ZonedDateTime var2, TemporalType var3);

    @Override
    public Query<R> setParameter(int var1, OffsetDateTime var2, TemporalType var3);

    @Override
    public ScrollableResults scroll();

    @Override
    public ScrollableResults scroll(ScrollMode var1);

    @Override
    public List<R> list();

    default public List<R> getResultList() {
        return this.list();
    }

    @Override
    public R uniqueResult();

    default public R getSingleResult() {
        return this.uniqueResult();
    }

    @Override
    public FlushMode getHibernateFlushMode();

    @Override
    public CacheMode getCacheMode();

    @Override
    public String getCacheRegion();

    @Override
    public Integer getFetchSize();

    @Override
    public LockOptions getLockOptions();

    @Override
    public String getComment();

    @Override
    public String getQueryString();

    @Override
    public ParameterMetadata getParameterMetadata();

    @Override
    public Query<R> setMaxResults(int var1);

    @Override
    public Query<R> setFirstResult(int var1);

    @Override
    public Query<R> setHint(String var1, Object var2);

    @Override
    public <T> Query<R> setParameter(Parameter<T> var1, T var2);

    @Override
    public Query<R> setParameter(Parameter<Calendar> var1, Calendar var2, TemporalType var3);

    @Override
    public Query<R> setParameter(Parameter<Date> var1, Date var2, TemporalType var3);

    @Override
    public Query<R> setParameter(String var1, Object var2);

    @Override
    public Query<R> setParameter(String var1, Object var2, Type var3);

    @Override
    public Query<R> setParameter(String var1, Calendar var2, TemporalType var3);

    @Override
    public Query<R> setParameter(String var1, Date var2, TemporalType var3);

    @Override
    public Query<R> setParameter(int var1, Object var2);

    @Override
    public Query<R> setParameter(int var1, Calendar var2, TemporalType var3);

    @Override
    public Query<R> setParameter(int var1, Date var2, TemporalType var3);

    @Override
    public <T> Query<R> setParameter(QueryParameter<T> var1, T var2);

    @Override
    public <P> Query<R> setParameter(int var1, P var2, TemporalType var3);

    @Override
    public <P> Query<R> setParameter(QueryParameter<P> var1, P var2, Type var3);

    @Override
    public Query<R> setParameter(int var1, Object var2, Type var3);

    @Override
    public <P> Query<R> setParameter(QueryParameter<P> var1, P var2, TemporalType var3);

    @Override
    public <P> Query<R> setParameter(String var1, P var2, TemporalType var3);

    @Override
    public Query<R> setFlushMode(FlushModeType var1);

    @Override
    public Query<R> setLockMode(LockModeType var1);

    @Override
    public Query<R> setReadOnly(boolean var1);

    @Override
    public Query<R> setHibernateFlushMode(FlushMode var1);

    @Override
    public Query<R> setCacheMode(CacheMode var1);

    @Override
    public Query<R> setCacheable(boolean var1);

    @Override
    public Query<R> setCacheRegion(String var1);

    @Override
    public Query<R> setTimeout(int var1);

    @Override
    public Query<R> setFetchSize(int var1);

    @Override
    public Query<R> setLockOptions(LockOptions var1);

    @Override
    public Query<R> setLockMode(String var1, LockMode var2);

    @Override
    public Query<R> setComment(String var1);

    @Override
    public Query<R> addQueryHint(String var1);

    @Override
    public <P> Query<R> setParameterList(QueryParameter<P> var1, Collection<P> var2);

    @Override
    public Query<R> setParameterList(String var1, Collection var2);

    @Override
    public Query<R> setParameterList(String var1, Collection var2, Type var3);

    @Override
    public Query<R> setParameterList(String var1, Object[] var2, Type var3);

    @Override
    public Query<R> setParameterList(String var1, Object[] var2);

    @Override
    public Query<R> setProperties(Object var1);

    @Override
    public Query<R> setProperties(Map var1);

    @Override
    @Deprecated
    default public Query<R> setFlushMode(FlushMode flushMode) {
        this.setHibernateFlushMode(flushMode);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setString(int position, String val) {
        this.setParameter(position, (Object)val, (Type)StringType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setCharacter(int position, char val) {
        this.setParameter(position, (Object)Character.valueOf(val), (Type)CharacterType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setBoolean(int position, boolean val) {
        this.setParameter(position, (Object)val, this.determineProperBooleanType(position, val, BooleanType.INSTANCE));
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setByte(int position, byte val) {
        this.setParameter(position, (Object)val, (Type)ByteType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setShort(int position, short val) {
        this.setParameter(position, (Object)val, (Type)ShortType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setInteger(int position, int val) {
        this.setParameter(position, (Object)val, (Type)IntegerType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setLong(int position, long val) {
        this.setParameter(position, (Object)val, (Type)LongType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setFloat(int position, float val) {
        this.setParameter(position, (Object)Float.valueOf(val), (Type)FloatType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setDouble(int position, double val) {
        this.setParameter(position, (Object)val, (Type)DoubleType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setBinary(int position, byte[] val) {
        this.setParameter(position, (Object)val, (Type)BinaryType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setText(int position, String val) {
        this.setParameter(position, (Object)val, (Type)TextType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setSerializable(int position, Serializable val) {
        this.setParameter(position, (Object)val);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setLocale(int position, Locale val) {
        this.setParameter(position, (Object)val, (Type)LocaleType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setBigDecimal(int position, BigDecimal val) {
        this.setParameter(position, (Object)val, (Type)BigDecimalType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setBigInteger(int position, BigInteger val) {
        this.setParameter(position, (Object)val, (Type)BigIntegerType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setDate(int position, Date val) {
        this.setParameter(position, (Object)val, (Type)DateType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setTime(int position, Date val) {
        this.setParameter(position, (Object)val, (Type)TimeType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setTimestamp(int position, Date val) {
        this.setParameter(position, (Object)val, (Type)TimestampType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setCalendar(int position, Calendar val) {
        this.setParameter(position, (Object)val, (Type)TimestampType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setCalendarDate(int position, Calendar val) {
        this.setParameter(position, (Object)val, (Type)DateType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setString(String name, String val) {
        this.setParameter(name, (Object)val, (Type)StringType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setCharacter(String name, char val) {
        this.setParameter(name, (Object)Character.valueOf(val), (Type)CharacterType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setBoolean(String name, boolean val) {
        this.setParameter(name, (Object)val, this.determineProperBooleanType(name, val, BooleanType.INSTANCE));
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setByte(String name, byte val) {
        this.setParameter(name, (Object)val, (Type)ByteType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setShort(String name, short val) {
        this.setParameter(name, (Object)val, (Type)ShortType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setInteger(String name, int val) {
        this.setParameter(name, (Object)val, (Type)IntegerType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setLong(String name, long val) {
        this.setParameter(name, (Object)val, (Type)LongType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setFloat(String name, float val) {
        this.setParameter(name, (Object)Float.valueOf(val), (Type)FloatType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setDouble(String name, double val) {
        this.setParameter(name, (Object)val, (Type)DoubleType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setBinary(String name, byte[] val) {
        this.setParameter(name, (Object)val, (Type)BinaryType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setText(String name, String val) {
        this.setParameter(name, (Object)val, (Type)TextType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setSerializable(String name, Serializable val) {
        this.setParameter(name, (Object)val);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setLocale(String name, Locale val) {
        this.setParameter(name, (Object)val, (Type)TextType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setBigDecimal(String name, BigDecimal val) {
        this.setParameter(name, (Object)val, (Type)BigDecimalType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setBigInteger(String name, BigInteger val) {
        this.setParameter(name, (Object)val, (Type)BigIntegerType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setDate(String name, Date val) {
        this.setParameter(name, (Object)val, (Type)DateType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setTime(String name, Date val) {
        this.setParameter(name, (Object)val, (Type)TimeType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setTimestamp(String name, Date value) {
        this.setParameter(name, (Object)value, (Type)TimestampType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setCalendar(String name, Calendar value) {
        this.setParameter(name, (Object)value, (Type)TimestampType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    default public Query<R> setCalendarDate(String name, Calendar value) {
        this.setParameter(name, (Object)value, (Type)DateType.INSTANCE);
        return this;
    }

    @Override
    @Deprecated
    public Query<R> setEntity(int var1, Object var2);

    @Override
    @Deprecated
    public Query<R> setEntity(String var1, Object var2);

    @Override
    @Deprecated
    public Query<R> setResultTransformer(ResultTransformer var1);

    @Override
    @Deprecated
    default public Query<R> setParameters(Object[] values, Type[] types) {
        if (!1.$assertionsDisabled && values.length != types.length) {
            throw new AssertionError();
        }
        for (int i = 0; i < values.length; ++i) {
            this.setParameter(i, values[i], types[i]);
        }
        return this;
    }

    default public Stream<R> getResultStream() {
        return this.stream();
    }

    static {
        if (1.$assertionsDisabled) {
            // empty if block
        }
    }
}

