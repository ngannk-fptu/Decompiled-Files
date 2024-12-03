/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.BaseResourceCollectionContainer;

public class Union
extends BaseResourceCollectionContainer {
    public static Union getInstance(ResourceCollection rc) {
        return rc instanceof Union ? (Union)rc : new Union(rc);
    }

    public Union() {
    }

    public Union(Project project) {
        super(project);
    }

    public Union(ResourceCollection rc) {
        this(Project.getProject(rc), rc);
    }

    public Union(Project project, ResourceCollection rc) {
        super(project);
        this.add(rc);
    }

    public String[] list() {
        if (this.isReference()) {
            return this.getRef().list();
        }
        return (String[])this.streamResources().map(Object::toString).toArray(String[]::new);
    }

    public Resource[] listResources() {
        if (this.isReference()) {
            return this.getRef().listResources();
        }
        return (Resource[])this.streamResources().toArray(Resource[]::new);
    }

    @Override
    protected Collection<Resource> getCollection() {
        return this.getAllResources();
    }

    @Deprecated
    protected <T> Collection<T> getCollection(boolean asString) {
        return asString ? this.getAllToStrings() : this.getAllResources();
    }

    protected Collection<String> getAllToStrings() {
        return this.streamResources(Object::toString).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    protected Set<Resource> getAllResources() {
        return this.streamResources().collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Union getRef() {
        return this.getCheckedRef(Union.class);
    }

    private Stream<? extends Resource> streamResources() {
        return this.streamResources(Function.identity());
    }

    private <T> Stream<? extends T> streamResources(Function<? super Resource, ? extends T> mapper) {
        return this.getResourceCollections().stream().flatMap(ResourceCollection::stream).map(mapper).distinct();
    }
}

