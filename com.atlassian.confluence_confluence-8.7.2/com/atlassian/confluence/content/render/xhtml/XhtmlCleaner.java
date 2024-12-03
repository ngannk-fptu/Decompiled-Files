/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public interface XhtmlCleaner {
    public Result clean(ContentEntityObject var1);

    public String cleanQuietly(ContentEntityObject var1);

    @Deprecated
    public String cleanQuietly(String var1, ConversionContext var2);

    public String cleanQuietly(String var1);

    public String cleanStyleAttribute(String var1);

    public boolean isCleanUrlAttribute(String var1);

    public static class Result {
        private List<AppliedRuleDescription> ruleDescriptions = new ArrayList<AppliedRuleDescription>();
        private String cleanedData;

        public void setCleanedData(String data) {
            this.cleanedData = data;
        }

        public void addAppliedRuleDescription(AppliedRuleDescription description) {
            this.ruleDescriptions.add(description);
        }

        public List<AppliedRuleDescription> getAppliedRuleDescriptions() {
            return Collections.unmodifiableList(this.ruleDescriptions);
        }

        public String getCleanedData() {
            return this.cleanedData;
        }
    }

    public static class AppliedRuleDescription {
        private List<String> parameters;
        private String key;

        public AppliedRuleDescription(String key, List<String> parameters) {
            this.key = key;
            this.parameters = parameters;
        }

        public List<String> getParameters() {
            return this.parameters;
        }

        public String getKey() {
            return this.key;
        }
    }
}

