/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyEasyDict;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.AbstractDict;
import com.google.template.soy.internal.base.Pair;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class EasyDictImpl
extends AbstractDict
implements SoyEasyDict {
    private final SoyValueHelper valueHelper;
    private boolean isMutable;

    public EasyDictImpl(SoyValueHelper valueHelper) {
        super(Maps.newHashMap());
        this.valueHelper = valueHelper;
        this.isMutable = true;
    }

    @Override
    public void setField(String name, SoyValueProvider valueProvider) {
        Preconditions.checkState((boolean)this.isMutable, (Object)"Cannot modify immutable SoyEasyDict.");
        Map concreteMap = this.providerMap;
        concreteMap.put(name, Preconditions.checkNotNull((Object)valueProvider));
    }

    @Override
    public void delField(String name) {
        Preconditions.checkState((boolean)this.isMutable, (Object)"Cannot modify immutable SoyEasyDict.");
        this.providerMap.remove(name);
    }

    @Override
    public void setItemsFromDict(SoyDict dict) {
        for (SoyValue soyValue : dict.getItemKeys()) {
            this.setField(this.getStringKey(soyValue), dict.getItem(soyValue));
        }
    }

    @Override
    public void setFieldsFromJavaStringMap(Map<String, ?> javaStringMap) {
        for (Map.Entry<String, ?> entry : javaStringMap.entrySet()) {
            this.setField(entry.getKey(), this.valueHelper.convert(entry.getValue()));
        }
    }

    @Override
    public void set(String dottedName, @Nullable Object value) {
        Pair<SoyRecord, String> pair = this.getLastRecordAndLastName(dottedName, true);
        if (pair.first == null || !(pair.first instanceof SoyEasyDict)) {
            throw new SoyDataException("Cannot set data at dotted name '" + dottedName + "'.");
        }
        ((SoyEasyDict)pair.first).setField((String)pair.second, this.valueHelper.convert(value));
    }

    @Override
    public void del(String dottedName) {
        Pair<SoyRecord, String> pair = this.getLastRecordAndLastName(dottedName, false);
        if (pair.first == null || !(pair.first instanceof SoyEasyDict)) {
            throw new SoyDataException("Cannot del data at dotted name '" + dottedName + "'.");
        }
        ((SoyEasyDict)pair.first).delField((String)pair.second);
    }

    @Override
    public boolean has(String dottedName) {
        Pair<SoyRecord, String> pair = this.getLastRecordAndLastName(dottedName, false);
        return pair.first != null && ((SoyRecord)pair.first).hasField((String)pair.second);
    }

    @Override
    public SoyValue get(String dottedName) {
        Pair<SoyRecord, String> pair = this.getLastRecordAndLastName(dottedName, false);
        return pair.first != null ? ((SoyRecord)pair.first).getField((String)pair.second) : null;
    }

    @Override
    public SoyValueProvider getProvider(String dottedName) {
        Pair<SoyRecord, String> pair = this.getLastRecordAndLastName(dottedName, false);
        return pair.first != null ? ((SoyRecord)pair.first).getFieldProvider((String)pair.second) : null;
    }

    @Override
    public SoyEasyDict makeImmutable() {
        this.isMutable = false;
        return this;
    }

    private Pair<SoyRecord, String> getLastRecordAndLastName(String dottedName, boolean doCreateRecordsIfNecessary) {
        SoyRecord lastRecord;
        String[] names = dottedName.split("[.]");
        int n = names.length;
        String lastName = names[n - 1];
        if (n == 1) {
            lastRecord = this;
        } else {
            lastRecord = this;
            for (int i = 0; i <= n - 2; ++i) {
                SoyValue value = lastRecord.getField(names[i]);
                if (value instanceof SoyRecord) {
                    lastRecord = (SoyRecord)value;
                    continue;
                }
                if (value == null && doCreateRecordsIfNecessary && lastRecord instanceof SoyEasyDict) {
                    EasyDictImpl newRecord = new EasyDictImpl(this.valueHelper);
                    ((SoyEasyDict)lastRecord).setField(names[i], newRecord);
                    lastRecord = newRecord;
                    continue;
                }
                lastRecord = null;
                break;
            }
        }
        return Pair.of(lastRecord, lastName);
    }
}

