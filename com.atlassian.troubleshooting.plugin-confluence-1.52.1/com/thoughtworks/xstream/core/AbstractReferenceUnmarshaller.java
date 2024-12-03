/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.TreeUnmarshaller;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractReferenceUnmarshaller
extends TreeUnmarshaller {
    private static final Object NULL = new Object();
    private Map values = new HashMap();
    private FastStack parentStack = new FastStack(16);

    public AbstractReferenceUnmarshaller(Object root, HierarchicalStreamReader reader, ConverterLookup converterLookup, Mapper mapper) {
        super(root, reader, converterLookup, mapper);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Object convert(Object parent, Class type, Converter converter) {
        Object result;
        block8: {
            String attributeName;
            Object parentReferenceKey;
            if (this.parentStack.size() > 0 && (parentReferenceKey = this.parentStack.peek()) != null && !this.values.containsKey(parentReferenceKey)) {
                this.values.put(parentReferenceKey, parent);
            }
            String reference = (attributeName = this.getMapper().aliasForSystemAttribute("reference")) == null ? null : this.reader.getAttribute(attributeName);
            boolean isReferenceable = this.getMapper().isReferenceable(type);
            if (reference != null) {
                Object cache;
                Object v0 = cache = isReferenceable ? this.values.get(this.getReferenceKey(reference)) : null;
                if (cache == null) {
                    ConversionException ex = new ConversionException("Invalid reference");
                    ex.add("reference", reference);
                    ex.add("referenced-type", type.getName());
                    ex.add("referenceable", Boolean.toString(isReferenceable));
                    throw ex;
                }
                if (cache == NULL) {
                    return null;
                }
                Object v1 = cache;
                return v1;
            }
            if (!isReferenceable) {
                return super.convert(parent, type, converter);
            }
            Object currentReferenceKey = this.getCurrentReferenceKey();
            this.parentStack.push(currentReferenceKey);
            Object localResult = null;
            try {
                result = localResult = super.convert(parent, type, converter);
                if (currentReferenceKey == null) break block8;
                this.values.put(currentReferenceKey, result == null ? NULL : result);
            }
            catch (Throwable throwable) {
                result = localResult;
                if (currentReferenceKey != null) {
                    this.values.put(currentReferenceKey, result == null ? NULL : result);
                }
                this.parentStack.popSilently();
                throw throwable;
            }
        }
        this.parentStack.popSilently();
        return result;
    }

    protected abstract Object getReferenceKey(String var1);

    protected abstract Object getCurrentReferenceKey();
}

