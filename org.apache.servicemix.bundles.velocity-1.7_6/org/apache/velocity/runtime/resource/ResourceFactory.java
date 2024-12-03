/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.resource;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.resource.ContentResource;
import org.apache.velocity.runtime.resource.Resource;

public class ResourceFactory {
    public static Resource getResource(String resourceName, int resourceType) {
        Resource resource = null;
        switch (resourceType) {
            case 1: {
                resource = new Template();
                break;
            }
            case 2: {
                resource = new ContentResource();
            }
        }
        return resource;
    }
}

