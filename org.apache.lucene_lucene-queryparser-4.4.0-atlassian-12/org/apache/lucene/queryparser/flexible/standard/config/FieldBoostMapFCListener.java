/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.config;

import java.util.Map;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfigListener;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;

public class FieldBoostMapFCListener
implements FieldConfigListener {
    private QueryConfigHandler config = null;

    public FieldBoostMapFCListener(QueryConfigHandler config) {
        this.config = config;
    }

    @Override
    public void buildFieldConfig(FieldConfig fieldConfig) {
        Float boost;
        Map<String, Float> fieldBoostMap = this.config.get(StandardQueryConfigHandler.ConfigurationKeys.FIELD_BOOST_MAP);
        if (fieldBoostMap != null && (boost = fieldBoostMap.get(fieldConfig.getField())) != null) {
            fieldConfig.set(StandardQueryConfigHandler.ConfigurationKeys.BOOST, boost);
        }
    }
}

