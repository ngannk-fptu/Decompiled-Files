/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigDelayedMergeObject;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ReplaceableMergeStack;
import com.typesafe.config.impl.ResolveContext;
import com.typesafe.config.impl.ResolveResult;
import com.typesafe.config.impl.ResolveSource;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.Unmergeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class ConfigDelayedMerge
extends AbstractConfigValue
implements Unmergeable,
ReplaceableMergeStack {
    private final List<AbstractConfigValue> stack;

    ConfigDelayedMerge(ConfigOrigin origin, List<AbstractConfigValue> stack) {
        super(origin);
        this.stack = stack;
        if (stack.isEmpty()) {
            throw new ConfigException.BugOrBroken("creating empty delayed merge value");
        }
        for (AbstractConfigValue v : stack) {
            if (!(v instanceof ConfigDelayedMerge) && !(v instanceof ConfigDelayedMergeObject)) continue;
            throw new ConfigException.BugOrBroken("placed nested DelayedMerge in a ConfigDelayedMerge, should have consolidated stack");
        }
    }

    @Override
    public ConfigValueType valueType() {
        throw new ConfigException.NotResolved("called valueType() on value with unresolved substitutions, need to Config#resolve() first, see API docs");
    }

    @Override
    public Object unwrapped() {
        throw new ConfigException.NotResolved("called unwrapped() on value with unresolved substitutions, need to Config#resolve() first, see API docs");
    }

    @Override
    ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
        return ConfigDelayedMerge.resolveSubstitutions(this, this.stack, context, source);
    }

    static ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ReplaceableMergeStack replaceable, List<AbstractConfigValue> stack, ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(context.depth(), "delayed merge stack has " + stack.size() + " items:");
            int count = 0;
            for (AbstractConfigValue v : stack) {
                ConfigImpl.trace(context.depth() + 1, count + ": " + v);
                ++count;
            }
        }
        ResolveContext newContext = context;
        int count = 0;
        AbstractConfigValue merged = null;
        for (AbstractConfigValue end : stack) {
            ResolveSource sourceForEnd;
            if (end instanceof ReplaceableMergeStack) {
                throw new ConfigException.BugOrBroken("A delayed merge should not contain another one: " + replaceable);
            }
            if (end instanceof Unmergeable) {
                AbstractConfigValue remainder = replaceable.makeReplacement(context, count + 1);
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(newContext.depth(), "remainder portion: " + remainder);
                }
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(newContext.depth(), "building sourceForEnd");
                }
                sourceForEnd = source.replaceWithinCurrentParent((AbstractConfigValue)((Object)replaceable), remainder);
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(newContext.depth(), "  sourceForEnd before reset parents but after replace: " + sourceForEnd);
                }
                sourceForEnd = sourceForEnd.resetParents();
            } else {
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(newContext.depth(), "will resolve end against the original source with parent pushed");
                }
                sourceForEnd = source.pushParent(replaceable);
            }
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(newContext.depth(), "sourceForEnd      =" + sourceForEnd);
            }
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(newContext.depth(), "Resolving highest-priority item in delayed merge " + end + " against " + sourceForEnd + " endWasRemoved=" + (source != sourceForEnd));
            }
            ResolveResult<? extends AbstractConfigValue> result = newContext.resolve(end, sourceForEnd);
            Object resolvedEnd = result.value;
            newContext = result.context;
            if (resolvedEnd != null) {
                if (merged == null) {
                    merged = (AbstractConfigValue)resolvedEnd;
                } else {
                    if (ConfigImpl.traceSubstitutionsEnabled()) {
                        ConfigImpl.trace(newContext.depth() + 1, "merging " + merged + " with fallback " + resolvedEnd);
                    }
                    merged = merged.withFallback((ConfigMergeable)resolvedEnd);
                }
            }
            ++count;
            if (!ConfigImpl.traceSubstitutionsEnabled()) continue;
            ConfigImpl.trace(newContext.depth(), "stack merged, yielding: " + merged);
        }
        return ResolveResult.make(newContext, merged);
    }

    @Override
    public AbstractConfigValue makeReplacement(ResolveContext context, int skipping) {
        return ConfigDelayedMerge.makeReplacement(context, this.stack, skipping);
    }

    static AbstractConfigValue makeReplacement(ResolveContext context, List<AbstractConfigValue> stack, int skipping) {
        List<AbstractConfigValue> subStack = stack.subList(skipping, stack.size());
        if (subStack.isEmpty()) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(context.depth(), "Nothing else in the merge stack, replacing with null");
            }
            return null;
        }
        AbstractConfigValue merged = null;
        for (AbstractConfigValue v : subStack) {
            if (merged == null) {
                merged = v;
                continue;
            }
            merged = merged.withFallback(v);
        }
        return merged;
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.UNRESOLVED;
    }

    @Override
    public AbstractConfigValue replaceChild(AbstractConfigValue child, AbstractConfigValue replacement) {
        List<AbstractConfigValue> newStack = ConfigDelayedMerge.replaceChildInList(this.stack, child, replacement);
        if (newStack == null) {
            return null;
        }
        return new ConfigDelayedMerge(this.origin(), newStack);
    }

    @Override
    public boolean hasDescendant(AbstractConfigValue descendant) {
        return ConfigDelayedMerge.hasDescendantInList(this.stack, descendant);
    }

    @Override
    ConfigDelayedMerge relativized(Path prefix) {
        ArrayList<AbstractConfigValue> newStack = new ArrayList<AbstractConfigValue>();
        for (AbstractConfigValue o : this.stack) {
            newStack.add(o.relativized(prefix));
        }
        return new ConfigDelayedMerge(this.origin(), newStack);
    }

    static boolean stackIgnoresFallbacks(List<AbstractConfigValue> stack) {
        AbstractConfigValue last = stack.get(stack.size() - 1);
        return last.ignoresFallbacks();
    }

    @Override
    protected boolean ignoresFallbacks() {
        return ConfigDelayedMerge.stackIgnoresFallbacks(this.stack);
    }

    @Override
    protected AbstractConfigValue newCopy(ConfigOrigin newOrigin) {
        return new ConfigDelayedMerge(newOrigin, this.stack);
    }

    @Override
    protected final ConfigDelayedMerge mergedWithTheUnmergeable(Unmergeable fallback) {
        return (ConfigDelayedMerge)this.mergedWithTheUnmergeable(this.stack, fallback);
    }

    @Override
    protected final ConfigDelayedMerge mergedWithObject(AbstractConfigObject fallback) {
        return (ConfigDelayedMerge)this.mergedWithObject(this.stack, fallback);
    }

    @Override
    protected ConfigDelayedMerge mergedWithNonObject(AbstractConfigValue fallback) {
        return (ConfigDelayedMerge)this.mergedWithNonObject(this.stack, fallback);
    }

    public Collection<AbstractConfigValue> unmergedValues() {
        return this.stack;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ConfigDelayedMerge;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ConfigDelayedMerge) {
            return this.canEqual(other) && (this.stack == ((ConfigDelayedMerge)other).stack || this.stack.equals(((ConfigDelayedMerge)other).stack));
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

    static void render(List<AbstractConfigValue> stack, StringBuilder sb, int indent, boolean atRoot, String atKey, ConfigRenderOptions options) {
        boolean commentMerge = options.getComments();
        if (commentMerge) {
            sb.append("# unresolved merge of " + stack.size() + " values follows (\n");
            if (atKey == null) {
                ConfigDelayedMerge.indent(sb, indent, options);
                sb.append("# this unresolved merge will not be parseable because it's at the root of the object\n");
                ConfigDelayedMerge.indent(sb, indent, options);
                sb.append("# the HOCON format has no way to list multiple root objects in a single file\n");
            }
        }
        ArrayList<AbstractConfigValue> reversed = new ArrayList<AbstractConfigValue>();
        reversed.addAll(stack);
        Collections.reverse(reversed);
        int i = 0;
        for (AbstractConfigValue v : reversed) {
            if (commentMerge) {
                ConfigDelayedMerge.indent(sb, indent, options);
                if (atKey != null) {
                    sb.append("#     unmerged value " + i + " for key " + ConfigImplUtil.renderJsonString(atKey) + " from ");
                } else {
                    sb.append("#     unmerged value " + i + " from ");
                }
                ++i;
                sb.append(v.origin().description());
                sb.append("\n");
                for (String comment : v.origin().comments()) {
                    ConfigDelayedMerge.indent(sb, indent, options);
                    sb.append("# ");
                    sb.append(comment);
                    sb.append("\n");
                }
            }
            ConfigDelayedMerge.indent(sb, indent, options);
            if (atKey != null) {
                sb.append(ConfigImplUtil.renderJsonString(atKey));
                if (options.getFormatted()) {
                    sb.append(" : ");
                } else {
                    sb.append(":");
                }
            }
            v.render(sb, indent, atRoot, options);
            sb.append(",");
            if (!options.getFormatted()) continue;
            sb.append('\n');
        }
        sb.setLength(sb.length() - 1);
        if (options.getFormatted()) {
            sb.setLength(sb.length() - 1);
            sb.append("\n");
        }
        if (commentMerge) {
            ConfigDelayedMerge.indent(sb, indent, options);
            sb.append("# ) end of unresolved merge\n");
        }
    }
}

