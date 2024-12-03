/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Index$Builder
 *  org.eclipse.jetty.util.Index$Mutable
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 *  org.eclipse.jetty.util.component.Dumpable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.http.pathmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import org.eclipse.jetty.http.pathmap.MappedResource;
import org.eclipse.jetty.http.pathmap.MatchedPath;
import org.eclipse.jetty.http.pathmap.MatchedResource;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.http.pathmap.PathSpecGroup;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject(value="Path Mappings")
public class PathMappings<E>
implements Iterable<MappedResource<E>>,
Dumpable {
    private static final Logger LOG = LoggerFactory.getLogger(PathMappings.class);
    private static final int PREFIX_TAIL_LEN = 3;
    private final Set<MappedResource<E>> _mappings = new TreeSet<MappedResource>(Comparator.comparing(MappedResource::getPathSpec));
    private boolean _orderIsSignificant;
    private boolean _optimizedExact = true;
    private final Map<String, MappedResource<E>> _exactMap = new HashMap<String, MappedResource<E>>();
    private boolean _optimizedPrefix = true;
    private final Index.Mutable<MappedResource<E>> _prefixMap = new Index.Builder().caseSensitive(true).mutable().build();
    private boolean _optimizedSuffix = true;
    private final Index.Mutable<MappedResource<E>> _suffixMap = new Index.Builder().caseSensitive(true).mutable().build();
    private MappedResource<E> _servletRoot;
    private MappedResource<E> _servletDefault;

    public String dump() {
        return Dumpable.dump((Dumpable)this);
    }

    public void dump(Appendable out, String indent) throws IOException {
        Dumpable.dumpObjects((Appendable)out, (String)indent, (Object)this.toString(), (Object[])new Object[]{this._mappings});
    }

    @ManagedAttribute(value="mappings", readonly=true)
    public List<MappedResource<E>> getMappings() {
        return new ArrayList<MappedResource<E>>(this._mappings);
    }

    public int size() {
        return this._mappings.size();
    }

    public void reset() {
        this._mappings.clear();
        this._prefixMap.clear();
        this._suffixMap.clear();
        this._optimizedExact = true;
        this._optimizedPrefix = true;
        this._optimizedSuffix = true;
        this._orderIsSignificant = false;
        this._servletRoot = null;
        this._servletDefault = null;
    }

    public void removeIf(Predicate<MappedResource<E>> predicate) {
        this._mappings.removeIf(predicate);
    }

    public List<MatchedResource<E>> getMatchedList(String path) {
        ArrayList<MatchedResource<MatchedResource<E>>> ret = new ArrayList<MatchedResource<MatchedResource<E>>>();
        for (MappedResource<E> mr : this._mappings) {
            MatchedPath matchedPath = mr.getPathSpec().matched(path);
            if (matchedPath == null) continue;
            ret.add(new MatchedResource<E>(mr.getResource(), mr.getPathSpec(), matchedPath));
        }
        return ret;
    }

    public List<MappedResource<E>> getMatches(String path) {
        if (this._mappings.isEmpty()) {
            return Collections.emptyList();
        }
        boolean isRootPath = "/".equals(path);
        ArrayList<MappedResource<MappedResource<MappedResource<E>>>> matches = null;
        block4: for (MappedResource<E> mr : this._mappings) {
            switch (mr.getPathSpec().getGroup()) {
                case ROOT: {
                    if (!isRootPath) continue block4;
                    if (matches == null) {
                        matches = new ArrayList<MappedResource<MappedResource<MappedResource<E>>>>();
                    }
                    matches.add(mr);
                    continue block4;
                }
                case DEFAULT: {
                    if (!isRootPath && mr.getPathSpec().matched(path) == null) continue block4;
                    if (matches == null) {
                        matches = new ArrayList();
                    }
                    matches.add(mr);
                    continue block4;
                }
            }
            if (mr.getPathSpec().matched(path) == null) continue;
            if (matches == null) {
                matches = new ArrayList();
            }
            matches.add(mr);
        }
        return matches == null ? Collections.emptyList() : matches;
    }

    public MatchedResource<E> getMatched(String path) {
        if (this._mappings.isEmpty()) {
            return null;
        }
        if (this._orderIsSignificant) {
            return this.getMatchedIteratively(path);
        }
        if (this._servletRoot != null && "/".equals(path)) {
            return this._servletRoot.getPreMatched();
        }
        MappedResource<E> exact = this._exactMap.get(path);
        if (exact != null) {
            return exact.getPreMatched();
        }
        MappedResource prefix = (MappedResource)this._prefixMap.getBest(path);
        while (prefix != null) {
            PathSpec pathSpec = prefix.getPathSpec();
            MatchedPath matchedPath = pathSpec.matched(path);
            if (matchedPath != null) {
                return new MatchedResource(prefix.getResource(), pathSpec, matchedPath);
            }
            int specLength = pathSpec.getSpecLength();
            prefix = specLength > 3 ? (MappedResource)this._prefixMap.getBest(path, 0, specLength - 3) : null;
        }
        if (!this._suffixMap.isEmpty()) {
            int i = Math.max(0, path.lastIndexOf("/"));
            while ((i = path.indexOf(46, i + 1)) > 0) {
                MatchedPath matchedPath;
                MappedResource suffix = (MappedResource)this._suffixMap.get(path, i + 1, path.length() - i - 1);
                if (suffix == null || (matchedPath = suffix.getPathSpec().matched(path)) == null) continue;
                return new MatchedResource(suffix.getResource(), suffix.getPathSpec(), matchedPath);
            }
        }
        if (this._servletDefault != null) {
            return new MatchedResource<E>(this._servletDefault.getResource(), this._servletDefault.getPathSpec(), this._servletDefault.getPathSpec().matched(path));
        }
        return null;
    }

    private MatchedResource<E> getMatchedIteratively(String path) {
        PathSpecGroup lastGroup = null;
        boolean skipRestOfGroup = false;
        for (MappedResource<E> mr : this._mappings) {
            MatchedPath matchedPath;
            PathSpecGroup group = mr.getPathSpec().getGroup();
            if (group == lastGroup && skipRestOfGroup) continue;
            if (group != lastGroup) {
                skipRestOfGroup = false;
                switch (group) {
                    case EXACT: {
                        if (!this._optimizedExact) break;
                        MappedResource<E> exact = this._exactMap.get(path);
                        if (exact != null) {
                            return exact.getPreMatched();
                        }
                        skipRestOfGroup = true;
                        break;
                    }
                    case PREFIX_GLOB: {
                        if (!this._optimizedPrefix) break;
                        MappedResource prefix = (MappedResource)this._prefixMap.getBest(path);
                        while (prefix != null) {
                            PathSpec pathSpec = prefix.getPathSpec();
                            matchedPath = pathSpec.matched(path);
                            if (matchedPath != null) {
                                return new MatchedResource(prefix.getResource(), pathSpec, matchedPath);
                            }
                            int specLength = pathSpec.getSpecLength();
                            prefix = specLength > 3 ? (MappedResource)this._prefixMap.getBest(path, 0, specLength - 3) : null;
                        }
                        skipRestOfGroup = true;
                        break;
                    }
                    case SUFFIX_GLOB: {
                        if (!this._optimizedSuffix) break;
                        int i = 0;
                        while ((i = path.indexOf(46, i + 1)) > 0) {
                            MappedResource suffix = (MappedResource)this._suffixMap.get(path, i + 1, path.length() - i - 1);
                            if (suffix == null || (matchedPath = suffix.getPathSpec().matched(path)) == null) continue;
                            return new MatchedResource(suffix.getResource(), suffix.getPathSpec(), matchedPath);
                        }
                        skipRestOfGroup = true;
                        break;
                    }
                }
            }
            if ((matchedPath = mr.getPathSpec().matched(path)) != null) {
                return new MatchedResource<E>(mr.getResource(), mr.getPathSpec(), matchedPath);
            }
            lastGroup = group;
        }
        return null;
    }

    @Deprecated(forRemoval=true)
    public MappedResource<E> getMatch(String path) {
        MatchedResource<E> matchedPath = this.getMatched(path);
        return new MappedResource<E>(matchedPath.getPathSpec(), matchedPath.getResource());
    }

    @Override
    public Iterator<MappedResource<E>> iterator() {
        return this._mappings.iterator();
    }

    @Deprecated(forRemoval=true)
    public static PathSpec asPathSpec(String pathSpecString) {
        return PathSpec.from(pathSpecString);
    }

    public E get(PathSpec spec) {
        return this._mappings.stream().filter(mappedResource -> mappedResource.getPathSpec().equals(spec)).map(MappedResource::getResource).findFirst().orElse(null);
    }

    public boolean put(String pathSpecString, E resource) {
        return this.put(PathSpec.from(pathSpecString), resource);
    }

    public boolean put(PathSpec pathSpec, E resource) {
        MappedResource<E> entry = new MappedResource<E>(pathSpec, resource);
        boolean added = this._mappings.add(entry);
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} {} to {}", new Object[]{added ? "Added" : "Ignored", entry, this});
        }
        if (added) {
            switch (pathSpec.getGroup()) {
                case EXACT: {
                    if (pathSpec instanceof ServletPathSpec) {
                        String exact = pathSpec.getDeclaration();
                        if (exact == null) break;
                        this._exactMap.put(exact, entry);
                        break;
                    }
                    this._optimizedExact = false;
                    this._orderIsSignificant = true;
                    break;
                }
                case PREFIX_GLOB: {
                    if (pathSpec instanceof ServletPathSpec) {
                        String prefix = pathSpec.getPrefix();
                        if (prefix == null) break;
                        this._prefixMap.put(prefix, entry);
                        break;
                    }
                    this._optimizedPrefix = false;
                    this._orderIsSignificant = true;
                    break;
                }
                case SUFFIX_GLOB: {
                    if (pathSpec instanceof ServletPathSpec) {
                        String suffix = pathSpec.getSuffix();
                        if (suffix == null) break;
                        this._suffixMap.put(suffix, entry);
                        break;
                    }
                    this._optimizedSuffix = false;
                    this._orderIsSignificant = true;
                    break;
                }
                case ROOT: {
                    if (pathSpec instanceof ServletPathSpec) {
                        if (this._servletRoot != null) break;
                        this._servletRoot = entry;
                        break;
                    }
                    this._orderIsSignificant = true;
                    break;
                }
                case DEFAULT: {
                    if (pathSpec instanceof ServletPathSpec) {
                        if (this._servletDefault != null) break;
                        this._servletDefault = entry;
                        break;
                    }
                    this._orderIsSignificant = true;
                    break;
                }
            }
        }
        return added;
    }

    public boolean remove(PathSpec pathSpec) {
        Iterator<MappedResource<E>> iter = this._mappings.iterator();
        boolean removed = false;
        while (iter.hasNext()) {
            if (!iter.next().getPathSpec().equals(pathSpec)) continue;
            removed = true;
            iter.remove();
            break;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} {} to {}", new Object[]{removed ? "Removed" : "Ignored", pathSpec, this});
        }
        if (removed) {
            switch (pathSpec.getGroup()) {
                case EXACT: {
                    String exact = pathSpec.getDeclaration();
                    if (exact == null) break;
                    this._exactMap.remove(exact);
                    this._optimizedExact = this.canBeOptimized(PathSpecGroup.EXACT);
                    this._orderIsSignificant = this.nonServletPathSpec();
                    break;
                }
                case PREFIX_GLOB: {
                    String prefix = pathSpec.getPrefix();
                    if (prefix == null) break;
                    this._prefixMap.remove(prefix);
                    this._optimizedPrefix = this.canBeOptimized(PathSpecGroup.PREFIX_GLOB);
                    this._orderIsSignificant = this.nonServletPathSpec();
                    break;
                }
                case SUFFIX_GLOB: {
                    String suffix = pathSpec.getSuffix();
                    if (suffix == null) break;
                    this._suffixMap.remove(suffix);
                    this._optimizedSuffix = this.canBeOptimized(PathSpecGroup.SUFFIX_GLOB);
                    this._orderIsSignificant = this.nonServletPathSpec();
                    break;
                }
                case ROOT: {
                    this._servletRoot = this._mappings.stream().filter(mapping -> mapping.getPathSpec().getGroup() == PathSpecGroup.ROOT).filter(mapping -> mapping.getPathSpec() instanceof ServletPathSpec).findFirst().orElse(null);
                    this._orderIsSignificant = this.nonServletPathSpec();
                    break;
                }
                case DEFAULT: {
                    this._servletDefault = this._mappings.stream().filter(mapping -> mapping.getPathSpec().getGroup() == PathSpecGroup.DEFAULT).filter(mapping -> mapping.getPathSpec() instanceof ServletPathSpec).findFirst().orElse(null);
                    this._orderIsSignificant = this.nonServletPathSpec();
                }
            }
        }
        return removed;
    }

    private boolean canBeOptimized(PathSpecGroup suffixGlob) {
        return this._mappings.stream().filter(mapping -> mapping.getPathSpec().getGroup() == suffixGlob).allMatch(mapping -> mapping.getPathSpec() instanceof ServletPathSpec);
    }

    private boolean nonServletPathSpec() {
        return this._mappings.stream().allMatch(mapping -> mapping.getPathSpec() instanceof ServletPathSpec);
    }

    public String toString() {
        return String.format("%s[size=%d]", this.getClass().getSimpleName(), this._mappings.size());
    }
}

