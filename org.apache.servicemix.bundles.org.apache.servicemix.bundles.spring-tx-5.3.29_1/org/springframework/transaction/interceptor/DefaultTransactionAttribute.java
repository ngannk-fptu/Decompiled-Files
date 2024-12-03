/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.transaction.interceptor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

public class DefaultTransactionAttribute
extends DefaultTransactionDefinition
implements TransactionAttribute {
    @Nullable
    private String descriptor;
    @Nullable
    private String timeoutString;
    @Nullable
    private String qualifier;
    private Collection<String> labels = Collections.emptyList();

    public DefaultTransactionAttribute() {
    }

    public DefaultTransactionAttribute(TransactionAttribute other) {
        super(other);
    }

    public DefaultTransactionAttribute(int propagationBehavior) {
        super(propagationBehavior);
    }

    public void setDescriptor(@Nullable String descriptor) {
        this.descriptor = descriptor;
    }

    @Nullable
    public String getDescriptor() {
        return this.descriptor;
    }

    public void setTimeoutString(@Nullable String timeoutString) {
        this.timeoutString = timeoutString;
    }

    @Nullable
    public String getTimeoutString() {
        return this.timeoutString;
    }

    public void setQualifier(@Nullable String qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    @Nullable
    public String getQualifier() {
        return this.qualifier;
    }

    public void setLabels(Collection<String> labels) {
        this.labels = labels;
    }

    @Override
    public Collection<String> getLabels() {
        return this.labels;
    }

    @Override
    public boolean rollbackOn(Throwable ex) {
        return ex instanceof RuntimeException || ex instanceof Error;
    }

    public void resolveAttributeStrings(@Nullable StringValueResolver resolver) {
        String timeoutString = this.timeoutString;
        if (StringUtils.hasText((String)timeoutString)) {
            if (resolver != null) {
                timeoutString = resolver.resolveStringValue(timeoutString);
            }
            if (StringUtils.hasLength((String)timeoutString)) {
                try {
                    this.setTimeout(Integer.parseInt(timeoutString));
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Invalid timeoutString value \"" + timeoutString + "\" - cannot parse into int");
                }
            }
        }
        if (resolver != null) {
            if (this.qualifier != null) {
                this.qualifier = resolver.resolveStringValue(this.qualifier);
            }
            LinkedHashSet<String> resolvedLabels = new LinkedHashSet<String>(this.labels.size());
            for (String label : this.labels) {
                resolvedLabels.add(resolver.resolveStringValue(label));
            }
            this.labels = resolvedLabels;
        }
    }

    protected final StringBuilder getAttributeDescription() {
        StringBuilder result = this.getDefinitionDescription();
        if (StringUtils.hasText((String)this.qualifier)) {
            result.append("; '").append(this.qualifier).append('\'');
        }
        if (!this.labels.isEmpty()) {
            result.append("; ").append(this.labels);
        }
        return result;
    }
}

