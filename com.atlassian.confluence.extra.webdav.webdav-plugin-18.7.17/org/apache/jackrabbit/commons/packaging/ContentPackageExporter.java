/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.packaging;

import java.io.OutputStream;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.packaging.ContentPackage;

public interface ContentPackageExporter {
    public void export(ContentPackage var1, OutputStream var2) throws RepositoryException;
}

