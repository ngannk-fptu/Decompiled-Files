/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 */
package org.apache.commons.configuration2.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.FilterConfig;
import org.apache.commons.configuration2.web.BaseWebConfiguration;

public class ServletFilterConfiguration
extends BaseWebConfiguration {
    protected FilterConfig config;

    public ServletFilterConfiguration(FilterConfig config) {
        this.config = config;
    }

    @Override
    protected Object getPropertyInternal(String key) {
        return this.handleDelimiters(this.config.getInitParameter(key));
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        Enumeration en = this.config.getInitParameterNames();
        return Collections.list(en).iterator();
    }
}

