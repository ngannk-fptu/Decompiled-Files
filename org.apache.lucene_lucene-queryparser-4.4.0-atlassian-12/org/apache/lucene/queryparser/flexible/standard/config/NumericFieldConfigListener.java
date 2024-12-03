/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.config;

import java.util.Map;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfigListener;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;

public class NumericFieldConfigListener
implements FieldConfigListener {
    private final QueryConfigHandler config;

    public NumericFieldConfigListener(QueryConfigHandler config) {
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null!");
        }
        this.config = config;
    }

    @Override
    public void buildFieldConfig(FieldConfig fieldConfig) {
        NumericConfig numericConfig;
        Map<String, NumericConfig> numericConfigMap = this.config.get(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG_MAP);
        if (numericConfigMap != null && (numericConfig = numericConfigMap.get(fieldConfig.getField())) != null) {
            fieldConfig.set(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG, numericConfig);
        }
    }
}

