/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.validator.internal.util.StringHelper;

public class ContainerElementTypePath {
    private final List<Integer> nodes;

    private ContainerElementTypePath(List<Integer> nodes) {
        this.nodes = nodes;
    }

    public static ContainerElementTypePath root() {
        return new ContainerElementTypePath(new ArrayList<Integer>());
    }

    public static ContainerElementTypePath of(ContainerElementTypePath parentPath, Integer typeArgumentIndex) {
        ArrayList<Integer> nodes = new ArrayList<Integer>(parentPath.nodes);
        nodes.add(typeArgumentIndex);
        return new ContainerElementTypePath(nodes);
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
        ContainerElementTypePath other = (ContainerElementTypePath)obj;
        return this.nodes.equals(other.nodes);
    }

    public int hashCode() {
        return this.nodes.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(StringHelper.join(this.nodes, ", ")).append("]");
        return sb.toString();
    }
}

