/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Parameter;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ParameterList
implements Serializable,
Iterable<Parameter> {
    private static final long serialVersionUID = -1913059830016450169L;
    private final List<Parameter> parameters;

    public ParameterList() {
        this(false);
    }

    public ParameterList(boolean unmodifiable) {
        this.parameters = unmodifiable ? Collections.emptyList() : new CopyOnWriteArrayList<Parameter>();
    }

    public ParameterList(ParameterList list, boolean unmodifiable) {
        CopyOnWriteArrayList<Parameter> parameterList = new CopyOnWriteArrayList<Parameter>();
        list.forEach(parameter -> {
            try {
                parameterList.add((Parameter)parameter.copy());
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        });
        this.parameters = unmodifiable ? Collections.unmodifiableList(parameterList) : parameterList;
    }

    public final String toString() {
        if (!this.parameters.isEmpty()) {
            return this.parameters.stream().map(Parameter::toString).collect(Collectors.joining(";", ";", ""));
        }
        return "";
    }

    public final <T extends Parameter> T getParameter(String aName) {
        for (Parameter p : this.parameters) {
            if (!aName.equalsIgnoreCase(p.getName())) continue;
            return (T)p;
        }
        return null;
    }

    public final ParameterList getParameters(String name) {
        ParameterList list = new ParameterList();
        for (Parameter p : this.parameters) {
            if (!p.getName().equalsIgnoreCase(name)) continue;
            list.add(p);
        }
        return list;
    }

    public final boolean add(Parameter parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("Trying to add null Parameter");
        }
        return this.parameters.add(parameter);
    }

    public final boolean replace(Parameter parameter) {
        for (Parameter parameter1 : this.getParameters(parameter.getName())) {
            this.remove(parameter1);
        }
        return this.add(parameter);
    }

    public final boolean isEmpty() {
        return this.parameters.isEmpty();
    }

    @Override
    public final Iterator<Parameter> iterator() {
        return this.parameters.iterator();
    }

    public final boolean remove(Parameter parameter) {
        return this.parameters.remove(parameter);
    }

    public final void removeAll(String paramName) {
        ParameterList params = this.getParameters(paramName);
        this.parameters.removeAll(params.parameters);
    }

    public final int size() {
        return this.parameters.size();
    }

    public final boolean equals(Object arg0) {
        if (arg0 instanceof ParameterList) {
            ParameterList p = (ParameterList)arg0;
            return Objects.equals(this.parameters, p.parameters);
        }
        return super.equals(arg0);
    }

    public final int hashCode() {
        return new HashCodeBuilder().append(this.parameters).toHashCode();
    }
}

