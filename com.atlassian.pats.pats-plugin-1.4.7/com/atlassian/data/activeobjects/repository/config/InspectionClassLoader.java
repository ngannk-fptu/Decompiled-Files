/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.instrument.classloading.ShadowingClassLoader
 */
package com.atlassian.data.activeobjects.repository.config;

import org.springframework.instrument.classloading.ShadowingClassLoader;

class InspectionClassLoader
extends ShadowingClassLoader {
    InspectionClassLoader(ClassLoader parent) {
        super(parent, true);
        this.excludePackage("org.springframework.");
    }
}

