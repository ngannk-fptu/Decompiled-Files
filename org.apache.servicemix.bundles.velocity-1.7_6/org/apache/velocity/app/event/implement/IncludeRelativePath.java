/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.app.event.implement;

import org.apache.velocity.app.event.IncludeEventHandler;

public class IncludeRelativePath
implements IncludeEventHandler {
    public String includeEvent(String includeResourcePath, String currentResourcePath, String directiveName) {
        if (includeResourcePath.startsWith("/") || includeResourcePath.startsWith("\\")) {
            return includeResourcePath;
        }
        int lastslashpos = Math.max(currentResourcePath.lastIndexOf("/"), currentResourcePath.lastIndexOf("\\"));
        if (lastslashpos == -1) {
            return includeResourcePath;
        }
        return currentResourcePath.substring(0, lastslashpos) + "/" + includeResourcePath;
    }
}

