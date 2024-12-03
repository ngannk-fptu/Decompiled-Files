/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.writefilter;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.tika.metadata.AccessPermissions;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.writefilter.MetadataWriteFilter;
import org.apache.tika.utils.StringUtils;

public class StandardWriteFilter
implements MetadataWriteFilter,
Serializable {
    public static final Set<String> ALWAYS_SET_FIELDS = new HashSet<String>();
    public static final Set<String> ALWAYS_ADD_FIELDS = new HashSet<String>();
    private static final String METADATA_TRUNCATED_KEY;
    private static final String TIKA_CONTENT_KEY;
    private static final String[] TRUE;
    private final int minimumMaxFieldSizeInAlwaysFields = 300;
    private final boolean includeEmpty;
    private final int maxTotalEstimatedSize;
    private final int maxValuesPerField;
    private final int maxFieldSize;
    private final int maxKeySize;
    private final Set<String> includeFields;
    private Map<String, Integer> fieldSizes = new HashMap<String, Integer>();
    int estimatedSize = 0;

    protected StandardWriteFilter(int maxKeySize, int maxFieldSize, int maxEstimatedSize, int maxValuesPerField, Set<String> includeFields, boolean includeEmpty) {
        this.maxKeySize = maxKeySize;
        this.maxFieldSize = maxFieldSize;
        this.maxTotalEstimatedSize = maxEstimatedSize;
        this.maxValuesPerField = maxValuesPerField;
        this.includeFields = includeFields;
        this.includeEmpty = includeEmpty;
    }

    @Override
    public void filterExisting(Map<String, String[]> data) {
        HashMap<String, String[]> tmp = new HashMap<String, String[]>();
        for (Map.Entry<String, String[]> e : data.entrySet()) {
            String name = e.getKey();
            String[] vals = e.getValue();
            if (!this.includeField(name)) continue;
            for (int i = 0; i < vals.length; ++i) {
                String v = vals[i];
                if (!this.include(name, v)) continue;
                this.add(name, v, tmp);
            }
        }
        data.clear();
        data.putAll(tmp);
    }

    @Override
    public void set(String field, String value, Map<String, String[]> data) {
        if (!this.include(field, value)) {
            return;
        }
        if (ALWAYS_SET_FIELDS.contains(field) || ALWAYS_ADD_FIELDS.contains(field)) {
            this.setAlwaysInclude(field, value, data);
            return;
        }
        StringSizePair filterKey = this.filterKey(field, value, data);
        this.setFilterKey(filterKey, value, data);
    }

    private void setAlwaysInclude(String field, String value, Map<String, String[]> data) {
        String[] vals;
        if (TIKA_CONTENT_KEY.equals(field)) {
            data.put(field, new String[]{value});
            return;
        }
        int sizeToAdd = StandardWriteFilter.estimateSize(value);
        int alwaysMaxFieldLength = Math.max(300, this.maxFieldSize);
        String toSet = value;
        if (sizeToAdd > alwaysMaxFieldLength) {
            toSet = this.truncate(value, alwaysMaxFieldLength, data);
            sizeToAdd = StandardWriteFilter.estimateSize(toSet);
        }
        int totalAdded = data.containsKey(field) ? 0 : StandardWriteFilter.estimateSize(field);
        totalAdded += sizeToAdd;
        if (data.containsKey(field) && (vals = data.get(field)).length > 0) {
            totalAdded -= StandardWriteFilter.estimateSize(vals[0]);
        }
        this.estimatedSize += totalAdded;
        data.put(field, new String[]{toSet});
    }

    private void addAlwaysInclude(String field, String value, Map<String, String[]> data) {
        if (TIKA_CONTENT_KEY.equals(field)) {
            data.put(field, new String[]{value});
            return;
        }
        if (!data.containsKey(field)) {
            this.setAlwaysInclude(field, value, data);
            return;
        }
        int toAddSize = StandardWriteFilter.estimateSize(value);
        int alwaysMaxFieldLength = Math.max(300, this.maxFieldSize);
        String toAddValue = value;
        if (toAddSize > alwaysMaxFieldLength) {
            toAddValue = this.truncate(value, alwaysMaxFieldLength, data);
            toAddSize = StandardWriteFilter.estimateSize(toAddValue);
        }
        int totalAdded = data.containsKey(field) ? 0 : StandardWriteFilter.estimateSize(field);
        this.estimatedSize += (totalAdded += toAddSize);
        data.put(field, this.appendValue(data.get(field), toAddValue));
    }

    private int maxAllowedToSet(StringSizePair filterKey) {
        Integer existingSizeInt = this.fieldSizes.get(filterKey.string);
        int existingSize = existingSizeInt == null ? 0 : existingSizeInt;
        int allowedByMaxTotal = this.maxTotalEstimatedSize - this.estimatedSize;
        allowedByMaxTotal += existingSize;
        return Math.min(this.maxFieldSize, allowedByMaxTotal -= existingSizeInt == null ? filterKey.size : 0);
    }

    @Override
    public void add(String field, String value, Map<String, String[]> data) {
        if (!this.include(field, value)) {
            return;
        }
        if (ALWAYS_SET_FIELDS.contains(field)) {
            this.setAlwaysInclude(field, value, data);
            return;
        }
        if (ALWAYS_ADD_FIELDS.contains(field)) {
            this.addAlwaysInclude(field, value, data);
            return;
        }
        StringSizePair filterKey = this.filterKey(field, value, data);
        if (!data.containsKey(filterKey.string)) {
            this.setFilterKey(filterKey, value, data);
            return;
        }
        String[] vals = data.get(filterKey.string);
        if (vals != null && vals.length >= this.maxValuesPerField) {
            this.setTruncated(data);
            return;
        }
        Integer fieldSizeInteger = this.fieldSizes.get(filterKey.string);
        int fieldSize = fieldSizeInteger == null ? 0 : fieldSizeInteger;
        int maxAllowed = this.maxAllowedToAdd(filterKey);
        if (maxAllowed <= 0) {
            this.setTruncated(data);
            return;
        }
        int valueLength = StandardWriteFilter.estimateSize(value);
        String toAdd = value;
        if (valueLength > maxAllowed && (valueLength = StandardWriteFilter.estimateSize(toAdd = this.truncate(value, maxAllowed, data))) == 0) {
            return;
        }
        int addedOverall = valueLength;
        if (fieldSizeInteger == null) {
            addedOverall += filterKey.size;
        }
        this.estimatedSize += addedOverall;
        this.fieldSizes.put(filterKey.string, valueLength + fieldSize);
        data.put(filterKey.string, this.appendValue(data.get(filterKey.string), toAdd));
    }

    private String[] appendValue(String[] values, String value) {
        if (value == null) {
            return values;
        }
        String[] newValues = new String[values.length + 1];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[newValues.length - 1] = value;
        return newValues;
    }

    private int maxAllowedToAdd(StringSizePair filterKey) {
        Integer existingSizeInt = this.fieldSizes.get(filterKey.string);
        int existingSize = existingSizeInt == null ? 0 : existingSizeInt;
        int allowedByMaxField = this.maxFieldSize - existingSize;
        int allowedByMaxTotal = this.maxTotalEstimatedSize - this.estimatedSize - 1;
        return Math.min(allowedByMaxField, allowedByMaxTotal -= existingSizeInt == null ? filterKey.size : 0);
    }

    private void setFilterKey(StringSizePair filterKey, String value, Map<String, String[]> data) {
        if (!data.containsKey(filterKey.string) && filterKey.size + this.estimatedSize > this.maxTotalEstimatedSize) {
            this.setTruncated(data);
            return;
        }
        Integer fieldSizeInteger = this.fieldSizes.get(filterKey.string);
        int fieldSize = fieldSizeInteger == null ? 0 : fieldSizeInteger;
        int maxAllowed = this.maxAllowedToSet(filterKey);
        if (maxAllowed <= 0) {
            this.setTruncated(data);
            return;
        }
        int valueLength = StandardWriteFilter.estimateSize(value);
        String toSet = value;
        if (valueLength > maxAllowed && (valueLength = StandardWriteFilter.estimateSize(toSet = this.truncate(value, maxAllowed, data))) == 0) {
            return;
        }
        int addedOverall = 0;
        if (fieldSizeInteger == null) {
            addedOverall += filterKey.size;
        }
        this.estimatedSize += (addedOverall += valueLength - fieldSize);
        this.fieldSizes.put(filterKey.string, valueLength);
        data.put(filterKey.string, new String[]{toSet});
    }

    private void setTruncated(Map<String, String[]> data) {
        data.put(METADATA_TRUNCATED_KEY, TRUE);
    }

    private StringSizePair filterKey(String field, String value, Map<String, String[]> data) {
        int size = StandardWriteFilter.estimateSize(field);
        if (size <= this.maxKeySize) {
            return new StringSizePair(field, size, false);
        }
        String toWrite = this.truncate(field, this.maxKeySize, data);
        return new StringSizePair(toWrite, StandardWriteFilter.estimateSize(toWrite), true);
    }

    private String truncate(String value, int length, Map<String, String[]> data) {
        this.setTruncated(data);
        byte[] bytes = value.getBytes(StandardCharsets.UTF_16BE);
        ByteBuffer bb = ByteBuffer.wrap(bytes, 0, length);
        CharBuffer cb = CharBuffer.allocate(length);
        CharsetDecoder decoder = StandardCharsets.UTF_16BE.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        decoder.decode(bb, cb, true);
        decoder.flush(cb);
        return new String(cb.array(), 0, cb.position());
    }

    private boolean include(String field, String value) {
        return this.includeField(field) && this.includeValue(value);
    }

    private boolean includeValue(String value) {
        if (this.includeEmpty) {
            return true;
        }
        return !StringUtils.isBlank(value);
    }

    private boolean includeField(String name) {
        if (ALWAYS_SET_FIELDS.contains(name)) {
            return true;
        }
        return this.includeFields == null || this.includeFields.contains(name);
    }

    private static int estimateSize(String s) {
        return 2 * s.length();
    }

    static {
        ALWAYS_SET_FIELDS.add("Content-Length");
        ALWAYS_SET_FIELDS.add("Content-Type");
        ALWAYS_SET_FIELDS.add("Content-Encoding");
        ALWAYS_SET_FIELDS.add(TikaCoreProperties.CONTENT_TYPE_USER_OVERRIDE.getName());
        ALWAYS_SET_FIELDS.add(TikaCoreProperties.CONTENT_TYPE_PARSER_OVERRIDE.getName());
        ALWAYS_SET_FIELDS.add(TikaCoreProperties.CONTENT_TYPE_HINT.getName());
        ALWAYS_SET_FIELDS.add(TikaCoreProperties.TIKA_CONTENT.getName());
        ALWAYS_SET_FIELDS.add("resourceName");
        ALWAYS_SET_FIELDS.add(AccessPermissions.EXTRACT_CONTENT.getName());
        ALWAYS_SET_FIELDS.add(AccessPermissions.EXTRACT_FOR_ACCESSIBILITY.getName());
        ALWAYS_SET_FIELDS.add("Content-Disposition");
        ALWAYS_SET_FIELDS.add(TikaCoreProperties.CONTAINER_EXCEPTION.getName());
        ALWAYS_SET_FIELDS.add(TikaCoreProperties.EMBEDDED_EXCEPTION.getName());
        ALWAYS_ADD_FIELDS.add(TikaCoreProperties.TIKA_PARSED_BY.getName());
        METADATA_TRUNCATED_KEY = TikaCoreProperties.TRUNCATED_METADATA.getName();
        TIKA_CONTENT_KEY = TikaCoreProperties.TIKA_CONTENT.getName();
        TRUE = new String[]{"true"};
    }

    private static class StringSizePair {
        final String string;
        final int size;
        final boolean truncated;

        public StringSizePair(String string, int size, boolean truncated) {
            this.string = string;
            this.size = size;
            this.truncated = truncated;
        }
    }
}

