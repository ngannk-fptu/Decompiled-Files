/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.core.util.FastField;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ElementIgnoringMapper
extends MapperWrapper {
    protected final Set fieldsToOmit = new HashSet();
    protected final Set unknownElementsToIgnore = new LinkedHashSet();

    public ElementIgnoringMapper(Mapper wrapped) {
        super(wrapped);
    }

    public void addElementsToIgnore(Pattern pattern) {
        this.unknownElementsToIgnore.add(pattern);
    }

    public void omitField(Class definedIn, String fieldName) {
        this.fieldsToOmit.add(this.key(definedIn, fieldName));
    }

    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
        if (this.fieldsToOmit.contains(this.key(definedIn, fieldName))) {
            return false;
        }
        if (definedIn == Object.class && this.isIgnoredElement(fieldName)) {
            return false;
        }
        return super.shouldSerializeMember(definedIn, fieldName);
    }

    public boolean isIgnoredElement(String name) {
        if (!this.unknownElementsToIgnore.isEmpty()) {
            Iterator iter = this.unknownElementsToIgnore.iterator();
            while (iter.hasNext()) {
                Pattern pattern = (Pattern)iter.next();
                if (!pattern.matcher(name).matches()) continue;
                return true;
            }
        }
        return super.isIgnoredElement(name);
    }

    private Object key(Class type, String name) {
        return new FastField(type, name);
    }
}

