/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.model.Model;

public class DependencyDefinition {
    Model depender;
    String dependee;

    public DependencyDefinition(Model depender, String dependee) {
        this.depender = depender;
        this.dependee = dependee;
    }

    public String getDependee() {
        return this.dependee;
    }

    public Model getDepender() {
        return this.depender;
    }
}

