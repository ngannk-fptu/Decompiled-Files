/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.modzdetector.Modifications
 *  com.atlassian.modzdetector.ModzDetector
 *  com.atlassian.modzdetector.ModzRegistryException
 *  com.atlassian.modzdetector.ResourceAccessor
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.status.service;

import com.atlassian.modzdetector.Modifications;
import com.atlassian.modzdetector.ModzDetector;
import com.atlassian.modzdetector.ModzRegistryException;
import com.atlassian.modzdetector.ResourceAccessor;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import org.apache.struts2.ServletActionContext;

public class HashRegistryCache {
    private final ModzDetector detector;
    private SoftReference<Modifications> ref;

    public HashRegistryCache() {
        this(new ModzDetector(new ResourceAccessor(){

            public InputStream getResourceByPath(String resourceName) {
                if (((String)resourceName).charAt(0) != '/') {
                    resourceName = "/" + (String)resourceName;
                }
                return ServletActionContext.getServletContext().getResourceAsStream((String)resourceName);
            }

            public InputStream getResourceFromClasspath(String resourceName) {
                return this.getClassLoader().getResourceAsStream(resourceName);
            }

            private ClassLoader getClassLoader() {
                return HashRegistryCache.class.getClassLoader();
            }
        }), new SoftReference<Object>(null));
    }

    HashRegistryCache(ModzDetector detector, SoftReference<Modifications> r) {
        this.detector = detector;
        this.ref = r;
    }

    public synchronized Modifications getModifications() throws ModzRegistryException {
        Modifications mods = this.ref.get();
        if (mods == null) {
            mods = this.detector.getModifiedFiles();
            this.ref = new SoftReference<Modifications>(mods);
        }
        return mods;
    }
}

