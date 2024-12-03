/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.view;

import java.util.Locale;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractUrlBasedView
extends AbstractView
implements InitializingBean {
    @Nullable
    private String url;

    protected AbstractUrlBasedView() {
    }

    protected AbstractUrlBasedView(String url) {
        this.url = url;
    }

    public void setUrl(@Nullable String url) {
        this.url = url;
    }

    @Nullable
    public String getUrl() {
        return this.url;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.isUrlRequired() && this.getUrl() == null) {
            throw new IllegalArgumentException("Property 'url' is required");
        }
    }

    protected boolean isUrlRequired() {
        return true;
    }

    public boolean checkResource(Locale locale) throws Exception {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "; URL [" + this.getUrl() + "]";
    }
}

