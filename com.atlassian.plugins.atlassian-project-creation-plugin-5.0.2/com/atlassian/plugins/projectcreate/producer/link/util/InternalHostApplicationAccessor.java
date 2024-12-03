/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.sal.api.component.ComponentLocator
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.projectcreate.producer.link.util;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.sal.api.component.ComponentLocator;
import java.util.Iterator;
import org.springframework.stereotype.Component;

@Component
public class InternalHostApplicationAccessor {
    public InternalHostApplication get() {
        Iterator components = ComponentLocator.getComponents(InternalHostApplication.class).iterator();
        return components.hasNext() ? (InternalHostApplication)components.next() : null;
    }
}

