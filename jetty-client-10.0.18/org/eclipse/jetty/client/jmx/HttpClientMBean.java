/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.jmx.ObjectMBean
 */
package org.eclipse.jetty.client.jmx;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.jmx.ObjectMBean;

public class HttpClientMBean
extends ObjectMBean {
    public HttpClientMBean(Object managedObject) {
        super(managedObject);
    }

    public String getObjectContextBasis() {
        HttpClient httpClient = (HttpClient)((Object)this.getManagedObject());
        return httpClient.getName();
    }
}

