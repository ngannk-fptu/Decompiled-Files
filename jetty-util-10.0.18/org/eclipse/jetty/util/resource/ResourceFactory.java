/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.resource;

import java.io.IOException;
import org.eclipse.jetty.util.resource.Resource;

public interface ResourceFactory {
    public Resource getResource(String var1) throws IOException;
}

