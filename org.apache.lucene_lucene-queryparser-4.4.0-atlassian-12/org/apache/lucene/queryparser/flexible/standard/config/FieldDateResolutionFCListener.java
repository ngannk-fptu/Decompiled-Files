/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.DateTools$Resolution
 */
package org.apache.lucene.queryparser.flexible.standard.config;

import java.util.Map;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfigListener;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;

public class FieldDateResolutionFCListener
implements FieldConfigListener {
    private QueryConfigHandler config = null;

    public FieldDateResolutionFCListener(QueryConfigHandler config) {
        this.config = config;
    }

    @Override
    public void buildFieldConfig(FieldConfig fieldConfig) {
        DateTools.Resolution dateRes = null;
        Map<CharSequence, DateTools.Resolution> dateResMap = this.config.get(StandardQueryConfigHandler.ConfigurationKeys.FIELD_DATE_RESOLUTION_MAP);
        if (dateResMap != null) {
            dateRes = dateResMap.get(fieldConfig.getField());
        }
        if (dateRes == null) {
            dateRes = this.config.get(StandardQueryConfigHandler.ConfigurationKeys.DATE_RESOLUTION);
        }
        if (dateRes != null) {
            fieldConfig.set(StandardQueryConfigHandler.ConfigurationKeys.DATE_RESOLUTION, dateRes);
        }
    }
}

