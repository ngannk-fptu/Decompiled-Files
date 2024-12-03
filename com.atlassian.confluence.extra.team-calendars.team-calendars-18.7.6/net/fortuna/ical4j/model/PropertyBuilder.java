/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import net.fortuna.ical4j.model.AbstractContentBuilder;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.Strings;

public class PropertyBuilder
extends AbstractContentBuilder {
    private List<PropertyFactory<?>> factories = new ArrayList();
    private String name;
    private String value;
    private ParameterList parameters = new ParameterList();

    public PropertyBuilder factories(List<PropertyFactory<?>> factories) {
        this.factories.addAll(factories);
        return this;
    }

    public PropertyBuilder name(String name) {
        this.name = name.toUpperCase();
        return this;
    }

    public PropertyBuilder value(String value) {
        this.value = value.trim();
        return this;
    }

    public PropertyBuilder parameter(Parameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public Property build() throws ParseException, IOException, URISyntaxException {
        XProperty property = null;
        for (PropertyFactory<?> factory : this.factories) {
            if (!factory.supports(this.name) || !((property = (XProperty)factory.createProperty(this.parameters, this.value)) instanceof Escapable)) continue;
            ((Property)property).setValue(Strings.unescape(this.value));
        }
        if (property == null) {
            if (this.isExperimentalName(this.name)) {
                property = new XProperty(this.name, this.parameters, this.value);
            } else if (this.allowIllegalNames()) {
                property = new XProperty(this.name, this.parameters, this.value);
            } else {
                throw new IllegalArgumentException("Illegal property [" + this.name + "]");
            }
        }
        return property;
    }
}

