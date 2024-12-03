/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.util.ClassLoaderUtil
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
 */
package org.apache.struts2.views.velocity;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import java.io.InputStream;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class StrutsResourceLoader
extends ClasspathResourceLoader {
    public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
        if (name == null || name.length() == 0) {
            throw new ResourceNotFoundException("No template name provided");
        }
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        try {
            return ClassLoaderUtil.getResourceAsStream((String)name, StrutsResourceLoader.class);
        }
        catch (Exception e) {
            throw new ResourceNotFoundException((Throwable)e);
        }
    }
}

