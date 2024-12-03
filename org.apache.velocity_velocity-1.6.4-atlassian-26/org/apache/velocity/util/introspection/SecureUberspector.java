/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.util.Iterator;
import org.apache.velocity.util.RuntimeServicesAware;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.SecureIntrospectorControl;
import org.apache.velocity.util.introspection.SecureIntrospectorImpl;
import org.apache.velocity.util.introspection.UberspectImpl;

public class SecureUberspector
extends UberspectImpl
implements RuntimeServicesAware {
    @Override
    public void init() throws Exception {
        super.init();
        String[] badPackages = this.runtimeServices.getConfiguration().getStringArray("introspector.restrict.packages");
        String[] badClasses = this.runtimeServices.getConfiguration().getStringArray("introspector.restrict.classes");
        String[] allowlistClasses = this.runtimeServices.getConfiguration().getStringArray("introspector.allowlist.classes");
        this.introspector = new SecureIntrospectorImpl(badClasses, badPackages, allowlistClasses, this.log, this.runtimeServices);
    }

    @Override
    public Iterator getIterator(Object obj, Info i) throws Exception {
        if (obj != null) {
            SecureIntrospectorControl sic = (SecureIntrospectorControl)((Object)this.introspector);
            if (sic.checkObjectExecutePermission(obj.getClass(), null)) {
                return super.getIterator(obj, i);
            }
            this.log.warn("Cannot retrieve iterator from " + obj.getClass() + " due to security restrictions.");
        }
        return null;
    }
}

