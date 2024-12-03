/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.baggage;

import brave.baggage.BaggageField;
import brave.internal.Nullable;
import brave.internal.baggage.BaggageContext;
import brave.internal.baggage.BaggageFields;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ExtraBaggageContext
extends BaggageContext {
    static final ExtraBaggageContext INSTANCE = new ExtraBaggageContext();

    public static BaggageContext get() {
        return INSTANCE;
    }

    public static List<BaggageField> getAllFields(TraceContextOrSamplingFlags extracted) {
        if (extracted.context() != null) {
            return ExtraBaggageContext.getAllFields(extracted.context());
        }
        return ExtraBaggageContext.getAllFields(extracted.extra());
    }

    public static List<BaggageField> getAllFields(TraceContext context) {
        return ExtraBaggageContext.getAllFields(context.extra());
    }

    public static Map<String, String> getAllValues(TraceContextOrSamplingFlags extracted) {
        if (extracted.context() != null) {
            return ExtraBaggageContext.getAllValues(extracted.context());
        }
        return ExtraBaggageContext.getAllValues(extracted.extra());
    }

    public static Map<String, String> getAllValues(TraceContext context) {
        return ExtraBaggageContext.getAllValues(context.extra());
    }

    @Nullable
    public static BaggageField getFieldByName(TraceContextOrSamplingFlags extracted, String name) {
        if (extracted.context() != null) {
            return ExtraBaggageContext.getFieldByName(extracted.context(), name);
        }
        return ExtraBaggageContext.getFieldByName(ExtraBaggageContext.getAllFields(extracted.extra()), name);
    }

    @Nullable
    public static BaggageField getFieldByName(TraceContext context, String name) {
        return ExtraBaggageContext.getFieldByName(ExtraBaggageContext.getAllFields(context.extra()), name);
    }

    @Override
    public String getValue(BaggageField field, TraceContextOrSamplingFlags extracted) {
        if (extracted.context() != null) {
            return this.getValue(field, extracted.context());
        }
        return ExtraBaggageContext.getValue(field, extracted.extra());
    }

    @Override
    public String getValue(BaggageField field, TraceContext context) {
        return ExtraBaggageContext.getValue(field, context.extra());
    }

    @Override
    public boolean updateValue(BaggageField field, TraceContextOrSamplingFlags extracted, @Nullable String value) {
        if (extracted.context() != null) {
            return this.updateValue(field, extracted.context(), value);
        }
        return ExtraBaggageContext.updateValue(field, extracted.extra(), value);
    }

    @Override
    public boolean updateValue(BaggageField field, TraceContext context, String value) {
        return ExtraBaggageContext.updateValue(field, context.extra(), value);
    }

    static List<BaggageField> getAllFields(List<Object> extraList) {
        BaggageFields extra = ExtraBaggageContext.findExtra(BaggageFields.class, extraList);
        if (extra == null) {
            return Collections.emptyList();
        }
        return extra.getAllFields();
    }

    static Map<String, String> getAllValues(List<Object> extraList) {
        BaggageFields extra = ExtraBaggageContext.findExtra(BaggageFields.class, extraList);
        if (extra == null) {
            return Collections.emptyMap();
        }
        return extra.getAllValues();
    }

    @Nullable
    static BaggageField getFieldByName(List<BaggageField> fields, String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if ((name = name.trim()).isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        }
        for (BaggageField field : fields) {
            if (!name.equals(field.name())) continue;
            return field;
        }
        return null;
    }

    @Nullable
    static String getValue(BaggageField field, List<Object> extraList) {
        BaggageFields extra = ExtraBaggageContext.findExtra(BaggageFields.class, extraList);
        if (extra == null) {
            return null;
        }
        return extra.getValue(field);
    }

    static boolean updateValue(BaggageField field, List<Object> extraList, @Nullable String value) {
        BaggageFields extra = ExtraBaggageContext.findExtra(BaggageFields.class, extraList);
        return extra != null && extra.updateValue(field, value);
    }

    public static <T> T findExtra(Class<T> type, List<Object> extra) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        int length = extra.size();
        for (int i = 0; i < length; ++i) {
            Object nextExtra = extra.get(i);
            if (nextExtra.getClass() != type) continue;
            return (T)nextExtra;
        }
        return null;
    }
}

