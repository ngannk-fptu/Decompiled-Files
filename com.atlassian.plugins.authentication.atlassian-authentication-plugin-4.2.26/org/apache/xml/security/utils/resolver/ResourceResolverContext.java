/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.utils.resolver;

import java.security.AccessController;
import java.util.Collections;
import java.util.Map;
import org.w3c.dom.Attr;

public class ResourceResolverContext {
    private static boolean allowUnsafeResourceResolving = AccessController.doPrivileged(() -> Boolean.getBoolean("org.apache.xml.security.allowUnsafeResourceResolving"));
    private final Map<String, String> properties;
    public final String uriToResolve;
    public final boolean secureValidation;
    public final String baseUri;
    public final Attr attr;

    public ResourceResolverContext(Attr attr, String baseUri, boolean secureValidation) {
        this(attr, baseUri, secureValidation, Collections.emptyMap());
    }

    public ResourceResolverContext(Attr attr, String baseUri, boolean secureValidation, Map<String, String> properties) {
        this.attr = attr;
        this.baseUri = baseUri;
        this.secureValidation = secureValidation;
        this.uriToResolve = attr != null ? attr.getValue() : null;
        this.properties = Collections.unmodifiableMap(properties != null ? properties : Collections.emptyMap());
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public boolean isURISafeToResolve() {
        if (allowUnsafeResourceResolving) {
            return true;
        }
        if (this.uriToResolve != null) {
            if (this.uriToResolve.startsWith("file:") || this.uriToResolve.startsWith("http:")) {
                return false;
            }
            if (!this.uriToResolve.isEmpty() && this.uriToResolve.charAt(0) != '#' && this.baseUri != null && (this.baseUri.startsWith("file:") || this.baseUri.startsWith("http:"))) {
                return false;
            }
        }
        return true;
    }
}

