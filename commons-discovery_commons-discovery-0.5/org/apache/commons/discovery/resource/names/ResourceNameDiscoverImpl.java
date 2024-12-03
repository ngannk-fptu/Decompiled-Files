/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.resource.names;

import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;

public abstract class ResourceNameDiscoverImpl
implements ResourceNameDiscover {
    public abstract ResourceNameIterator findResourceNames(String var1);

    public ResourceNameIterator findResourceNames(final ResourceNameIterator inputNames) {
        return new ResourceNameIterator(){
            private ResourceNameIterator resourceNames = null;
            private String resourceName = null;

            public boolean hasNext() {
                if (this.resourceName == null) {
                    this.resourceName = this.getNextResourceName();
                }
                return this.resourceName != null;
            }

            public String nextResourceName() {
                String name = this.resourceName;
                this.resourceName = null;
                return name;
            }

            private String getNextResourceName() {
                while (inputNames.hasNext() && (this.resourceNames == null || !this.resourceNames.hasNext())) {
                    this.resourceNames = ResourceNameDiscoverImpl.this.findResourceNames(inputNames.nextResourceName());
                }
                return this.resourceNames != null && this.resourceNames.hasNext() ? this.resourceNames.nextResourceName() : null;
            }
        };
    }
}

