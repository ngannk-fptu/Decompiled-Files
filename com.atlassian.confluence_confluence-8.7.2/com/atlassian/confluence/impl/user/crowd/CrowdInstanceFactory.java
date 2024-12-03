/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.atlassian.crowd.util.InstanceFactory
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.crowd.util.InstanceFactory;

interface CrowdInstanceFactory
extends InstanceFactory {
    default public Object getInstance(String className) throws ClassNotFoundException {
        return this.getInstance(className, this.getClass().getClassLoader());
    }

    default public Object getInstance(String className, ClassLoader classLoader) throws ClassNotFoundException {
        return this.getInstance(ClassLoaderUtils.loadClass((String)className, (ClassLoader)classLoader));
    }
}

