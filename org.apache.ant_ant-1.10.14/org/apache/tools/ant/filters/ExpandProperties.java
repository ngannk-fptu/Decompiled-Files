/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.filters.BaseFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.property.GetProperty;
import org.apache.tools.ant.property.ParseProperties;
import org.apache.tools.ant.types.PropertySet;

public final class ExpandProperties
extends BaseFilterReader
implements ChainableReader {
    private static final int EOF = -1;
    private char[] buffer;
    private int index;
    private PropertySet propertySet;

    public ExpandProperties() {
    }

    public ExpandProperties(Reader in) {
        super(in);
    }

    public void add(PropertySet propertySet) {
        if (this.propertySet != null) {
            throw new BuildException("expandproperties filter accepts only one propertyset");
        }
        this.propertySet = propertySet;
    }

    @Override
    public int read() throws IOException {
        if (this.index > -1) {
            if (this.buffer == null) {
                String data = this.readFully();
                Project project = this.getProject();
                GetProperty getProperty = this.propertySet == null ? PropertyHelper.getPropertyHelper(project) : this.propertySet.getProperties()::getProperty;
                Object expanded = new ParseProperties(project, PropertyHelper.getPropertyHelper(project).getExpanders(), getProperty).parseProperties(data);
                char[] cArray = this.buffer = expanded == null ? new char[]{} : expanded.toString().toCharArray();
            }
            if (this.index < this.buffer.length) {
                return this.buffer[this.index++];
            }
            this.index = -1;
        }
        return -1;
    }

    @Override
    public Reader chain(Reader rdr) {
        ExpandProperties newFilter = new ExpandProperties(rdr);
        newFilter.setProject(this.getProject());
        newFilter.add(this.propertySet);
        return newFilter;
    }
}

