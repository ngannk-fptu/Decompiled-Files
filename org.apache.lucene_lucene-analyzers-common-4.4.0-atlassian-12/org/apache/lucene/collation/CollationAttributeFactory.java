/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Attribute
 *  org.apache.lucene.util.AttributeImpl
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.collation;

import java.text.Collator;
import org.apache.lucene.collation.tokenattributes.CollatedTermAttributeImpl;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeSource;

public class CollationAttributeFactory
extends AttributeSource.AttributeFactory {
    private final Collator collator;
    private final AttributeSource.AttributeFactory delegate;

    public CollationAttributeFactory(Collator collator) {
        this(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, collator);
    }

    public CollationAttributeFactory(AttributeSource.AttributeFactory delegate, Collator collator) {
        this.delegate = delegate;
        this.collator = collator;
    }

    public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass) {
        return attClass.isAssignableFrom(CollatedTermAttributeImpl.class) ? new CollatedTermAttributeImpl(this.collator) : this.delegate.createAttributeInstance(attClass);
    }
}

