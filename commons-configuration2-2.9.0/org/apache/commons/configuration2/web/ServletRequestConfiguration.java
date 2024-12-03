/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 */
package org.apache.commons.configuration2.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletRequest;
import org.apache.commons.configuration2.web.BaseWebConfiguration;

public class ServletRequestConfiguration
extends BaseWebConfiguration {
    protected ServletRequest request;

    public ServletRequestConfiguration(ServletRequest request) {
        this.request = request;
    }

    @Override
    protected Object getPropertyInternal(String key) {
        String[] values = this.request.getParameterValues(key);
        if (values == null || values.length == 0) {
            return null;
        }
        if (values.length == 1) {
            return this.handleDelimiters(values[0]);
        }
        ArrayList<Object> result = new ArrayList<Object>(values.length);
        for (String value : values) {
            Object val = this.handleDelimiters(value);
            if (val instanceof Collection) {
                result.addAll((Collection)val);
                continue;
            }
            result.add(val);
        }
        return result;
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        Map parameterMap = this.request.getParameterMap();
        return parameterMap.keySet().iterator();
    }
}

