/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.descriptor.web.ContextEnvironment
 *  org.apache.tomcat.util.descriptor.web.ContextResource
 *  org.apache.tomcat.util.descriptor.web.ContextResourceLink
 *  org.apache.tomcat.util.modeler.BaseModelMBean
 *  org.apache.tomcat.util.modeler.ManagedBean
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.mbeans;

import java.util.ArrayList;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.modeler.BaseModelMBean;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class NamingResourcesMBean
extends BaseModelMBean {
    private static final StringManager sm = StringManager.getManager(NamingResourcesMBean.class);
    protected final Registry registry = MBeanUtils.createRegistry();
    protected final ManagedBean managed = this.registry.findManagedBean("NamingResources");

    public String[] getEnvironments() {
        ContextEnvironment[] envs = ((NamingResourcesImpl)this.resource).findEnvironments();
        ArrayList<String> results = new ArrayList<String>();
        for (ContextEnvironment env : envs) {
            try {
                ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), env);
                results.add(oname.toString());
            }
            catch (MalformedObjectNameException e) {
                throw new IllegalArgumentException(sm.getString("namingResourcesMBean.createObjectNameError.environment", new Object[]{env}), e);
            }
        }
        return results.toArray(new String[0]);
    }

    public String[] getResources() {
        ContextResource[] resources = ((NamingResourcesImpl)this.resource).findResources();
        ArrayList<String> results = new ArrayList<String>();
        for (ContextResource contextResource : resources) {
            try {
                ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), contextResource);
                results.add(oname.toString());
            }
            catch (MalformedObjectNameException e) {
                throw new IllegalArgumentException(sm.getString("namingResourcesMBean.createObjectNameError.resource", new Object[]{contextResource}), e);
            }
        }
        return results.toArray(new String[0]);
    }

    public String[] getResourceLinks() {
        ContextResourceLink[] resourceLinks = ((NamingResourcesImpl)this.resource).findResourceLinks();
        ArrayList<String> results = new ArrayList<String>();
        for (ContextResourceLink resourceLink : resourceLinks) {
            try {
                ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), resourceLink);
                results.add(oname.toString());
            }
            catch (MalformedObjectNameException e) {
                throw new IllegalArgumentException(sm.getString("namingResourcesMBean.createObjectNameError.resourceLink", new Object[]{resourceLink}), e);
            }
        }
        return results.toArray(new String[0]);
    }

    public String addEnvironment(String envName, String type, String value) throws MalformedObjectNameException {
        NamingResourcesImpl nresources = (NamingResourcesImpl)this.resource;
        if (nresources == null) {
            return null;
        }
        ContextEnvironment env = nresources.findEnvironment(envName);
        if (env != null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.addAlreadyExists.environment", new Object[]{envName}));
        }
        env = new ContextEnvironment();
        env.setName(envName);
        env.setType(type);
        env.setValue(value);
        nresources.addEnvironment(env);
        ManagedBean managed = this.registry.findManagedBean("ContextEnvironment");
        ObjectName oname = MBeanUtils.createObjectName(managed.getDomain(), env);
        return oname.toString();
    }

    public String addResource(String resourceName, String type) throws MalformedObjectNameException {
        NamingResourcesImpl nresources = (NamingResourcesImpl)this.resource;
        if (nresources == null) {
            return null;
        }
        ContextResource resource = nresources.findResource(resourceName);
        if (resource != null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.addAlreadyExists.resource", new Object[]{resourceName}));
        }
        resource = new ContextResource();
        resource.setName(resourceName);
        resource.setType(type);
        nresources.addResource(resource);
        ManagedBean managed = this.registry.findManagedBean("ContextResource");
        ObjectName oname = MBeanUtils.createObjectName(managed.getDomain(), resource);
        return oname.toString();
    }

    public String addResourceLink(String resourceLinkName, String type) throws MalformedObjectNameException {
        NamingResourcesImpl nresources = (NamingResourcesImpl)this.resource;
        if (nresources == null) {
            return null;
        }
        ContextResourceLink resourceLink = nresources.findResourceLink(resourceLinkName);
        if (resourceLink != null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.addAlreadyExists.resourceLink", new Object[]{resourceLinkName}));
        }
        resourceLink = new ContextResourceLink();
        resourceLink.setName(resourceLinkName);
        resourceLink.setType(type);
        nresources.addResourceLink(resourceLink);
        ManagedBean managed = this.registry.findManagedBean("ContextResourceLink");
        ObjectName oname = MBeanUtils.createObjectName(managed.getDomain(), resourceLink);
        return oname.toString();
    }

    public void removeEnvironment(String envName) {
        NamingResourcesImpl nresources = (NamingResourcesImpl)this.resource;
        if (nresources == null) {
            return;
        }
        ContextEnvironment env = nresources.findEnvironment(envName);
        if (env == null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.removeNotFound.environment", new Object[]{envName}));
        }
        nresources.removeEnvironment(envName);
    }

    public void removeResource(String resourceName) {
        resourceName = ObjectName.unquote(resourceName);
        NamingResourcesImpl nresources = (NamingResourcesImpl)this.resource;
        if (nresources == null) {
            return;
        }
        ContextResource resource = nresources.findResource(resourceName);
        if (resource == null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.removeNotFound.resource", new Object[]{resourceName}));
        }
        nresources.removeResource(resourceName);
    }

    public void removeResourceLink(String resourceLinkName) {
        resourceLinkName = ObjectName.unquote(resourceLinkName);
        NamingResourcesImpl nresources = (NamingResourcesImpl)this.resource;
        if (nresources == null) {
            return;
        }
        ContextResourceLink resourceLink = nresources.findResourceLink(resourceLinkName);
        if (resourceLink == null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.removeNotFound.resourceLink", new Object[]{resourceLinkName}));
        }
        nresources.removeResourceLink(resourceLinkName);
    }
}

