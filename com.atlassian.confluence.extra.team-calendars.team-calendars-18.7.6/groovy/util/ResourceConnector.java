/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.util.ResourceException;
import java.net.URLConnection;

public interface ResourceConnector {
    public URLConnection getResourceConnection(String var1) throws ResourceException;
}

