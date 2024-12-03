/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import java.util.Iterator;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.stax.FOMFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMElementIteratorWrapper
implements Iterator {
    private final Iterator<?> iterator;
    private final FOMFactory factory;

    public FOMElementIteratorWrapper(FOMFactory factory, Iterator<?> iterator) {
        this.iterator = iterator;
        this.factory = factory;
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Object next() {
        return this.factory.getElementWrapper((Element)this.iterator.next());
    }

    @Override
    public void remove() {
        this.iterator.remove();
    }
}

