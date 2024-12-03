/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.app.event.implement;

import org.apache.velocity.app.event.IncludeEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.ContextAware;
import org.apache.velocity.util.RuntimeServicesAware;
import org.apache.velocity.util.StringUtils;

public class IncludeNotFound
implements IncludeEventHandler,
RuntimeServicesAware,
ContextAware {
    private static final String DEFAULT_NOT_FOUND = "notfound.vm";
    private static final String PROPERTY_NOT_FOUND = "eventhandler.include.notfound";
    private RuntimeServices rs = null;
    String notfound;
    Context context;

    public String includeEvent(String includeResourcePath, String currentResourcePath, String directiveName) {
        boolean exists;
        boolean bl = exists = this.rs.getLoaderNameForResource(includeResourcePath) != null;
        if (!exists) {
            this.context.put("missingResource", includeResourcePath);
            if (this.rs.getLoaderNameForResource(this.notfound) != null) {
                return this.notfound;
            }
            this.rs.getLog().error("Can't find include not found page: " + this.notfound);
            return null;
        }
        return includeResourcePath;
    }

    public void setRuntimeServices(RuntimeServices rs) {
        this.rs = rs;
        this.notfound = StringUtils.nullTrim(rs.getString(PROPERTY_NOT_FOUND, DEFAULT_NOT_FOUND));
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

