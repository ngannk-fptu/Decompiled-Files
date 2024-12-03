/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.CompoundConfiguration;
import org.apache.velocity.tools.config.Data;
import org.apache.velocity.tools.config.ToolboxConfiguration;

public class FactoryConfiguration
extends CompoundConfiguration<ToolboxConfiguration> {
    private final SortedSet<Data> data = new TreeSet<Data>();
    private final List<String> sources = new ArrayList<String>();

    public FactoryConfiguration() {
        this("");
    }

    public FactoryConfiguration(String source) {
        this(FactoryConfiguration.class, source);
    }

    protected FactoryConfiguration(Class clazz, String source) {
        this.addSource(clazz.getName() + "(" + source + ")");
    }

    public String getSource() {
        return this.sources.get(0);
    }

    public void setSource(String source) {
        this.sources.set(0, source);
    }

    public List<String> getSources() {
        return this.sources;
    }

    public void addSource(String source) {
        this.sources.add(source);
    }

    public void addData(Data newDatum) {
        Data datum = this.getData(newDatum);
        if (datum != null) {
            this.removeData(datum);
        }
        this.data.add(newDatum);
    }

    public boolean removeData(Data datum) {
        return this.data.remove(datum);
    }

    public Data getData(String key) {
        Data findme = new Data();
        findme.setKey(key);
        return this.getData(findme);
    }

    public Data getData(Data findme) {
        for (Data datum : this.data) {
            if (!datum.equals(findme)) continue;
            return datum;
        }
        return null;
    }

    public boolean hasData() {
        return !this.data.isEmpty();
    }

    public SortedSet<Data> getData() {
        return this.data;
    }

    public void setData(Collection<Data> data) {
        for (Data datum : data) {
            this.addData(datum);
        }
    }

    public void addToolbox(ToolboxConfiguration toolbox) {
        this.addChild(toolbox);
    }

    public void removeToolbox(ToolboxConfiguration toolbox) {
        this.removeChild(toolbox);
    }

    public ToolboxConfiguration getToolbox(String scope) {
        for (ToolboxConfiguration toolbox : this.getToolboxes()) {
            if (!scope.equals(toolbox.getScope())) continue;
            return toolbox;
        }
        return null;
    }

    public Collection<ToolboxConfiguration> getToolboxes() {
        return this.getChildren();
    }

    public void setToolboxes(Collection<ToolboxConfiguration> toolboxes) {
        this.setChildren(toolboxes);
    }

    public void addConfiguration(FactoryConfiguration config) {
        this.setData(config.getData());
        for (String source : config.getSources()) {
            this.addSource(source);
        }
        super.addConfiguration(config);
    }

    @Override
    public void validate() {
        super.validate();
        for (Data datum : this.data) {
            datum.validate();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FactoryConfiguration) {
            FactoryConfiguration that = (FactoryConfiguration)o;
            return that.toString(false).equals(this.toString(false));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString(false).hashCode();
    }

    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean includeSources) {
        StringBuilder out = new StringBuilder();
        out.append("\nFactoryConfiguration from ");
        if (includeSources) {
            out.append(this.getSources().size());
            out.append(" sources ");
        }
        this.appendProperties(out);
        if (this.hasData()) {
            out.append("including ");
            out.append(this.data.size());
            out.append(" data");
        }
        if (this.getToolboxes().isEmpty()) {
            out.append("\n ");
        } else {
            this.appendChildren(out, "toolboxes: \n ", "\n ");
        }
        if (this.hasData()) {
            for (Data datum : this.data) {
                out.append(datum);
                out.append("\n ");
            }
        }
        if (includeSources) {
            int count = 0;
            for (String source : this.getSources()) {
                out.append("\n Source ");
                out.append(count++);
                out.append(": ");
                out.append(source);
            }
            out.append("\n");
        }
        return out.toString();
    }

    public ToolboxFactory createFactory() {
        ToolboxFactory factory = new ToolboxFactory();
        factory.configure(this);
        return factory;
    }
}

