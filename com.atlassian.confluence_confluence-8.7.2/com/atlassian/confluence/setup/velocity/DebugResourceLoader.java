/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.resource.Resource
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.velocity;

import java.io.InputStream;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugResourceLoader
extends ResourceLoader {
    private static final Logger log = LoggerFactory.getLogger(DebugResourceLoader.class);

    public void init(ExtendedProperties configuration) {
        log.warn("");
    }

    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        log.warn(source);
        return null;
    }

    public boolean isSourceModified(Resource resource) {
        return false;
    }

    public long getLastModified(Resource resource) {
        return 0L;
    }
}

