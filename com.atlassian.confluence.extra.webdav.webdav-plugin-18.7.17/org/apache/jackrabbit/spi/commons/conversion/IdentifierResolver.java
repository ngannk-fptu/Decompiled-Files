/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;

public interface IdentifierResolver {
    public Path getPath(String var1) throws MalformedPathException;

    public void checkFormat(String var1) throws MalformedPathException;
}

