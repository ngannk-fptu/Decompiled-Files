/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.bnd.build.Project;
import java.util.Set;

public interface DependencyContributor {
    public void addDependencies(Project var1, Set<String> var2);
}

