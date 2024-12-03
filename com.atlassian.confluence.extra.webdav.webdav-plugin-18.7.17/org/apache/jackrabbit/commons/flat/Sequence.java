/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.flat;

import java.util.Iterator;
import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

public interface Sequence<T extends Item>
extends Iterable<T> {
    @Override
    public Iterator<T> iterator();

    public T getItem(String var1) throws AccessDeniedException, PathNotFoundException, ItemNotFoundException, RepositoryException;

    public boolean hasItem(String var1) throws RepositoryException;
}

