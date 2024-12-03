/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.resource;

import java.util.Map;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.resource.Resource;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ConsumerType
public interface Requirement {
    public String getNamespace();

    public Map<String, String> getDirectives();

    public Map<String, Object> getAttributes();

    public Resource getResource();

    public boolean equals(Object var1);

    public int hashCode();
}

