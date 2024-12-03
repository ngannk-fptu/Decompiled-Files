/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.resource;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;

public interface ResourceManager {
    public static final int RESOURCE_TEMPLATE = 1;
    public static final int RESOURCE_CONTENT = 2;

    public void initialize(RuntimeServices var1);

    public Resource getResource(String var1, int var2, String var3) throws ResourceNotFoundException, ParseErrorException;

    public String getLoaderNameForResource(String var1);
}

