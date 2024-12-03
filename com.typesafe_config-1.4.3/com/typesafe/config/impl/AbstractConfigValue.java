/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.ConfigDelayedMerge;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.Container;
import com.typesafe.config.impl.MergeableValue;
import com.typesafe.config.impl.OriginType;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveContext;
import com.typesafe.config.impl.ResolveResult;
import com.typesafe.config.impl.ResolveSource;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SimpleConfig;
import com.typesafe.config.impl.SimpleConfigObject;
import com.typesafe.config.impl.SimpleConfigOrigin;
import com.typesafe.config.impl.Unmergeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract class AbstractConfigValue
implements ConfigValue,
MergeableValue {
    private final SimpleConfigOrigin origin;

    AbstractConfigValue(ConfigOrigin origin) {
        this.origin = (SimpleConfigOrigin)origin;
    }

    @Override
    public SimpleConfigOrigin origin() {
        return this.origin;
    }

    ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ResolveContext context, ResolveSource source) throws NotPossibleToResolve {
        return ResolveResult.make(context, this);
    }

    ResolveStatus resolveStatus() {
        return ResolveStatus.RESOLVED;
    }

    protected static List<AbstractConfigValue> replaceChildInList(List<AbstractConfigValue> list, AbstractConfigValue child, AbstractConfigValue replacement) {
        int i;
        for (i = 0; i < list.size() && list.get(i) != child; ++i) {
        }
        if (i == list.size()) {
            throw new ConfigException.BugOrBroken("tried to replace " + child + " which is not in " + list);
        }
        ArrayList<AbstractConfigValue> newStack = new ArrayList<AbstractConfigValue>(list);
        if (replacement != null) {
            newStack.set(i, replacement);
        } else {
            newStack.remove(i);
        }
        if (newStack.isEmpty()) {
            return null;
        }
        return newStack;
    }

    protected static boolean hasDescendantInList(List<AbstractConfigValue> list, AbstractConfigValue descendant) {
        for (AbstractConfigValue v : list) {
            if (v != descendant) continue;
            return true;
        }
        for (AbstractConfigValue v : list) {
            if (!(v instanceof Container) || !((Container)((Object)v)).hasDescendant(descendant)) continue;
            return true;
        }
        return false;
    }

    AbstractConfigValue relativized(Path prefix) {
        return this;
    }

    @Override
    public AbstractConfigValue toFallbackValue() {
        return this;
    }

    protected abstract AbstractConfigValue newCopy(ConfigOrigin var1);

    protected boolean ignoresFallbacks() {
        return this.resolveStatus() == ResolveStatus.RESOLVED;
    }

    protected AbstractConfigValue withFallbacksIgnored() {
        if (this.ignoresFallbacks()) {
            return this;
        }
        throw new ConfigException.BugOrBroken("value class doesn't implement forced fallback-ignoring " + this);
    }

    protected final void requireNotIgnoringFallbacks() {
        if (this.ignoresFallbacks()) {
            throw new ConfigException.BugOrBroken("method should not have been called with ignoresFallbacks=true " + this.getClass().getSimpleName());
        }
    }

    protected AbstractConfigValue constructDelayedMerge(ConfigOrigin origin, List<AbstractConfigValue> stack) {
        return new ConfigDelayedMerge(origin, stack);
    }

    protected final AbstractConfigValue mergedWithTheUnmergeable(Collection<AbstractConfigValue> stack, Unmergeable fallback) {
        this.requireNotIgnoringFallbacks();
        ArrayList<AbstractConfigValue> newStack = new ArrayList<AbstractConfigValue>();
        newStack.addAll(stack);
        newStack.addAll(fallback.unmergedValues());
        return this.constructDelayedMerge(AbstractConfigObject.mergeOrigins(newStack), newStack);
    }

    private final AbstractConfigValue delayMerge(Collection<AbstractConfigValue> stack, AbstractConfigValue fallback) {
        ArrayList<AbstractConfigValue> newStack = new ArrayList<AbstractConfigValue>();
        newStack.addAll(stack);
        newStack.add(fallback);
        return this.constructDelayedMerge(AbstractConfigObject.mergeOrigins(newStack), newStack);
    }

    protected final AbstractConfigValue mergedWithObject(Collection<AbstractConfigValue> stack, AbstractConfigObject fallback) {
        this.requireNotIgnoringFallbacks();
        if (this instanceof AbstractConfigObject) {
            throw new ConfigException.BugOrBroken("Objects must reimplement mergedWithObject");
        }
        return this.mergedWithNonObject(stack, fallback);
    }

    protected final AbstractConfigValue mergedWithNonObject(Collection<AbstractConfigValue> stack, AbstractConfigValue fallback) {
        this.requireNotIgnoringFallbacks();
        if (this.resolveStatus() == ResolveStatus.RESOLVED) {
            return this.withFallbacksIgnored();
        }
        return this.delayMerge(stack, fallback);
    }

    protected AbstractConfigValue mergedWithTheUnmergeable(Unmergeable fallback) {
        this.requireNotIgnoringFallbacks();
        return this.mergedWithTheUnmergeable(Collections.singletonList(this), fallback);
    }

    protected AbstractConfigValue mergedWithObject(AbstractConfigObject fallback) {
        this.requireNotIgnoringFallbacks();
        return this.mergedWithObject(Collections.singletonList(this), fallback);
    }

    protected AbstractConfigValue mergedWithNonObject(AbstractConfigValue fallback) {
        this.requireNotIgnoringFallbacks();
        return this.mergedWithNonObject(Collections.singletonList(this), fallback);
    }

    @Override
    public AbstractConfigValue withOrigin(ConfigOrigin origin) {
        if (this.origin == origin) {
            return this;
        }
        return this.newCopy(origin);
    }

    @Override
    public AbstractConfigValue withFallback(ConfigMergeable mergeable) {
        if (this.ignoresFallbacks()) {
            return this;
        }
        ConfigValue other = ((MergeableValue)mergeable).toFallbackValue();
        if (other instanceof Unmergeable) {
            return this.mergedWithTheUnmergeable((Unmergeable)((Object)other));
        }
        if (other instanceof AbstractConfigObject) {
            return this.mergedWithObject((AbstractConfigObject)other);
        }
        return this.mergedWithNonObject((AbstractConfigValue)other);
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConfigValue;
    }

    public boolean equals(Object other) {
        if (other instanceof ConfigValue) {
            return this.canEqual(other) && this.valueType() == ((ConfigValue)other).valueType() && ConfigImplUtil.equalsHandlingNull(this.unwrapped(), ((ConfigValue)other).unwrapped());
        }
        return false;
    }

    public int hashCode() {
        Object o = this.unwrapped();
        if (o == null) {
            return 0;
        }
        return o.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.render(sb, 0, true, null, ConfigRenderOptions.concise());
        return this.getClass().getSimpleName() + "(" + sb.toString() + ")";
    }

    protected static void indent(StringBuilder sb, int indent, ConfigRenderOptions options) {
        if (options.getFormatted()) {
            for (int remaining = indent; remaining > 0; --remaining) {
                sb.append("    ");
            }
        }
    }

    protected void render(StringBuilder sb, int indent, boolean atRoot, String atKey, ConfigRenderOptions options) {
        if (atKey != null) {
            String renderedKey = options.getJson() ? ConfigImplUtil.renderJsonString(atKey) : ConfigImplUtil.renderStringUnquotedIfPossible(atKey);
            sb.append(renderedKey);
            if (options.getJson()) {
                if (options.getFormatted()) {
                    sb.append(" : ");
                } else {
                    sb.append(":");
                }
            } else if (this instanceof ConfigObject) {
                if (options.getFormatted()) {
                    sb.append(' ');
                }
            } else {
                sb.append("=");
            }
        }
        this.render(sb, indent, atRoot, options);
    }

    protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
        if (this.hideEnvVariableValue(options)) {
            sb.append("<env variable>");
        } else {
            Object u = this.unwrapped();
            sb.append(u.toString());
        }
    }

    protected boolean hideEnvVariableValue(ConfigRenderOptions options) {
        return !options.getShowEnvVariableValues() && this.origin.originType() == OriginType.ENV_VARIABLE;
    }

    protected void appendHiddenEnvVariableValue(StringBuilder sb) {
        sb.append("\"<env variable>\"");
    }

    @Override
    public final String render() {
        return this.render(ConfigRenderOptions.defaults());
    }

    @Override
    public final String render(ConfigRenderOptions options) {
        StringBuilder sb = new StringBuilder();
        this.render(sb, 0, true, null, options);
        return sb.toString();
    }

    String transformToString() {
        return null;
    }

    SimpleConfig atKey(ConfigOrigin origin, String key) {
        Map<String, AbstractConfigValue> m = Collections.singletonMap(key, this);
        return new SimpleConfigObject(origin, m).toConfig();
    }

    @Override
    public SimpleConfig atKey(String key) {
        return this.atKey(SimpleConfigOrigin.newSimple("atKey(" + key + ")"), key);
    }

    SimpleConfig atPath(ConfigOrigin origin, Path path) {
        SimpleConfig result = this.atKey(origin, path.last());
        for (Path parent = path.parent(); parent != null; parent = parent.parent()) {
            String key = parent.last();
            result = result.atKey(origin, key);
        }
        return result;
    }

    @Override
    public SimpleConfig atPath(String pathExpression) {
        SimpleConfigOrigin origin = SimpleConfigOrigin.newSimple("atPath(" + pathExpression + ")");
        return this.atPath(origin, Path.newPath(pathExpression));
    }

    protected abstract class NoExceptionsModifier
    implements Modifier {
        protected NoExceptionsModifier() {
        }

        @Override
        public final AbstractConfigValue modifyChildMayThrow(String keyOrNull, AbstractConfigValue v) throws Exception {
            try {
                return this.modifyChild(keyOrNull, v);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ConfigException.BugOrBroken("Unexpected exception", e);
            }
        }

        abstract AbstractConfigValue modifyChild(String var1, AbstractConfigValue var2);
    }

    protected static interface Modifier {
        public AbstractConfigValue modifyChildMayThrow(String var1, AbstractConfigValue var2) throws Exception;
    }

    static class NotPossibleToResolve
    extends Exception {
        private static final long serialVersionUID = 1L;
        private final String traceString;

        NotPossibleToResolve(ResolveContext context) {
            super("was not possible to resolve");
            this.traceString = context.traceString();
        }

        String traceString() {
            return this.traceString;
        }
    }
}

