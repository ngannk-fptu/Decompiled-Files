/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.fortuna.ical4j.model.AbstractContentBuilder;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.util.Strings;

public class ParameterBuilder
extends AbstractContentBuilder {
    private List<ParameterFactory<?>> factories = new ArrayList();
    private String name;
    private String value;

    public ParameterBuilder factories(List<ParameterFactory<?>> factories) {
        this.factories.addAll(factories);
        return this;
    }

    public ParameterBuilder name(String name) {
        this.name = name.toUpperCase();
        return this;
    }

    public ParameterBuilder value(String value) {
        this.value = Strings.escapeNewline(value);
        return this;
    }

    public Parameter build() throws URISyntaxException {
        XParameter parameter = null;
        for (ParameterFactory<?> factory : this.factories) {
            if (!factory.supports(this.name)) continue;
            parameter = (XParameter)factory.createParameter(this.value);
            break;
        }
        if (parameter == null) {
            if (this.isExperimentalName(this.name)) {
                parameter = new XParameter(this.name, this.value);
            } else if (this.allowIllegalNames()) {
                parameter = new XParameter(this.name, this.value);
            } else {
                throw new IllegalArgumentException(String.format("Unsupported parameter name: %s", this.name));
            }
        }
        return parameter;
    }
}

