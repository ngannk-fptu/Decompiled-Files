/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 */
package org.eclipse.jetty.http.pathmap;

import org.eclipse.jetty.http.pathmap.MatchedResource;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject(value="Mapped Resource")
public class MappedResource<E>
implements Comparable<MappedResource<E>> {
    private final PathSpec pathSpec;
    private final E resource;
    private final MatchedResource<E> preMatched;

    public MappedResource(PathSpec pathSpec, E resource) {
        MatchedResource<E> matched;
        this.pathSpec = pathSpec;
        this.resource = resource;
        switch (pathSpec.getGroup()) {
            case ROOT: {
                matched = new MatchedResource<E>(resource, pathSpec, pathSpec.matched("/"));
                break;
            }
            case EXACT: {
                matched = new MatchedResource<E>(resource, pathSpec, pathSpec.matched(pathSpec.getDeclaration()));
                break;
            }
            default: {
                matched = null;
            }
        }
        this.preMatched = matched;
    }

    public MatchedResource<E> getPreMatched() {
        return this.preMatched;
    }

    @Override
    public int compareTo(MappedResource<E> other) {
        return this.pathSpec.compareTo(other.pathSpec);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MappedResource other = (MappedResource)obj;
        return !(this.pathSpec == null ? other.pathSpec != null : !this.pathSpec.equals(other.pathSpec));
    }

    @ManagedAttribute(value="path spec", readonly=true)
    public PathSpec getPathSpec() {
        return this.pathSpec;
    }

    @ManagedAttribute(value="resource", readonly=true)
    public E getResource() {
        return this.resource;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.pathSpec == null ? 0 : this.pathSpec.hashCode());
        return result;
    }

    public String toString() {
        return String.format("MappedResource[pathSpec=%s,resource=%s]", this.pathSpec, this.resource);
    }
}

