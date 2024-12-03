/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.core.DefaultResourceConfig;
import javax.ws.rs.core.Application;

public class ApplicationAdapter
extends DefaultResourceConfig {
    public ApplicationAdapter(Application ac) {
        if (ac.getClasses() != null) {
            this.getClasses().addAll(ac.getClasses());
        }
        if (ac.getSingletons() != null) {
            this.getSingletons().addAll(ac.getSingletons());
        }
    }
}

