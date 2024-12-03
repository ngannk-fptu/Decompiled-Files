/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.web;

import java.util.Collection;
import org.apache.commons.configuration2.AbstractConfiguration;

abstract class BaseWebConfiguration
extends AbstractConfiguration {
    BaseWebConfiguration() {
    }

    @Override
    protected boolean isEmptyInternal() {
        return !this.getKeysInternal().hasNext();
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        return this.getPropertyInternal(key) != null;
    }

    @Override
    protected void clearPropertyDirect(String key) {
        throw new UnsupportedOperationException("Read only configuration");
    }

    @Override
    protected void addPropertyDirect(String key, Object obj) {
        throw new UnsupportedOperationException("Read only configuration");
    }

    protected Object handleDelimiters(Object value) {
        if (value instanceof String) {
            Collection<String> values = this.getListDelimiterHandler().split((String)((Object)value), true);
            value = values.size() > 1 ? values : values.iterator().next();
        }
        return value;
    }
}

