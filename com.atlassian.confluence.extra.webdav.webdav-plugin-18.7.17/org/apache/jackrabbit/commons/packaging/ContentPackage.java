/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.packaging;

import java.util.Iterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public interface ContentPackage {
    public Iterator getItems(Session var1) throws RepositoryException;
}

