/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.thoughtworks.xstream.XStream
 *  com.thoughtworks.xstream.converters.Converter
 *  com.thoughtworks.xstream.converters.SingleValueConverter
 *  com.thoughtworks.xstream.converters.basic.BigDecimalConverter
 *  com.thoughtworks.xstream.converters.basic.BigIntegerConverter
 *  com.thoughtworks.xstream.converters.basic.BooleanConverter
 *  com.thoughtworks.xstream.converters.basic.ByteConverter
 *  com.thoughtworks.xstream.converters.basic.CharConverter
 *  com.thoughtworks.xstream.converters.basic.DateConverter
 *  com.thoughtworks.xstream.converters.basic.DoubleConverter
 *  com.thoughtworks.xstream.converters.basic.FloatConverter
 *  com.thoughtworks.xstream.converters.basic.IntConverter
 *  com.thoughtworks.xstream.converters.basic.LongConverter
 *  com.thoughtworks.xstream.converters.basic.NullConverter
 *  com.thoughtworks.xstream.converters.basic.ShortConverter
 *  com.thoughtworks.xstream.converters.basic.StringBufferConverter
 *  com.thoughtworks.xstream.converters.basic.StringConverter
 *  com.thoughtworks.xstream.converters.basic.URLConverter
 *  com.thoughtworks.xstream.converters.collections.ArrayConverter
 *  com.thoughtworks.xstream.converters.collections.BitSetConverter
 *  com.thoughtworks.xstream.converters.collections.CharArrayConverter
 *  com.thoughtworks.xstream.converters.collections.CollectionConverter
 *  com.thoughtworks.xstream.converters.collections.MapConverter
 *  com.thoughtworks.xstream.converters.collections.PropertiesConverter
 *  com.thoughtworks.xstream.converters.collections.TreeMapConverter
 *  com.thoughtworks.xstream.converters.collections.TreeSetConverter
 *  com.thoughtworks.xstream.converters.enums.EnumConverter
 *  com.thoughtworks.xstream.converters.extended.ColorConverter
 *  com.thoughtworks.xstream.converters.extended.CurrencyConverter
 *  com.thoughtworks.xstream.converters.extended.DynamicProxyConverter
 *  com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter
 *  com.thoughtworks.xstream.converters.extended.FileConverter
 *  com.thoughtworks.xstream.converters.extended.FontConverter
 *  com.thoughtworks.xstream.converters.extended.GregorianCalendarConverter
 *  com.thoughtworks.xstream.converters.extended.JavaClassConverter
 *  com.thoughtworks.xstream.converters.extended.JavaMethodConverter
 *  com.thoughtworks.xstream.converters.extended.LocaleConverter
 *  com.thoughtworks.xstream.converters.extended.RegexPatternConverter
 *  com.thoughtworks.xstream.converters.extended.SqlDateConverter
 *  com.thoughtworks.xstream.converters.extended.SqlTimeConverter
 *  com.thoughtworks.xstream.converters.extended.SqlTimestampConverter
 *  com.thoughtworks.xstream.converters.extended.StackTraceElementConverter
 *  com.thoughtworks.xstream.converters.extended.ThrowableConverter
 *  com.thoughtworks.xstream.converters.reflection.ExternalizableConverter
 *  com.thoughtworks.xstream.converters.reflection.ReflectionConverter
 *  com.thoughtworks.xstream.converters.reflection.ReflectionProvider
 *  com.thoughtworks.xstream.converters.reflection.SerializableConverter
 *  com.thoughtworks.xstream.converters.time.InstantConverter
 *  com.thoughtworks.xstream.core.ClassLoaderReference
 *  com.thoughtworks.xstream.io.HierarchicalStreamDriver
 *  com.thoughtworks.xstream.io.xml.XppDriver
 *  com.thoughtworks.xstream.mapper.ArrayMapper
 *  com.thoughtworks.xstream.mapper.CachingMapper
 *  com.thoughtworks.xstream.mapper.ClassAliasingMapper
 *  com.thoughtworks.xstream.mapper.DefaultImplementationsMapper
 *  com.thoughtworks.xstream.mapper.DefaultMapper
 *  com.thoughtworks.xstream.mapper.DynamicProxyMapper
 *  com.thoughtworks.xstream.mapper.ImmutableTypesMapper
 *  com.thoughtworks.xstream.mapper.ImplicitCollectionMapper
 *  com.thoughtworks.xstream.mapper.Mapper
 *  com.thoughtworks.xstream.mapper.Mapper$Null
 *  com.thoughtworks.xstream.mapper.OuterClassMapper
 *  com.thoughtworks.xstream.mapper.SecurityMapper
 *  com.thoughtworks.xstream.mapper.XStream11XmlFriendlyMapper
 *  com.thoughtworks.xstream.mapper.XmlFriendlyMapper
 */
package com.atlassian.confluence.impl.xstream;

import com.atlassian.annotations.Internal;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.basic.BigDecimalConverter;
import com.thoughtworks.xstream.converters.basic.BigIntegerConverter;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.converters.basic.ByteConverter;
import com.thoughtworks.xstream.converters.basic.CharConverter;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.converters.basic.DoubleConverter;
import com.thoughtworks.xstream.converters.basic.FloatConverter;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.LongConverter;
import com.thoughtworks.xstream.converters.basic.NullConverter;
import com.thoughtworks.xstream.converters.basic.ShortConverter;
import com.thoughtworks.xstream.converters.basic.StringBufferConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.basic.URLConverter;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.converters.collections.BitSetConverter;
import com.thoughtworks.xstream.converters.collections.CharArrayConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.collections.PropertiesConverter;
import com.thoughtworks.xstream.converters.collections.TreeMapConverter;
import com.thoughtworks.xstream.converters.collections.TreeSetConverter;
import com.thoughtworks.xstream.converters.enums.EnumConverter;
import com.thoughtworks.xstream.converters.extended.ColorConverter;
import com.thoughtworks.xstream.converters.extended.CurrencyConverter;
import com.thoughtworks.xstream.converters.extended.DynamicProxyConverter;
import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import com.thoughtworks.xstream.converters.extended.FontConverter;
import com.thoughtworks.xstream.converters.extended.GregorianCalendarConverter;
import com.thoughtworks.xstream.converters.extended.JavaClassConverter;
import com.thoughtworks.xstream.converters.extended.JavaMethodConverter;
import com.thoughtworks.xstream.converters.extended.LocaleConverter;
import com.thoughtworks.xstream.converters.extended.RegexPatternConverter;
import com.thoughtworks.xstream.converters.extended.SqlDateConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimeConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;
import com.thoughtworks.xstream.converters.extended.StackTraceElementConverter;
import com.thoughtworks.xstream.converters.extended.ThrowableConverter;
import com.thoughtworks.xstream.converters.reflection.ExternalizableConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SerializableConverter;
import com.thoughtworks.xstream.converters.time.InstantConverter;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.CachingMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import com.thoughtworks.xstream.mapper.ImplicitCollectionMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.OuterClassMapper;
import com.thoughtworks.xstream.mapper.SecurityMapper;
import com.thoughtworks.xstream.mapper.XStream11XmlFriendlyMapper;
import com.thoughtworks.xstream.mapper.XmlFriendlyMapper;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

@Internal
public final class XStream111
extends XStream {
    public XStream111(ReflectionProvider reflectionProvider, ClassLoaderReference classLoaderReference) {
        super(reflectionProvider, (HierarchicalStreamDriver)new XppDriver(), classLoaderReference, XStream111.buildMapper(classLoaderReference, reflectionProvider));
    }

    private static Mapper buildMapper(ClassLoaderReference classLoaderReference, ReflectionProvider reflectionProvider) {
        DefaultMapper mapper = new DefaultMapper(classLoaderReference);
        mapper = new XStream11XmlFriendlyMapper((Mapper)mapper);
        mapper = new XmlFriendlyMapper((Mapper)mapper);
        mapper = new ClassAliasingMapper((Mapper)mapper);
        mapper = new ImplicitCollectionMapper((Mapper)mapper, reflectionProvider);
        mapper = new DynamicProxyMapper((Mapper)mapper);
        mapper = new OuterClassMapper((Mapper)mapper);
        mapper = new ArrayMapper((Mapper)mapper);
        mapper = new DefaultImplementationsMapper((Mapper)mapper);
        mapper = new ImmutableTypesMapper((Mapper)mapper);
        mapper = new SecurityMapper((Mapper)mapper);
        mapper = new CachingMapper((Mapper)mapper);
        return mapper;
    }

    protected boolean useXStream11XmlFriendlyMapper() {
        return true;
    }

    protected void setupAliases() {
        this.alias("null", Mapper.Null.class);
        this.alias("int", Integer.class);
        this.alias("float", Float.class);
        this.alias("double", Double.class);
        this.alias("long", Long.class);
        this.alias("short", Short.class);
        this.alias("char", Character.class);
        this.alias("byte", Byte.class);
        this.alias("boolean", Boolean.class);
        this.alias("number", Number.class);
        this.alias("object", Object.class);
        this.alias("big-int", BigInteger.class);
        this.alias("big-decimal", BigDecimal.class);
        this.alias("string-buffer", StringBuffer.class);
        this.alias("string", String.class);
        this.alias("java-class", Class.class);
        this.alias("method", Method.class);
        this.alias("constructor", Constructor.class);
        this.alias("date", Date.class);
        this.alias("url", URL.class);
        this.alias("bit-set", BitSet.class);
        this.alias("map", Map.class);
        this.alias("entry", Map.Entry.class);
        this.alias("properties", Properties.class);
        this.alias("list", List.class);
        this.alias("set", Set.class);
        this.alias("linked-list", LinkedList.class);
        this.alias("vector", Vector.class);
        this.alias("tree-map", TreeMap.class);
        this.alias("tree-set", TreeSet.class);
        this.alias("hashtable", Hashtable.class);
        this.alias("awt-color", Color.class);
        this.alias("awt-font", Font.class);
        this.alias("sql-timestamp", Timestamp.class);
        this.alias("sql-time", Time.class);
        this.alias("sql-date", java.sql.Date.class);
        this.alias("file", File.class);
        this.alias("locale", Locale.class);
        this.alias("gregorian-calendar", Calendar.class);
        this.alias("linked-hash-map", LinkedHashMap.class);
        this.alias("linked-hash-set", LinkedHashSet.class);
        this.alias("trace", StackTraceElement.class);
        this.alias("currency", Currency.class);
    }

    protected void setupConverters() {
        ReflectionConverter reflectionConverter = new ReflectionConverter(this.getMapper(), this.getReflectionProvider());
        this.registerConverter((Converter)reflectionConverter, -20);
        this.registerConverter((Converter)new SerializableConverter(this.getMapper(), this.getReflectionProvider(), this.getClassLoaderReference()), -10);
        this.registerConverter((Converter)new ExternalizableConverter(this.getMapper(), this.getClassLoaderReference()), -10);
        this.registerConverter((SingleValueConverter)new IntConverter(), 0);
        this.registerConverter((SingleValueConverter)new FloatConverter(), 0);
        this.registerConverter((SingleValueConverter)new DoubleConverter(), 0);
        this.registerConverter((SingleValueConverter)new LongConverter(), 0);
        this.registerConverter((SingleValueConverter)new ShortConverter(), 0);
        this.registerConverter((Converter)new CharConverter(), 0);
        this.registerConverter((SingleValueConverter)new BooleanConverter(), 0);
        this.registerConverter((SingleValueConverter)new ByteConverter(), 0);
        this.registerConverter((SingleValueConverter)new StringConverter(), 0);
        this.registerConverter((SingleValueConverter)new StringBufferConverter(), 0);
        this.registerConverter((SingleValueConverter)new DateConverter(null), 0);
        this.registerConverter((Converter)new BitSetConverter(), 0);
        this.registerConverter((SingleValueConverter)new URLConverter(), 0);
        this.registerConverter((SingleValueConverter)new BigIntegerConverter(), 0);
        this.registerConverter((SingleValueConverter)new BigDecimalConverter(), 0);
        this.registerConverter((Converter)new ArrayConverter(this.getMapper()), 0);
        this.registerConverter((Converter)new CharArrayConverter(), 0);
        this.registerConverter((Converter)new CollectionConverter(this.getMapper()), 0);
        this.registerConverter((Converter)new MapConverter(this.getMapper()), 0);
        this.registerConverter((Converter)new TreeMapConverter(this.getMapper()), 0);
        this.registerConverter((Converter)new TreeSetConverter(this.getMapper()), 0);
        this.registerConverter((Converter)new PropertiesConverter(), 0);
        this.registerConverter((Converter)new EncodedByteArrayConverter(), 0);
        this.registerConverter((SingleValueConverter)new FileConverter(), 0);
        this.registerConverter((Converter)new NullConverter(), 10000);
        this.registerConverter((SingleValueConverter)new SqlTimestampConverter(), 0);
        this.registerConverter((SingleValueConverter)new SqlTimeConverter(), 0);
        this.registerConverter((SingleValueConverter)new SqlDateConverter(), 0);
        this.registerConverter((Converter)new DynamicProxyConverter(this.getMapper(), this.getClassLoaderReference()), 0);
        this.registerConverter((SingleValueConverter)new JavaClassConverter(this.getClassLoaderReference()), 0);
        this.registerConverter((Converter)new JavaMethodConverter(this.getClassLoaderReference()), 0);
        this.registerConverter((Converter)new FontConverter(this.getMapper()), 0);
        this.registerConverter((Converter)new ColorConverter(), 0);
        this.registerConverter((SingleValueConverter)new LocaleConverter(), 0);
        this.registerConverter((Converter)new GregorianCalendarConverter(), 0);
        this.registerConverter((Converter)new ThrowableConverter(this.getConverterLookup()), 0);
        this.registerConverter((SingleValueConverter)new StackTraceElementConverter(), 0);
        this.registerConverter((SingleValueConverter)new CurrencyConverter(), 0);
        this.registerConverter((Converter)new RegexPatternConverter(), 0);
        this.registerConverter((SingleValueConverter)new InstantConverter(), 0);
        this.registerConverter((Converter)new EnumConverter(), 0);
    }
}

