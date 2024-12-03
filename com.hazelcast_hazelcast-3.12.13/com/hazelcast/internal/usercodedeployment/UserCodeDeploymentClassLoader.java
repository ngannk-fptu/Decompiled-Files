/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment;

import com.hazelcast.internal.usercodedeployment.UserCodeDeploymentService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;

public class UserCodeDeploymentClassLoader
extends ClassLoader {
    private static final ILogger LOG = Logger.getLogger(UserCodeDeploymentClassLoader.class);
    private UserCodeDeploymentService userCodeDeploymentService;

    public UserCodeDeploymentClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void setUserCodeDeploymentService(UserCodeDeploymentService service) {
        this.userCodeDeploymentService = service;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        if (this.userCodeDeploymentService != null) {
            clazz = this.userCodeDeploymentService.findLoadedClass(name);
        }
        if (clazz == null) {
            try {
                return super.loadClass(name, resolve);
            }
            catch (ClassNotFoundException e) {
                if (this.userCodeDeploymentService == null) {
                    LOG.finest("User Code Deployment classloader is not initialized yet. ");
                    throw e;
                }
                clazz = this.userCodeDeploymentService.handleClassNotFoundException(name);
                if (resolve) {
                    this.resolveClass(clazz);
                }
                return clazz;
            }
        }
        return clazz;
    }
}

