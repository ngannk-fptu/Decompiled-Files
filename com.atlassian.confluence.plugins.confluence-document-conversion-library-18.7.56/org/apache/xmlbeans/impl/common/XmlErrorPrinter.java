/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.common;

import java.net.URI;
import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Iterator;
import org.apache.xmlbeans.XmlError;

public class XmlErrorPrinter
extends AbstractCollection<XmlError> {
    private final boolean _noisy;
    private final URI _baseURI;

    public XmlErrorPrinter(boolean noisy, URI baseURI) {
        this._noisy = noisy;
        this._baseURI = baseURI;
    }

    @Override
    public boolean add(XmlError err) {
        if (err != null) {
            if (err.getSeverity() == 0 || err.getSeverity() == 1) {
                System.err.println(err.toString(this._baseURI));
            } else if (this._noisy) {
                System.out.println(err.toString(this._baseURI));
            }
        }
        return false;
    }

    @Override
    public Iterator<XmlError> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public int size() {
        return 0;
    }
}

