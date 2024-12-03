/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.ConfigString;
import com.typesafe.config.impl.Container;
import com.typesafe.config.impl.DefaultTransformer;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveContext;
import com.typesafe.config.impl.ResolveResult;
import com.typesafe.config.impl.ResolveSource;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SimpleConfigList;
import com.typesafe.config.impl.SimpleConfigOrigin;
import com.typesafe.config.impl.Unmergeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class ConfigConcatenation
extends AbstractConfigValue
implements Unmergeable,
Container {
    private final List<AbstractConfigValue> pieces;

    ConfigConcatenation(ConfigOrigin origin, List<AbstractConfigValue> pieces) {
        super(origin);
        this.pieces = pieces;
        if (pieces.size() < 2) {
            throw new ConfigException.BugOrBroken("Created concatenation with less than 2 items: " + this);
        }
        boolean hadUnmergeable = false;
        for (AbstractConfigValue p : pieces) {
            if (p instanceof ConfigConcatenation) {
                throw new ConfigException.BugOrBroken("ConfigConcatenation should never be nested: " + this);
            }
            if (!(p instanceof Unmergeable)) continue;
            hadUnmergeable = true;
        }
        if (!hadUnmergeable) {
            throw new ConfigException.BugOrBroken("Created concatenation without an unmergeable in it: " + this);
        }
    }

    private ConfigException.NotResolved notResolved() {
        return new ConfigException.NotResolved("need to Config#resolve(), see the API docs for Config#resolve(); substitution not resolved: " + this);
    }

    @Override
    public ConfigValueType valueType() {
        throw this.notResolved();
    }

    @Override
    public Object unwrapped() {
        throw this.notResolved();
    }

    @Override
    protected ConfigConcatenation newCopy(ConfigOrigin newOrigin) {
        return new ConfigConcatenation(newOrigin, this.pieces);
    }

    @Override
    protected boolean ignoresFallbacks() {
        return false;
    }

    public Collection<ConfigConcatenation> unmergedValues() {
        return Collections.singleton(this);
    }

    private static boolean isIgnoredWhitespace(AbstractConfigValue value) {
        return value instanceof ConfigString && !((ConfigString)value).wasQuoted();
    }

    private static void join(ArrayList<AbstractConfigValue> builder, AbstractConfigValue origRight) {
        AbstractConfigValue left = builder.get(builder.size() - 1);
        AbstractConfigValue right = origRight;
        if (left instanceof ConfigObject && right instanceof SimpleConfigList) {
            left = DefaultTransformer.transform(left, ConfigValueType.LIST);
        } else if (left instanceof SimpleConfigList && right instanceof ConfigObject) {
            right = DefaultTransformer.transform(right, ConfigValueType.LIST);
        }
        AbstractConfigValue joined = null;
        if (left instanceof ConfigObject && right instanceof ConfigObject) {
            joined = right.withFallback(left);
        } else if (left instanceof SimpleConfigList && right instanceof SimpleConfigList) {
            joined = ((SimpleConfigList)left).concatenate((SimpleConfigList)right);
        } else if ((left instanceof SimpleConfigList || left instanceof ConfigObject) && ConfigConcatenation.isIgnoredWhitespace(right)) {
            joined = left;
        } else {
            if (left instanceof ConfigConcatenation || right instanceof ConfigConcatenation) {
                throw new ConfigException.BugOrBroken("unflattened ConfigConcatenation");
            }
            if (!(left instanceof Unmergeable) && !(right instanceof Unmergeable)) {
                String s1 = left.transformToString();
                String s2 = right.transformToString();
                if (s1 == null || s2 == null) {
                    throw new ConfigException.WrongType(left.origin(), "Cannot concatenate object or list with a non-object-or-list, " + left + " and " + right + " are not compatible");
                }
                ConfigOrigin joinedOrigin = SimpleConfigOrigin.mergeOrigins(left.origin(), right.origin());
                joined = new ConfigString.Quoted(joinedOrigin, s1 + s2);
            }
        }
        if (joined == null) {
            builder.add(right);
        } else {
            builder.remove(builder.size() - 1);
            builder.add(joined);
        }
    }

    static List<AbstractConfigValue> consolidate(List<AbstractConfigValue> pieces) {
        if (pieces.size() < 2) {
            return pieces;
        }
        ArrayList<AbstractConfigValue> flattened = new ArrayList<AbstractConfigValue>(pieces.size());
        for (AbstractConfigValue v : pieces) {
            if (v instanceof ConfigConcatenation) {
                flattened.addAll(((ConfigConcatenation)v).pieces);
                continue;
            }
            flattened.add(v);
        }
        ArrayList<AbstractConfigValue> consolidated = new ArrayList<AbstractConfigValue>(flattened.size());
        for (AbstractConfigValue v : flattened) {
            if (consolidated.isEmpty()) {
                consolidated.add(v);
                continue;
            }
            ConfigConcatenation.join(consolidated, v);
        }
        return consolidated;
    }

    static AbstractConfigValue concatenate(List<AbstractConfigValue> pieces) {
        List<AbstractConfigValue> consolidated = ConfigConcatenation.consolidate(pieces);
        if (consolidated.isEmpty()) {
            return null;
        }
        if (consolidated.size() == 1) {
            return consolidated.get(0);
        }
        ConfigOrigin mergedOrigin = SimpleConfigOrigin.mergeOrigins(consolidated);
        return new ConfigConcatenation(mergedOrigin, consolidated);
    }

    @Override
    ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            int indent = context.depth() + 2;
            ConfigImpl.trace(indent - 1, "concatenation has " + this.pieces.size() + " pieces:");
            int count = 0;
            for (AbstractConfigValue abstractConfigValue : this.pieces) {
                ConfigImpl.trace(indent, count + ": " + abstractConfigValue);
                ++count;
            }
        }
        ResolveSource sourceWithParent = source;
        ResolveContext newContext = context;
        ArrayList<AbstractConfigValue> resolved = new ArrayList<AbstractConfigValue>(this.pieces.size());
        for (AbstractConfigValue p : this.pieces) {
            Path restriction = newContext.restrictToChild();
            ResolveResult<? extends AbstractConfigValue> result = newContext.unrestricted().resolve(p, sourceWithParent);
            Object r = result.value;
            newContext = result.context.restrict(restriction);
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(context.depth(), "resolved concat piece to " + r);
            }
            if (r == null) continue;
            resolved.add((AbstractConfigValue)r);
        }
        List<AbstractConfigValue> list = ConfigConcatenation.consolidate(resolved);
        if (list.size() > 1 && context.options().getAllowUnresolved()) {
            return ResolveResult.make(newContext, new ConfigConcatenation(this.origin(), list));
        }
        if (list.isEmpty()) {
            return ResolveResult.make(newContext, null);
        }
        if (list.size() == 1) {
            return ResolveResult.make(newContext, list.get(0));
        }
        throw new ConfigException.BugOrBroken("Bug in the library; resolved list was joined to too many values: " + list);
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.UNRESOLVED;
    }

    @Override
    public ConfigConcatenation replaceChild(AbstractConfigValue child, AbstractConfigValue replacement) {
        List<AbstractConfigValue> newPieces = ConfigConcatenation.replaceChildInList(this.pieces, child, replacement);
        if (newPieces == null) {
            return null;
        }
        return new ConfigConcatenation(this.origin(), newPieces);
    }

    @Override
    public boolean hasDescendant(AbstractConfigValue descendant) {
        return ConfigConcatenation.hasDescendantInList(this.pieces, descendant);
    }

    @Override
    ConfigConcatenation relativized(Path prefix) {
        ArrayList<AbstractConfigValue> newPieces = new ArrayList<AbstractConfigValue>();
        for (AbstractConfigValue p : this.pieces) {
            newPieces.add(p.relativized(prefix));
        }
        return new ConfigConcatenation(this.origin(), newPieces);
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ConfigConcatenation;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ConfigConcatenation) {
            return this.canEqual(other) && this.pieces.equals(((ConfigConcatenation)other).pieces);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.pieces.hashCode();
    }

    @Override
    protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
        for (AbstractConfigValue p : this.pieces) {
            p.render(sb, indent, atRoot, options);
        }
    }
}

