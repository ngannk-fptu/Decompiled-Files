/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view.servlet;

import org.apache.velocity.tools.view.ViewToolInfo;

@Deprecated
public class ServletToolInfo
extends ViewToolInfo {
    private String scope;
    private boolean exactPath;
    private String path;

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return this.scope;
    }

    public void setRequestPath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.equals("/*")) {
            this.path = null;
        } else if (path.endsWith("*")) {
            this.exactPath = false;
            this.path = path.substring(0, path.length() - 1);
        } else {
            this.exactPath = true;
            this.path = path;
        }
    }

    public String getRequestPath() {
        return this.path;
    }

    public boolean allowsRequestPath(String requestedPath) {
        if (this.path == null) {
            return true;
        }
        if (this.exactPath) {
            return this.path.equals(requestedPath);
        }
        if (requestedPath != null) {
            return requestedPath.startsWith(this.path);
        }
        return false;
    }
}

