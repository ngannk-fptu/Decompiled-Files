/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.util.introspection.SecureUberspector
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AllowlistSecureIntrospector;
import org.apache.velocity.util.introspection.SecureUberspector;

public class AllowlistSecureUberspector
extends SecureUberspector {
    public void init() throws Exception {
        super.init();
        this.introspector = new AllowlistSecureIntrospector(this.log, this.runtimeServices);
    }
}

