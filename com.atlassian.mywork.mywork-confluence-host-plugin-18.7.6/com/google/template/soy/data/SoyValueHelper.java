/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyAbstractCachingValueProvider;
import com.google.template.soy.data.SoyCustomValueConverter;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyEasyDict;
import com.google.template.soy.data.SoyEasyList;
import com.google.template.soy.data.SoyFutureValueProvider;
import com.google.template.soy.data.SoyGlobalsValue;
import com.google.template.soy.data.SoyList;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueConverter;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.DictImpl;
import com.google.template.soy.data.internal.EasyDictImpl;
import com.google.template.soy.data.internal.EasyListImpl;
import com.google.template.soy.data.internal.ListImpl;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.StringData;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Singleton
public final class SoyValueHelper
implements SoyValueConverter {
    public static final SoyValueHelper UNCUSTOMIZED_INSTANCE = new SoyValueHelper();
    public static final SoyDict EMPTY_DICT = DictImpl.EMPTY;
    public static final SoyList EMPTY_LIST = UNCUSTOMIZED_INSTANCE.newEasyList().makeImmutable();
    @Inject(optional=true)
    private List<SoyCustomValueConverter> customValueConverters;

    @Inject
    public SoyValueHelper() {
    }

    public SoyEasyDict newEasyDict() {
        return new EasyDictImpl(this);
    }

    public SoyEasyDict newEasyDict(Object ... alternatingKeysAndValues) {
        Preconditions.checkArgument((alternatingKeysAndValues.length % 2 == 0 ? 1 : 0) != 0);
        EasyDictImpl result = new EasyDictImpl(this);
        int n = alternatingKeysAndValues.length / 2;
        for (int i = 0; i < n; ++i) {
            result.set((String)alternatingKeysAndValues[2 * i], alternatingKeysAndValues[2 * i + 1]);
        }
        return result;
    }

    public SoyEasyDict newEasyDictFromDict(SoyDict dict) {
        EasyDictImpl result = new EasyDictImpl(this);
        result.setItemsFromDict(dict);
        return result;
    }

    public SoyEasyDict newEasyDictFromJavaStringMap(Map<String, ?> javaStringMap) {
        EasyDictImpl result = new EasyDictImpl(this);
        result.setFieldsFromJavaStringMap(javaStringMap);
        return result;
    }

    private SoyDict newDictFromJavaStringMap(Map<String, ?> javaStringMap) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<String, ?> entry : javaStringMap.entrySet()) {
            builder.put((Object)entry.getKey(), (Object)this.convertLazy(entry.getValue()));
        }
        return DictImpl.forProviderMap((Map<String, ? extends SoyValueProvider>)builder.build());
    }

    public SoyEasyList newEasyList() {
        return new EasyListImpl(this);
    }

    public SoyEasyList newEasyList(Object ... values) {
        return this.newEasyListFromJavaIterable(Arrays.asList(values));
    }

    public SoyEasyList newEasyListFromList(SoyList list) {
        EasyListImpl result = new EasyListImpl(this);
        result.addAllFromList(list);
        return result;
    }

    public SoyEasyList newEasyListFromJavaIterable(Iterable<?> javaIterable) {
        EasyListImpl result = new EasyListImpl(this);
        result.addAllFromJavaIterable(javaIterable);
        return result;
    }

    private SoyList newListFromJavaList(List<?> javaList) {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Object item : javaList) {
            builder.add((Object)this.convertLazy(item));
        }
        return ListImpl.forProviderList((List<? extends SoyValueProvider>)builder.build());
    }

    @Override
    @Nonnull
    public SoyValueProvider convert(@Nullable Object obj) {
        SoyValueProvider convertedPrimitive = this.convertPrimitive(obj);
        if (convertedPrimitive != null) {
            return convertedPrimitive;
        }
        if (obj instanceof Map) {
            Map objCast = (Map)obj;
            return this.newDictFromJavaStringMap(objCast);
        }
        if (obj instanceof Collection) {
            if (obj instanceof List) {
                return this.newListFromJavaList((List)obj);
            }
            return this.newEasyListFromJavaIterable((Collection)obj);
        }
        if (obj instanceof Iterable) {
            return this.newEasyListFromJavaIterable((Iterable)obj);
        }
        if (obj instanceof Future) {
            return new SoyFutureValueProvider(this, (Future)obj);
        }
        if (obj instanceof SoyGlobalsValue) {
            return this.convert(((SoyGlobalsValue)obj).getSoyGlobalValue());
        }
        if (this.customValueConverters != null) {
            for (SoyCustomValueConverter customConverter : this.customValueConverters) {
                SoyValueProvider result = customConverter.convert(this, obj);
                if (result == null) continue;
                return result;
            }
        }
        throw new SoyDataException("Attempting to convert unrecognized object to Soy value (object type " + obj.getClass().getName() + ").");
    }

    private SoyValueProvider convertLazy(final @Nullable Object obj) {
        SoyValueProvider convertedPrimitive = this.convertPrimitive(obj);
        if (convertedPrimitive != null) {
            return convertedPrimitive;
        }
        return new SoyAbstractCachingValueProvider(){

            @Override
            protected SoyValue compute() {
                return SoyValueHelper.this.convert(obj).resolve();
            }
        };
    }

    private SoyValueProvider convertPrimitive(@Nullable Object obj) {
        if (obj == null) {
            return NullData.INSTANCE;
        }
        if (obj instanceof SoyValueProvider) {
            return (SoyValueProvider)obj;
        }
        if (obj instanceof String) {
            return StringData.forValue((String)obj);
        }
        if (obj instanceof Boolean) {
            return BooleanData.forValue((Boolean)obj);
        }
        if (obj instanceof Number) {
            if (obj instanceof Integer) {
                return IntegerData.forValue(((Integer)obj).intValue());
            }
            if (obj instanceof Long) {
                return IntegerData.forValue((Long)obj);
            }
            if (obj instanceof Double) {
                return FloatData.forValue((Double)obj);
            }
            if (obj instanceof Float) {
                return FloatData.forValue(((Float)obj).floatValue());
            }
        }
        return null;
    }
}

