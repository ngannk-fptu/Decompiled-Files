/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractComplexProperty;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.AbstractSimpleProperty;
import org.apache.xmpbox.type.Cardinality;

public class ArrayProperty
extends AbstractComplexProperty {
    private final Cardinality arrayType;
    private final String namespace;
    private final String prefix;

    public ArrayProperty(XMPMetadata metadata, String namespace, String prefix, String propertyName, Cardinality type) {
        super(metadata, propertyName);
        this.arrayType = type;
        this.namespace = namespace;
        this.prefix = prefix;
    }

    public Cardinality getArrayType() {
        return this.arrayType;
    }

    public List<String> getElementsAsString() {
        List<AbstractField> allProperties = this.getContainer().getAllProperties();
        ArrayList<String> retval = new ArrayList<String>(allProperties.size());
        for (AbstractField tmp : allProperties) {
            retval.add(((AbstractSimpleProperty)tmp).getStringValue());
        }
        return Collections.unmodifiableList(retval);
    }

    @Override
    public final String getNamespace() {
        return this.namespace;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }
}

