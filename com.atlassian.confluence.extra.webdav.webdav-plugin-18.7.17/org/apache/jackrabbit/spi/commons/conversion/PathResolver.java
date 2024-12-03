/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;

public interface PathResolver {
    public Path getQPath(String var1) throws MalformedPathException, IllegalNameException, NamespaceException;

    public Path getQPath(String var1, boolean var2) throws MalformedPathException, IllegalNameException, NamespaceException;

    public String getJCRPath(Path var1) throws NamespaceException;
}

