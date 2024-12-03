/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.config;

import java.util.LinkedList;
import org.apache.lucene.queryparser.flexible.core.config.AbstractQueryConfig;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfigListener;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;

public abstract class QueryConfigHandler
extends AbstractQueryConfig {
    private final LinkedList<FieldConfigListener> listeners = new LinkedList();

    public FieldConfig getFieldConfig(String fieldName) {
        FieldConfig fieldConfig = new FieldConfig(StringUtils.toString(fieldName));
        for (FieldConfigListener listener : this.listeners) {
            listener.buildFieldConfig(fieldConfig);
        }
        return fieldConfig;
    }

    public void addFieldConfigListener(FieldConfigListener listener) {
        this.listeners.add(listener);
    }
}

