/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.repository;

import aQute.bnd.osgi.resource.PersistentResource;

public class PersistentResourcesRepository {

    static class ResourceDTO {
        public String path;
        public long modified;
        public PersistentResource resource;

        ResourceDTO() {
        }
    }
}

