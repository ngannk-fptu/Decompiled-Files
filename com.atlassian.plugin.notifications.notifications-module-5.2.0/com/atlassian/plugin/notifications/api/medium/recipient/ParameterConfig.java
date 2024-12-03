/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.api.medium.recipient;

import com.atlassian.plugin.notifications.api.medium.recipient.ParameterOption;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class ParameterConfig {
    @JsonProperty
    private final String html;
    @JsonProperty
    private final List<ParameterOption> options;
    @JsonProperty
    private final String url;

    private ParameterConfig(String html, List<ParameterOption> options, String url) {
        this.html = html;
        this.options = options;
        this.url = url;
    }

    public String getHtml() {
        return this.html;
    }

    public List<ParameterOption> getOptions() {
        return this.options;
    }

    public String getUrl() {
        return this.url;
    }

    public static class Builder {
        public ParameterConfig buildSelect(List<ParameterOption> options) {
            return new ParameterConfig(null, options, null);
        }

        public ParameterConfig buildAjaxSelect(String url) {
            return new ParameterConfig(null, null, url);
        }

        public ParameterConfig buildHtml(String html) {
            return new ParameterConfig(html, null, null);
        }
    }
}

