/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.IRelationshipMap;
import org.aspectj.asm.internal.Relationship;

public class RelationshipMap
extends HashMap<String, List<IRelationship>>
implements IRelationshipMap {
    private static final long serialVersionUID = 496638323566589643L;

    @Override
    public List<IRelationship> get(String handle) {
        List relationships = (List)super.get(handle);
        if (relationships == null) {
            return null;
        }
        return relationships;
    }

    @Override
    public List<IRelationship> get(IProgramElement source) {
        return this.get(source.getHandleIdentifier());
    }

    @Override
    public IRelationship get(String source, IRelationship.Kind kind, String relationshipName, boolean runtimeTest, boolean createIfMissing) {
        List<IRelationship> relationships = this.get(source);
        if (relationships == null) {
            if (!createIfMissing) {
                return null;
            }
            relationships = new ArrayList<IRelationship>();
            Relationship rel = new Relationship(relationshipName, kind, source, new ArrayList<String>(), runtimeTest);
            relationships.add(rel);
            super.put(source, relationships);
            return rel;
        }
        for (IRelationship curr : relationships) {
            if (curr.getKind() != kind || !curr.getName().equals(relationshipName) || curr.hasRuntimeTest() != runtimeTest) continue;
            return curr;
        }
        if (createIfMissing) {
            Relationship rel = new Relationship(relationshipName, kind, source, new ArrayList<String>(), runtimeTest);
            relationships.add(rel);
            return rel;
        }
        return null;
    }

    @Override
    public IRelationship get(IProgramElement source, IRelationship.Kind kind, String relationshipName, boolean runtimeTest, boolean createIfMissing) {
        return this.get(source.getHandleIdentifier(), kind, relationshipName, runtimeTest, createIfMissing);
    }

    @Override
    public IRelationship get(IProgramElement source, IRelationship.Kind kind, String relationshipName) {
        return this.get(source, kind, relationshipName, false, true);
    }

    @Override
    public boolean remove(String source, IRelationship relationship) {
        List list = (List)super.get(source);
        if (list != null) {
            return list.remove(relationship);
        }
        return false;
    }

    @Override
    public void removeAll(String source) {
        super.remove(source);
    }

    @Override
    public void put(String source, IRelationship relationship) {
        ArrayList<IRelationship> existingRelationships = (ArrayList<IRelationship>)super.get(source);
        if (existingRelationships == null) {
            existingRelationships = new ArrayList<IRelationship>();
            existingRelationships.add(relationship);
            super.put(source, existingRelationships);
        } else {
            boolean matched = false;
            for (IRelationship existingRelationship : existingRelationships) {
                if (!existingRelationship.getName().equals(relationship.getName()) || existingRelationship.getKind() != relationship.getKind()) continue;
                existingRelationship.getTargets().addAll(relationship.getTargets());
                matched = true;
            }
            if (matched) {
                System.err.println("matched = true");
            }
            if (matched) {
                existingRelationships.add(relationship);
            }
        }
    }

    @Override
    public void put(IProgramElement source, IRelationship relationship) {
        this.put(source.getHandleIdentifier(), relationship);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public Set<String> getEntries() {
        return this.keySet();
    }
}

