/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.view;

import com.atlassian.gadgets.view.ViewType;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class View
implements Serializable {
    public static final View DEFAULT = new Builder().viewType(ViewType.DEFAULT).writable(true).build();
    public static final View DIRECTORY = new Builder().viewType(ViewType.DIRECTORY).writable(false).build();
    private static final String WRITABLE_PARAM_NAME = "writable";
    private final ViewType viewType;
    private final boolean writable;
    private final Map<String, String> params;

    private View(Builder builder) {
        this.viewType = builder.viewType;
        String writableParam = (String)builder.paramMap.get(WRITABLE_PARAM_NAME);
        this.writable = writableParam == null ? false : Boolean.valueOf(writableParam);
        this.params = Collections.unmodifiableMap(new HashMap(builder.paramMap));
    }

    public ViewType getViewType() {
        return this.viewType;
    }

    public boolean isWritable() {
        return this.writable;
    }

    public Map<String, String> paramsAsMap() {
        return this.params;
    }

    public static class Builder {
        private ViewType viewType;
        private Map<String, String> paramMap = new HashMap<String, String>();

        public Builder viewType(ViewType viewType) {
            this.viewType = viewType;
            return this;
        }

        public Builder writable(boolean writable) {
            this.paramMap.put(View.WRITABLE_PARAM_NAME, Boolean.toString(writable));
            return this;
        }

        public Builder addViewParam(String name, String value) {
            this.paramMap.put(name, value);
            return this;
        }

        public Builder addViewParams(Map<String, String> params) {
            this.paramMap.putAll(params);
            return this;
        }

        public View build() {
            return new View(this);
        }
    }
}

