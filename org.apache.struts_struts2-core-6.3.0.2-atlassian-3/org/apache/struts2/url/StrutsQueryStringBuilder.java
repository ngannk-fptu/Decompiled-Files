/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.url;

import com.opensymphony.xwork2.inject.Inject;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.url.QueryStringBuilder;
import org.apache.struts2.url.UrlEncoder;

public class StrutsQueryStringBuilder
implements QueryStringBuilder {
    private static final Logger LOG = LogManager.getLogger(StrutsQueryStringBuilder.class);
    private final UrlEncoder encoder;

    @Inject
    public StrutsQueryStringBuilder(UrlEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void build(Map<String, Object> params, StringBuilder link, String paramSeparator) {
        if (params == null || params.isEmpty()) {
            LOG.debug("Params are empty, skipping building the query string");
            return;
        }
        LOG.debug("Building query string out of: {} parameters", (Object)params.size());
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Iterable) {
                for (Object o : (Iterable)value) {
                    this.appendParameterSubstring(queryString, paramSeparator, name, o);
                }
                continue;
            }
            if (value instanceof Object[]) {
                Object[] array;
                for (Object o : array = (Object[])value) {
                    this.appendParameterSubstring(queryString, paramSeparator, name, o);
                }
                continue;
            }
            this.appendParameterSubstring(queryString, paramSeparator, name, value);
        }
        if (queryString.length() > 0) {
            if (!link.toString().contains("?")) {
                link.append("?");
            } else {
                link.append(paramSeparator);
            }
            link.append((CharSequence)queryString);
        }
    }

    private void appendParameterSubstring(StringBuilder queryString, String paramSeparator, String name, Object value) {
        if (queryString.length() > 0) {
            queryString.append(paramSeparator);
        }
        String encodedName = this.encoder.encode(name);
        queryString.append(encodedName);
        queryString.append('=');
        if (value != null) {
            String encodedValue = this.encoder.encode(value.toString());
            queryString.append(encodedValue);
        }
    }
}

