/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringSubstitutor
 *  org.apache.commons.text.lookup.StringLookupFactory
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.config.providers.ValueSubstitutor;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvsValueSubstitutor
implements ValueSubstitutor {
    private static final Logger LOG = LogManager.getLogger(EnvsValueSubstitutor.class);
    protected StringSubstitutor envStrSubstitutor = new StringSubstitutor(System.getenv());
    protected StringSubstitutor sysStrSubstitutor;

    public EnvsValueSubstitutor() {
        this.envStrSubstitutor.setVariablePrefix("${env.");
        this.envStrSubstitutor.setVariableSuffix('}');
        this.envStrSubstitutor.setValueDelimiter(':');
        this.sysStrSubstitutor = new StringSubstitutor(StringLookupFactory.INSTANCE.systemPropertyStringLookup());
        this.sysStrSubstitutor.setVariablePrefix("${");
        this.sysStrSubstitutor.setVariableSuffix('}');
        this.sysStrSubstitutor.setValueDelimiter(':');
    }

    @Override
    public String substitute(String value) {
        LOG.debug("Substituting value {} with proper System variable or environment variable", (Object)value);
        String substituted = this.sysStrSubstitutor.replace(value);
        return this.envStrSubstitutor.replace(substituted);
    }
}

