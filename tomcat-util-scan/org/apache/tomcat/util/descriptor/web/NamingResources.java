/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;

public interface NamingResources {
    public void addEnvironment(ContextEnvironment var1);

    public void removeEnvironment(String var1);

    public void addResource(ContextResource var1);

    public void removeResource(String var1);

    public void addResourceLink(ContextResourceLink var1);

    public void removeResourceLink(String var1);

    public Object getContainer();
}

