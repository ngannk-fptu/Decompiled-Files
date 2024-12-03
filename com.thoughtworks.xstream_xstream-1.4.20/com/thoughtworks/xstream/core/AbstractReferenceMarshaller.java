/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.ReferencingMarshallingContext;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.core.util.ObjectIdDictionary;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import java.util.Iterator;

public abstract class AbstractReferenceMarshaller
extends TreeMarshaller {
    private ObjectIdDictionary references = new ObjectIdDictionary();
    private ObjectIdDictionary implicitElements = new ObjectIdDictionary();
    private PathTracker pathTracker = new PathTracker();
    private Path lastPath;

    public AbstractReferenceMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
        super(writer, converterLookup, mapper);
        this.writer = new PathTrackingWriter(writer, this.pathTracker);
    }

    public void convert(Object item, Converter converter) {
        if (this.getMapper().isImmutableValueType(item.getClass())) {
            converter.marshal(item, this.writer, this);
        } else {
            final Path currentPath = this.pathTracker.getPath();
            Id existingReference = (Id)this.references.lookupId(item);
            if (existingReference != null && existingReference.getPath() != currentPath) {
                String attributeName = this.getMapper().aliasForSystemAttribute("reference");
                if (attributeName != null) {
                    this.writer.addAttribute(attributeName, this.createReference(currentPath, existingReference.getItem()));
                }
            } else {
                Object newReferenceKey;
                Object object = newReferenceKey = existingReference == null ? this.createReferenceKey(currentPath, item) : existingReference.getItem();
                if (this.lastPath == null || !currentPath.isAncestor(this.lastPath)) {
                    this.fireValidReference(newReferenceKey);
                    this.lastPath = currentPath;
                    this.references.associateId(item, new Id(newReferenceKey, currentPath));
                }
                converter.marshal(item, this.writer, new ReferencingMarshallingContext(){

                    public void put(Object key, Object value) {
                        AbstractReferenceMarshaller.this.put(key, value);
                    }

                    public Iterator keys() {
                        return AbstractReferenceMarshaller.this.keys();
                    }

                    public Object get(Object key) {
                        return AbstractReferenceMarshaller.this.get(key);
                    }

                    public void convertAnother(Object nextItem, Converter converter) {
                        AbstractReferenceMarshaller.this.convertAnother(nextItem, converter);
                    }

                    public void convertAnother(Object nextItem) {
                        AbstractReferenceMarshaller.this.convertAnother(nextItem);
                    }

                    public void replace(Object original, Object replacement) {
                        AbstractReferenceMarshaller.this.references.associateId(replacement, new Id(newReferenceKey, currentPath));
                    }

                    public Object lookupReference(Object item) {
                        Id id = (Id)AbstractReferenceMarshaller.this.references.lookupId(item);
                        return id.getItem();
                    }

                    public Path currentPath() {
                        return AbstractReferenceMarshaller.this.pathTracker.getPath();
                    }

                    public void registerImplicit(Object item) {
                        if (AbstractReferenceMarshaller.this.implicitElements.containsId(item)) {
                            throw new ReferencedImplicitElementException(item, currentPath);
                        }
                        AbstractReferenceMarshaller.this.implicitElements.associateId(item, newReferenceKey);
                    }
                });
            }
        }
    }

    protected abstract String createReference(Path var1, Object var2);

    protected abstract Object createReferenceKey(Path var1, Object var2);

    protected abstract void fireValidReference(Object var1);

    public static class ReferencedImplicitElementException
    extends ConversionException {
        public ReferencedImplicitElementException(Object item, Path path) {
            super("Cannot reference implicit element");
            this.add("implicit-element", item.toString());
            this.add("referencing-element", path.toString());
        }
    }

    private static class Id {
        private Object item;
        private Path path;

        public Id(Object item, Path path) {
            this.item = item;
            this.path = path;
        }

        protected Object getItem() {
            return this.item;
        }

        protected Path getPath() {
            return this.path;
        }
    }
}

