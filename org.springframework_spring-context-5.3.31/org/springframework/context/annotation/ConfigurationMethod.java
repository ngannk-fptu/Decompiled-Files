/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.parsing.Location
 *  org.springframework.beans.factory.parsing.ProblemReporter
 *  org.springframework.core.type.MethodMetadata
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.context.annotation.ConfigurationClass;
import org.springframework.core.type.MethodMetadata;

abstract class ConfigurationMethod {
    protected final MethodMetadata metadata;
    protected final ConfigurationClass configurationClass;

    public ConfigurationMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
        this.metadata = metadata;
        this.configurationClass = configurationClass;
    }

    public MethodMetadata getMetadata() {
        return this.metadata;
    }

    public ConfigurationClass getConfigurationClass() {
        return this.configurationClass;
    }

    public Location getResourceLocation() {
        return new Location(this.configurationClass.getResource(), (Object)this.metadata);
    }

    void validate(ProblemReporter problemReporter) {
    }

    public String toString() {
        return String.format("[%s:name=%s,declaringClass=%s]", this.getClass().getSimpleName(), this.getMetadata().getMethodName(), this.getMetadata().getDeclaringClassName());
    }
}

