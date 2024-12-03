/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.aop;

import org.eclipse.gemini.blueprint.service.importer.ImportedOsgiServiceProxy;
import org.springframework.beans.factory.DisposableBean;

public class ProxyPlusCallback {
    public final ImportedOsgiServiceProxy proxy;
    public final DisposableBean destructionCallback;

    public ProxyPlusCallback(Object proxy, DisposableBean destructionCallback) {
        this.proxy = (ImportedOsgiServiceProxy)proxy;
        this.destructionCallback = destructionCallback;
    }
}

