/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.util.EventObject;
import org.osgi.framework.ServiceReference;

public class ServiceEvent
extends EventObject {
    static final long serialVersionUID = 8792901483909409299L;
    private final ServiceReference<?> reference;
    private final int type;
    public static final int REGISTERED = 1;
    public static final int MODIFIED = 2;
    public static final int UNREGISTERING = 4;
    public static final int MODIFIED_ENDMATCH = 8;

    public ServiceEvent(int type, ServiceReference<?> reference) {
        super(reference);
        this.reference = reference;
        this.type = type;
    }

    public ServiceReference<?> getServiceReference() {
        return this.reference;
    }

    public int getType() {
        return this.type;
    }
}

