/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import java.util.HashMap;
import java.util.Map;

public class BatchTemplateMessage
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "message";
    private final String message;
    private final Map<String, Object> args;

    private BatchTemplateMessage(String message, Map<String, Object> args) {
        this.message = message;
        this.args = args;
    }

    public String getMessage() {
        return this.message;
    }

    public Map<String, Object> getArgs() {
        return this.args;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    public static class Builder {
        private final String message;
        private Map<String, Object> args = new HashMap<String, Object>();

        public Builder(String message) {
            this.message = message;
        }

        public Builder arg(String name, BatchTemplateElement value) {
            this.args.put(name, value);
            return this;
        }

        public Builder arg(String name, String value) {
            this.args.put(name, value);
            return this;
        }

        public BatchTemplateMessage build() {
            return new BatchTemplateMessage(this.message, this.args);
        }
    }
}

