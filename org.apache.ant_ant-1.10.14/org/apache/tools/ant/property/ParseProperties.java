/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.property;

import java.text.ParsePosition;
import java.util.Collection;
import java.util.Objects;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.property.GetProperty;
import org.apache.tools.ant.property.ParseNextProperty;
import org.apache.tools.ant.property.PropertyExpander;

public class ParseProperties
implements ParseNextProperty {
    private final Project project;
    private final GetProperty getProperty;
    private final Collection<PropertyExpander> expanders;

    public ParseProperties(Project project, Collection<PropertyExpander> expanders, GetProperty getProperty) {
        this.project = project;
        this.expanders = expanders;
        this.getProperty = getProperty;
    }

    @Override
    public Project getProject() {
        return this.project;
    }

    public Object parseProperties(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        int len = value.length();
        ParsePosition pos = new ParsePosition(0);
        Object o = this.parseNextProperty(value, pos);
        if (o != null && pos.getIndex() >= len) {
            return o;
        }
        StringBuilder sb = new StringBuilder(len * 2);
        if (o == null) {
            sb.append(value.charAt(pos.getIndex()));
            pos.setIndex(pos.getIndex() + 1);
        } else {
            sb.append(o);
        }
        while (pos.getIndex() < len) {
            o = this.parseNextProperty(value, pos);
            if (o == null) {
                sb.append(value.charAt(pos.getIndex()));
                pos.setIndex(pos.getIndex() + 1);
                continue;
            }
            sb.append(o);
        }
        return sb.toString();
    }

    public boolean containsProperties(String value) {
        if (value == null) {
            return false;
        }
        int len = value.length();
        ParsePosition pos = new ParsePosition(0);
        while (pos.getIndex() < len) {
            if (this.parsePropertyName(value, pos) != null) {
                return true;
            }
            pos.setIndex(pos.getIndex() + 1);
        }
        return false;
    }

    @Override
    public Object parseNextProperty(String value, ParsePosition pos) {
        int start = pos.getIndex();
        if (start > value.length()) {
            return null;
        }
        String propertyName = this.parsePropertyName(value, pos);
        if (propertyName != null) {
            Object result = this.getProperty(propertyName);
            if (result != null) {
                return result;
            }
            if (this.project != null) {
                this.project.log("Property \"" + propertyName + "\" has not been set", 3);
            }
            return value.substring(start, pos.getIndex());
        }
        return null;
    }

    private String parsePropertyName(String value, ParsePosition pos) {
        return this.expanders.stream().map(xp -> xp.parsePropertyName(value, pos, this)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private Object getProperty(String propertyName) {
        return this.getProperty.getProperty(propertyName);
    }
}

