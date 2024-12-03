/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigDelayedMerge;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ReplaceableMergeStack;
import com.typesafe.config.impl.ResolveContext;
import com.typesafe.config.impl.ResolveResult;
import com.typesafe.config.impl.ResolveSource;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.Unmergeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ConfigDelayedMergeObject
extends AbstractConfigObject
implements Unmergeable,
ReplaceableMergeStack {
    private final List<AbstractConfigValue> stack;

    ConfigDelayedMergeObject(ConfigOrigin origin, List<AbstractConfigValue> stack) {
        super(origin);
        this.stack = stack;
        if (stack.isEmpty()) {
            throw new ConfigException.BugOrBroken("creating empty delayed merge object");
        }
        if (!(stack.get(0) instanceof AbstractConfigObject)) {
            throw new ConfigException.BugOrBroken("created a delayed merge object not guaranteed to be an object");
        }
        for (AbstractConfigValue v : stack) {
            if (!(v instanceof ConfigDelayedMerge) && !(v instanceof ConfigDelayedMergeObject)) continue;
            throw new ConfigException.BugOrBroken("placed nested DelayedMerge in a ConfigDelayedMergeObject, should have consolidated stack");
        }
    }

    @Override
    protected ConfigDelayedMergeObject newCopy(ResolveStatus status, ConfigOrigin origin) {
        if (status != this.resolveStatus()) {
            throw new ConfigException.BugOrBroken("attempt to create resolved ConfigDelayedMergeObject");
        }
        return new ConfigDelayedMergeObject(origin, this.stack);
    }

    @Override
    ResolveResult<? extends AbstractConfigObject> resolveSubstitutions(ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
        ResolveResult<? extends AbstractConfigValue> merged = ConfigDelayedMerge.resolveSubstitutions(this, this.stack, context, source);
        return merged.asObjectResult();
    }

    @Override
    public AbstractConfigValue makeReplacement(ResolveContext context, int skipping) {
        return ConfigDelayedMerge.makeReplacement(context, this.stack, skipping);
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.UNRESOLVED;
    }

    @Override
    public AbstractConfigValue replaceChild(AbstractConfigValue child, AbstractConfigValue replacement) {
        List<AbstractConfigValue> newStack = ConfigDelayedMergeObject.replaceChildInList(this.stack, child, replacement);
        if (newStack == null) {
            return null;
        }
        return new ConfigDelayedMergeObject(this.origin(), newStack);
    }

    @Override
    public boolean hasDescendant(AbstractConfigValue descendant) {
        return ConfigDelayedMergeObject.hasDescendantInList(this.stack, descendant);
    }

    @Override
    ConfigDelayedMergeObject relativized(Path prefix) {
        ArrayList<AbstractConfigValue> newStack = new ArrayList<AbstractConfigValue>();
        for (AbstractConfigValue o : this.stack) {
            newStack.add(o.relativized(prefix));
        }
        return new ConfigDelayedMergeObject(this.origin(), newStack);
    }

    @Override
    protected boolean ignoresFallbacks() {
        return ConfigDelayedMerge.stackIgnoresFallbacks(this.stack);
    }

    @Override
    protected final ConfigDelayedMergeObject mergedWithTheUnmergeable(Unmergeable fallback) {
        this.requireNotIgnoringFallbacks();
        return (ConfigDelayedMergeObject)this.mergedWithTheUnmergeable(this.stack, fallback);
    }

    @Override
    protected final ConfigDelayedMergeObject mergedWithObject(AbstractConfigObject fallback) {
        return this.mergedWithNonObject(fallback);
    }

    @Override
    protected final ConfigDelayedMergeObject mergedWithNonObject(AbstractConfigValue fallback) {
        this.requireNotIgnoringFallbacks();
        return (ConfigDelayedMergeObject)this.mergedWithNonObject(this.stack, fallback);
    }

    @Override
    public ConfigDelayedMergeObject withFallback(ConfigMergeable mergeable) {
        return (ConfigDelayedMergeObject)super.withFallback(mergeable);
    }

    @Override
    public ConfigDelayedMergeObject withOnlyKey(String key) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public ConfigDelayedMergeObject withoutKey(String key) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    protected AbstractConfigObject withOnlyPathOrNull(Path path) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    AbstractConfigObject withOnlyPath(Path path) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    AbstractConfigObject withoutPath(Path path) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public ConfigDelayedMergeObject withValue(String key, ConfigValue value) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    ConfigDelayedMergeObject withValue(Path path, ConfigValue value) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    public Collection<AbstractConfigValue> unmergedValues() {
        return this.stack;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ConfigDelayedMergeObject;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ConfigDelayedMergeObject) {
            return this.canEqual(other) && (this.stack == ((ConfigDelayedMergeObject)other).stack || this.stack.equals(((ConfigDelayedMergeObject)other).stack));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.stack.hashCode();
    }

    @Override
    protected void render(StringBuilder sb, int indent, boolean atRoot, String atKey, ConfigRenderOptions options) {
        ConfigDelayedMerge.render(this.stack, sb, indent, atRoot, atKey, options);
    }

    @Override
    protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
        this.render(sb, indent, atRoot, null, options);
    }

    private static ConfigException notResolved() {
        return new ConfigException.NotResolved("need to Config#resolve() before using this object, see the API docs for Config#resolve()");
    }

    @Override
    public Map<String, Object> unwrapped() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public AbstractConfigValue get(Object key) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public boolean containsKey(Object key) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public boolean containsValue(Object value) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public Set<Map.Entry<String, ConfigValue>> entrySet() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public boolean isEmpty() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public Set<String> keySet() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public int size() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public Collection<ConfigValue> values() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    protected AbstractConfigValue attemptPeekWithPartialResolve(String key) {
        for (AbstractConfigValue layer : this.stack) {
            if (layer instanceof AbstractConfigObject) {
                AbstractConfigObject objectLayer = (AbstractConfigObject)layer;
                AbstractConfigValue v = objectLayer.attemptPeekWithPartialResolve(key);
                if (v != null) {
                    if (!v.ignoresFallbacks()) continue;
                    return v;
                }
                if (!(layer instanceof Unmergeable)) continue;
                throw new ConfigException.BugOrBroken("should not be reached: unmergeable object returned null value");
            }
            if (layer instanceof Unmergeable) {
                throw new ConfigException.NotResolved("Key '" + key + "' is not available at '" + this.origin().description() + "' because value at '" + layer.origin().description() + "' has not been resolved and may turn out to contain or hide '" + key + "'. Be sure to Config#resolve() before using a config object.");
            }
            if (layer.resolveStatus() == ResolveStatus.UNRESOLVED) {
                if (!(layer instanceof ConfigList)) {
                    throw new ConfigException.BugOrBroken("Expecting a list here, not " + layer);
                }
                return null;
            }
            if (!layer.ignoresFallbacks()) {
                throw new ConfigException.BugOrBroken("resolved non-object should ignore fallbacks");
            }
            return null;
        }
        throw new ConfigException.BugOrBroken("Delayed merge stack does not contain any unmergeable values");
    }
}

