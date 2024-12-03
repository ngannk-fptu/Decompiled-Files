/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm.internal;

import java.util.List;
import org.aspectj.asm.IRelationship;

public class Relationship
implements IRelationship {
    private static final long serialVersionUID = 3855166397957609120L;
    private String name;
    private IRelationship.Kind kind;
    private boolean isAffects;
    private String sourceHandle;
    private List<String> targets;
    private boolean hasRuntimeTest;

    public Relationship(String name, IRelationship.Kind kind, String sourceHandle, List<String> targets, boolean runtimeTest) {
        this.name = name;
        this.isAffects = name.equals("advises") || name.equals("declares on") || name.equals("softens") || name.equals("matched by") || name.equals("declared on") || name.equals("annotates");
        this.kind = kind;
        this.sourceHandle = sourceHandle;
        this.targets = targets;
        this.hasRuntimeTest = runtimeTest;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public IRelationship.Kind getKind() {
        return this.kind;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSourceHandle() {
        return this.sourceHandle;
    }

    @Override
    public List<String> getTargets() {
        return this.targets;
    }

    @Override
    public void addTarget(String handle) {
        if (this.targets.contains(handle)) {
            return;
        }
        this.targets.add(handle);
    }

    @Override
    public boolean hasRuntimeTest() {
        return this.hasRuntimeTest;
    }

    @Override
    public boolean isAffects() {
        return this.isAffects;
    }
}

