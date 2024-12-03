/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.http;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;

class EmptyHttpFields
extends HttpFields.Immutable {
    public EmptyHttpFields() {
        super(new HttpField[0]);
    }

    @Override
    public Iterator<HttpField> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public void forEach(Consumer<? super HttpField> action) {
    }

    @Override
    public Stream<HttpField> stream() {
        return Stream.empty();
    }
}

