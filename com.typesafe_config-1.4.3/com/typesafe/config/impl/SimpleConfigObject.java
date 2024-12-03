/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.Container;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveContext;
import com.typesafe.config.impl.ResolveResult;
import com.typesafe.config.impl.ResolveSource;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SerializedConfigValue;
import com.typesafe.config.impl.SimpleConfig;
import com.typesafe.config.impl.SimpleConfigOrigin;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class SimpleConfigObject
extends AbstractConfigObject
implements Serializable {
    private static final long serialVersionUID = 2L;
    private final Map<String, AbstractConfigValue> value;
    private final boolean resolved;
    private final boolean ignoresFallbacks;
    private static final String EMPTY_NAME = "empty config";
    private static final SimpleConfigObject emptyInstance = SimpleConfigObject.empty(SimpleConfigOrigin.newSimple("empty config"));

    SimpleConfigObject(ConfigOrigin origin, Map<String, AbstractConfigValue> value, ResolveStatus status, boolean ignoresFallbacks) {
        super(origin);
        if (value == null) {
            throw new ConfigException.BugOrBroken("creating config object with null map");
        }
        this.value = value;
        this.resolved = status == ResolveStatus.RESOLVED;
        this.ignoresFallbacks = ignoresFallbacks;
        if (status != ResolveStatus.fromValues(value.values())) {
            throw new ConfigException.BugOrBroken("Wrong resolved status on " + this);
        }
    }

    SimpleConfigObject(ConfigOrigin origin, Map<String, AbstractConfigValue> value) {
        this(origin, value, ResolveStatus.fromValues(value.values()), false);
    }

    @Override
    public SimpleConfigObject withOnlyKey(String key) {
        return this.withOnlyPath(Path.newKey(key));
    }

    @Override
    public SimpleConfigObject withoutKey(String key) {
        return this.withoutPath(Path.newKey(key));
    }

    @Override
    protected SimpleConfigObject withOnlyPathOrNull(Path path) {
        String key = path.first();
        Path next = path.remainder();
        AbstractConfigValue v = this.value.get(key);
        if (next != null) {
            v = v != null && v instanceof AbstractConfigObject ? ((AbstractConfigObject)v).withOnlyPathOrNull(next) : null;
        }
        if (v == null) {
            return null;
        }
        return new SimpleConfigObject(this.origin(), Collections.singletonMap(key, v), v.resolveStatus(), this.ignoresFallbacks);
    }

    @Override
    SimpleConfigObject withOnlyPath(Path path) {
        SimpleConfigObject o = this.withOnlyPathOrNull(path);
        if (o == null) {
            return new SimpleConfigObject(this.origin(), Collections.emptyMap(), ResolveStatus.RESOLVED, this.ignoresFallbacks);
        }
        return o;
    }

    @Override
    SimpleConfigObject withoutPath(Path path) {
        String key = path.first();
        Path next = path.remainder();
        AbstractConfigValue v = this.value.get(key);
        if (v != null && next != null && v instanceof AbstractConfigObject) {
            v = ((AbstractConfigObject)v).withoutPath(next);
            HashMap<String, AbstractConfigValue> updated = new HashMap<String, AbstractConfigValue>(this.value);
            updated.put(key, v);
            return new SimpleConfigObject(this.origin(), updated, ResolveStatus.fromValues(updated.values()), this.ignoresFallbacks);
        }
        if (next != null || v == null) {
            return this;
        }
        HashMap<String, AbstractConfigValue> smaller = new HashMap<String, AbstractConfigValue>(this.value.size() - 1);
        for (Map.Entry<String, AbstractConfigValue> old : this.value.entrySet()) {
            if (old.getKey().equals(key)) continue;
            smaller.put(old.getKey(), old.getValue());
        }
        return new SimpleConfigObject(this.origin(), smaller, ResolveStatus.fromValues(smaller.values()), this.ignoresFallbacks);
    }

    @Override
    public SimpleConfigObject withValue(String key, ConfigValue v) {
        Map<String, AbstractConfigValue> newMap;
        if (v == null) {
            throw new ConfigException.BugOrBroken("Trying to store null ConfigValue in a ConfigObject");
        }
        if (this.value.isEmpty()) {
            newMap = Collections.singletonMap(key, (AbstractConfigValue)v);
        } else {
            newMap = new HashMap<String, AbstractConfigValue>(this.value);
            newMap.put(key, (AbstractConfigValue)v);
        }
        return new SimpleConfigObject(this.origin(), newMap, ResolveStatus.fromValues(newMap.values()), this.ignoresFallbacks);
    }

    @Override
    SimpleConfigObject withValue(Path path, ConfigValue v) {
        String key = path.first();
        Path next = path.remainder();
        if (next == null) {
            return this.withValue(key, v);
        }
        AbstractConfigValue child = this.value.get(key);
        if (child != null && child instanceof AbstractConfigObject) {
            return this.withValue(key, (ConfigValue)((AbstractConfigObject)child).withValue(next, v));
        }
        SimpleConfig subtree = ((AbstractConfigValue)v).atPath(SimpleConfigOrigin.newSimple("withValue(" + next.render() + ")"), next);
        return this.withValue(key, (ConfigValue)subtree.root());
    }

    @Override
    protected AbstractConfigValue attemptPeekWithPartialResolve(String key) {
        return this.value.get(key);
    }

    private SimpleConfigObject newCopy(ResolveStatus newStatus, ConfigOrigin newOrigin, boolean newIgnoresFallbacks) {
        return new SimpleConfigObject(newOrigin, this.value, newStatus, newIgnoresFallbacks);
    }

    @Override
    protected SimpleConfigObject newCopy(ResolveStatus newStatus, ConfigOrigin newOrigin) {
        return this.newCopy(newStatus, newOrigin, this.ignoresFallbacks);
    }

    @Override
    protected SimpleConfigObject withFallbacksIgnored() {
        if (this.ignoresFallbacks) {
            return this;
        }
        return this.newCopy(this.resolveStatus(), this.origin(), true);
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.fromBoolean(this.resolved);
    }

    @Override
    public SimpleConfigObject replaceChild(AbstractConfigValue child, AbstractConfigValue replacement) {
        HashMap<String, AbstractConfigValue> newChildren = new HashMap<String, AbstractConfigValue>(this.value);
        for (Map.Entry<String, AbstractConfigValue> old : newChildren.entrySet()) {
            if (old.getValue() != child) continue;
            if (replacement != null) {
                old.setValue(replacement);
            } else {
                newChildren.remove(old.getKey());
            }
            return new SimpleConfigObject(this.origin(), newChildren, ResolveStatus.fromValues(newChildren.values()), this.ignoresFallbacks);
        }
        throw new ConfigException.BugOrBroken("SimpleConfigObject.replaceChild did not find " + child + " in " + this);
    }

    @Override
    public boolean hasDescendant(AbstractConfigValue descendant) {
        for (AbstractConfigValue child : this.value.values()) {
            if (child != descendant) continue;
            return true;
        }
        for (AbstractConfigValue child : this.value.values()) {
            if (!(child instanceof Container) || !((Container)((Object)child)).hasDescendant(descendant)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected boolean ignoresFallbacks() {
        return this.ignoresFallbacks;
    }

    @Override
    public Map<String, Object> unwrapped() {
        HashMap<String, Object> m = new HashMap<String, Object>();
        for (Map.Entry<String, AbstractConfigValue> e : this.value.entrySet()) {
            m.put(e.getKey(), e.getValue().unwrapped());
        }
        return m;
    }

    @Override
    protected SimpleConfigObject mergedWithObject(AbstractConfigObject abstractFallback) {
        this.requireNotIgnoringFallbacks();
        if (!(abstractFallback instanceof SimpleConfigObject)) {
            throw new ConfigException.BugOrBroken("should not be reached (merging non-SimpleConfigObject)");
        }
        SimpleConfigObject fallback = (SimpleConfigObject)abstractFallback;
        boolean changed = false;
        boolean allResolved = true;
        HashMap<String, AbstractConfigValue> merged = new HashMap<String, AbstractConfigValue>();
        HashSet<String> allKeys = new HashSet<String>();
        allKeys.addAll(this.keySet());
        allKeys.addAll(fallback.keySet());
        for (String key : allKeys) {
            AbstractConfigValue first = this.value.get(key);
            AbstractConfigValue second = fallback.value.get(key);
            AbstractConfigValue kept = first == null ? second : (second == null ? first : first.withFallback(second));
            merged.put(key, kept);
            if (first != kept) {
                changed = true;
            }
            if (kept.resolveStatus() != ResolveStatus.UNRESOLVED) continue;
            allResolved = false;
        }
        ResolveStatus newResolveStatus = ResolveStatus.fromBoolean(allResolved);
        boolean newIgnoresFallbacks = fallback.ignoresFallbacks();
        if (changed) {
            return new SimpleConfigObject(SimpleConfigObject.mergeOrigins(this, fallback), merged, newResolveStatus, newIgnoresFallbacks);
        }
        if (newResolveStatus != this.resolveStatus() || newIgnoresFallbacks != this.ignoresFallbacks()) {
            return this.newCopy(newResolveStatus, this.origin(), newIgnoresFallbacks);
        }
        return this;
    }

    private SimpleConfigObject modify(AbstractConfigValue.NoExceptionsModifier modifier) {
        try {
            return this.modifyMayThrow(modifier);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ConfigException.BugOrBroken("unexpected checked exception", e);
        }
    }

    private SimpleConfigObject modifyMayThrow(AbstractConfigValue.Modifier modifier) throws Exception {
        HashMap<String, AbstractConfigValue> changes = null;
        for (String k : this.keySet()) {
            AbstractConfigValue v;
            AbstractConfigValue modified = modifier.modifyChildMayThrow(k, v = this.value.get(k));
            if (modified == v) continue;
            if (changes == null) {
                changes = new HashMap<String, AbstractConfigValue>();
            }
            changes.put(k, modified);
        }
        if (changes == null) {
            return this;
        }
        HashMap<String, AbstractConfigValue> modified = new HashMap<String, AbstractConfigValue>();
        boolean sawUnresolved = false;
        for (String k : this.keySet()) {
            AbstractConfigValue newValue;
            if (changes.containsKey(k)) {
                newValue = (AbstractConfigValue)changes.get(k);
                if (newValue == null) continue;
                modified.put(k, newValue);
                if (newValue.resolveStatus() != ResolveStatus.UNRESOLVED) continue;
                sawUnresolved = true;
                continue;
            }
            newValue = this.value.get(k);
            modified.put(k, newValue);
            if (newValue.resolveStatus() != ResolveStatus.UNRESOLVED) continue;
            sawUnresolved = true;
        }
        return new SimpleConfigObject(this.origin(), modified, sawUnresolved ? ResolveStatus.UNRESOLVED : ResolveStatus.RESOLVED, this.ignoresFallbacks());
    }

    @Override
    ResolveResult<? extends AbstractConfigObject> resolveSubstitutions(ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
        if (this.resolveStatus() == ResolveStatus.RESOLVED) {
            return ResolveResult.make(context, this);
        }
        ResolveSource sourceWithParent = source.pushParent(this);
        try {
            ResolveModifier modifier = new ResolveModifier(context, sourceWithParent);
            SimpleConfigObject value = this.modifyMayThrow(modifier);
            return ResolveResult.make(modifier.context, value).asObjectResult();
        }
        catch (AbstractConfigValue.NotPossibleToResolve e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ConfigException.BugOrBroken("unexpected checked exception", e);
        }
    }

    @Override
    SimpleConfigObject relativized(final Path prefix) {
        return this.modify(new AbstractConfigValue.NoExceptionsModifier(){

            @Override
            public AbstractConfigValue modifyChild(String key, AbstractConfigValue v) {
                return v.relativized(prefix);
            }
        });
    }

    @Override
    protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
        if (this.isEmpty()) {
            sb.append("{}");
        } else {
            int innerIndent;
            boolean outerBraces;
            boolean bl = outerBraces = options.getJson() || !atRoot;
            if (outerBraces) {
                innerIndent = indent + 1;
                sb.append("{");
                if (options.getFormatted()) {
                    sb.append('\n');
                }
            } else {
                innerIndent = indent;
            }
            int separatorCount = 0;
            String[] keys = this.keySet().toArray(new String[this.size()]);
            Arrays.sort(keys, new RenderComparator());
            for (String k : keys) {
                AbstractConfigValue v = this.value.get(k);
                if (options.getOriginComments()) {
                    String[] lines;
                    for (String l : lines = v.origin().description().split("\n")) {
                        SimpleConfigObject.indent(sb, indent + 1, options);
                        sb.append('#');
                        if (!l.isEmpty()) {
                            sb.append(' ');
                        }
                        sb.append(l);
                        sb.append("\n");
                    }
                }
                if (options.getComments()) {
                    for (String comment : v.origin().comments()) {
                        SimpleConfigObject.indent(sb, innerIndent, options);
                        sb.append("#");
                        if (!comment.startsWith(" ")) {
                            sb.append(' ');
                        }
                        sb.append(comment);
                        sb.append("\n");
                    }
                }
                SimpleConfigObject.indent(sb, innerIndent, options);
                v.render(sb, innerIndent, false, k, options);
                if (options.getFormatted()) {
                    if (options.getJson()) {
                        sb.append(",");
                        separatorCount = 2;
                    } else {
                        separatorCount = 1;
                    }
                    sb.append('\n');
                    continue;
                }
                sb.append(",");
                separatorCount = 1;
            }
            sb.setLength(sb.length() - separatorCount);
            if (outerBraces) {
                if (options.getFormatted()) {
                    sb.append('\n');
                    if (outerBraces) {
                        SimpleConfigObject.indent(sb, indent, options);
                    }
                }
                sb.append("}");
            }
        }
        if (atRoot && options.getFormatted()) {
            sb.append('\n');
        }
    }

    @Override
    public AbstractConfigValue get(Object key) {
        return this.value.get(key);
    }

    private static boolean mapEquals(Map<String, ConfigValue> a, Map<String, ConfigValue> b) {
        Set<String> bKeys;
        if (a == b) {
            return true;
        }
        Set<String> aKeys = a.keySet();
        if (!aKeys.equals(bKeys = b.keySet())) {
            return false;
        }
        for (String key : aKeys) {
            if (a.get(key).equals(b.get(key))) continue;
            return false;
        }
        return true;
    }

    private static int mapHash(Map<String, ConfigValue> m) {
        ArrayList<String> keys = new ArrayList<String>();
        keys.addAll(m.keySet());
        Collections.sort(keys);
        int valuesHash = 0;
        for (String k : keys) {
            valuesHash += m.get(k).hashCode();
        }
        return 41 * (41 + keys.hashCode()) + valuesHash;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ConfigObject;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ConfigObject) {
            return this.canEqual(other) && SimpleConfigObject.mapEquals(this, (ConfigObject)other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return SimpleConfigObject.mapHash(this);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.value.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return this.value.keySet();
    }

    @Override
    public boolean containsValue(Object v) {
        return this.value.containsValue(v);
    }

    @Override
    public Set<Map.Entry<String, ConfigValue>> entrySet() {
        HashSet<Map.Entry<String, ConfigValue>> entries = new HashSet<Map.Entry<String, ConfigValue>>();
        for (Map.Entry<String, AbstractConfigValue> e : this.value.entrySet()) {
            entries.add(new AbstractMap.SimpleImmutableEntry<String, AbstractConfigValue>(e.getKey(), e.getValue()));
        }
        return entries;
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public Collection<ConfigValue> values() {
        return new HashSet<ConfigValue>(this.value.values());
    }

    static final SimpleConfigObject empty() {
        return emptyInstance;
    }

    static final SimpleConfigObject empty(ConfigOrigin origin) {
        if (origin == null) {
            return SimpleConfigObject.empty();
        }
        return new SimpleConfigObject(origin, Collections.emptyMap());
    }

    static final SimpleConfigObject emptyMissing(ConfigOrigin baseOrigin) {
        return new SimpleConfigObject(SimpleConfigOrigin.newSimple(baseOrigin.description() + " (not found)"), Collections.emptyMap());
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializedConfigValue(this);
    }

    private static final class RenderComparator
    implements Comparator<String>,
    Serializable {
        private static final long serialVersionUID = 1L;

        private RenderComparator() {
        }

        private static boolean isAllDigits(String s) {
            int length = s.length();
            if (length == 0) {
                return false;
            }
            for (int i = 0; i < length; ++i) {
                char c = s.charAt(i);
                if (Character.isDigit(c)) continue;
                return false;
            }
            return true;
        }

        @Override
        public int compare(String a, String b) {
            boolean aDigits = RenderComparator.isAllDigits(a);
            boolean bDigits = RenderComparator.isAllDigits(b);
            if (aDigits && bDigits) {
                return new BigInteger(a).compareTo(new BigInteger(b));
            }
            if (aDigits) {
                return -1;
            }
            if (bDigits) {
                return 1;
            }
            return a.compareTo(b);
        }
    }

    private static final class ResolveModifier
    implements AbstractConfigValue.Modifier {
        final Path originalRestrict;
        ResolveContext context;
        final ResolveSource source;

        ResolveModifier(ResolveContext context, ResolveSource source) {
            this.context = context;
            this.source = source;
            this.originalRestrict = context.restrictToChild();
        }

        @Override
        public AbstractConfigValue modifyChildMayThrow(String key, AbstractConfigValue v) throws AbstractConfigValue.NotPossibleToResolve {
            if (this.context.isRestrictedToChild()) {
                if (key.equals(this.context.restrictToChild().first())) {
                    Path remainder = this.context.restrictToChild().remainder();
                    if (remainder != null) {
                        ResolveResult<? extends AbstractConfigValue> result = this.context.restrict(remainder).resolve(v, this.source);
                        this.context = result.context.unrestricted().restrict(this.originalRestrict);
                        return result.value;
                    }
                    return v;
                }
                return v;
            }
            ResolveResult<? extends AbstractConfigValue> result = this.context.unrestricted().resolve(v, this.source);
            this.context = result.context.unrestricted().restrict(this.originalRestrict);
            return result.value;
        }
    }
}

