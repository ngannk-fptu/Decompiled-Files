/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.util.GeneralUtil;

@Deprecated
public abstract class AbstractLabelEntityBean {
    ConfluenceActionSupport dummy = GeneralUtil.newWiredConfluenceActionSupport();

    protected AbstractLabelEntityBean() {
    }

    public String getText(String key) {
        return this.dummy.getText(key);
    }

    public String getText(String key, Object[] inserts) {
        return this.dummy.getText(key, inserts);
    }

    public String getText(String key, String str1) {
        return this.dummy.getText(key, str1);
    }
}

