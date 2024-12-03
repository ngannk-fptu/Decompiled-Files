/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.DefaultFormBuilder;
import com.atlassian.httpclient.api.FormBuilder;
import java.util.List;
import java.util.Map;

public final class EntityBuilders {
    private EntityBuilders() {
    }

    public static FormBuilder newForm() {
        return new DefaultFormBuilder();
    }

    FormBuilder newFormWithParams(Map<String, String> params) {
        FormBuilder form = EntityBuilders.newForm();
        for (Map.Entry<String, String> param : params.entrySet()) {
            form.addParam(param.getKey(), param.getValue());
        }
        return form;
    }

    FormBuilder newFormWithListParams(Map<String, List<String>> params) {
        FormBuilder form = EntityBuilders.newForm();
        for (Map.Entry<String, List<String>> param : params.entrySet()) {
            String key = param.getKey();
            List<String> values = param.getValue();
            if (values != null && values.size() > 0) {
                for (String value : values) {
                    form.addParam(key, value);
                }
                continue;
            }
            form.addParam(key);
        }
        return form;
    }
}

