/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SchemaDependencies {
    private final Map<String, List<String>> _contributions = new HashMap<String, List<String>>();
    private final Map<String, Set<String>> _dependencies = new HashMap<String, Set<String>>();

    void registerDependency(String source, String target) {
        this._dependencies.computeIfAbsent(target, k -> new HashSet()).add(source);
    }

    Set<String> computeTransitiveClosure(List<String> modifiedNamespaces) {
        ArrayList<String> nsList = new ArrayList<String>(modifiedNamespaces);
        HashSet<String> result = new HashSet<String>(modifiedNamespaces);
        for (int i = 0; i < nsList.size(); ++i) {
            Set<String> deps = this._dependencies.get(nsList.get(i));
            if (deps == null) continue;
            for (String ns : deps) {
                if (result.contains(ns)) continue;
                nsList.add(ns);
                result.add(ns);
            }
        }
        return result;
    }

    SchemaDependencies() {
    }

    SchemaDependencies(SchemaDependencies base, Set<String> updatedNs) {
        for (String target : base._dependencies.keySet()) {
            if (updatedNs.contains(target)) continue;
            HashSet<String> depSet = new HashSet<String>();
            this._dependencies.put(target, depSet);
            Set<String> baseDepSet = base._dependencies.get(target);
            for (String source : baseDepSet) {
                if (updatedNs.contains(source)) continue;
                depSet.add(source);
            }
        }
        for (String ns : base._contributions.keySet()) {
            if (updatedNs.contains(ns)) continue;
            ArrayList fileList = new ArrayList();
            this._contributions.put(ns, fileList);
            fileList.addAll(base._contributions.get(ns));
        }
    }

    void registerContribution(String ns, String fileURL) {
        this._contributions.computeIfAbsent(ns, k -> new ArrayList()).add(fileURL);
    }

    boolean isFileRepresented(String fileURL) {
        return this._contributions.values().stream().anyMatch(l -> l.contains(fileURL));
    }

    List<String> getFilesTouched(Set<String> updatedNs) {
        return updatedNs.stream().map(this._contributions::get).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
    }

    List<String> getNamespacesTouched(Set<String> modifiedFiles) {
        return this._contributions.entrySet().stream().filter(e -> ((List)e.getValue()).stream().anyMatch(modifiedFiles::contains)).map(Map.Entry::getKey).collect(Collectors.toList());
    }
}

