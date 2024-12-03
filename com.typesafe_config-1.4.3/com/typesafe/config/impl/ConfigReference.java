/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveContext;
import com.typesafe.config.impl.ResolveResult;
import com.typesafe.config.impl.ResolveSource;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SubstitutionExpression;
import com.typesafe.config.impl.Unmergeable;
import java.util.Collection;
import java.util.Collections;

final class ConfigReference
extends AbstractConfigValue
implements Unmergeable {
    private final SubstitutionExpression expr;
    private final int prefixLength;

    ConfigReference(ConfigOrigin origin, SubstitutionExpression expr) {
        this(origin, expr, 0);
    }

    private ConfigReference(ConfigOrigin origin, SubstitutionExpression expr, int prefixLength) {
        super(origin);
        this.expr = expr;
        this.prefixLength = prefixLength;
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
    protected ConfigReference newCopy(ConfigOrigin newOrigin) {
        return new ConfigReference(newOrigin, this.expr, this.prefixLength);
    }

    @Override
    protected boolean ignoresFallbacks() {
        return false;
    }

    public Collection<ConfigReference> unmergedValues() {
        return Collections.singleton(this);
    }

    @Override
    ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ResolveContext context, ResolveSource source) {
        AbstractConfigValue v;
        ResolveContext newContext = context.addCycleMarker(this);
        try {
            ResolveSource.ResultWithPath resultWithPath = source.lookupSubst(newContext, this.expr, this.prefixLength);
            newContext = resultWithPath.result.context;
            if (resultWithPath.result.value != null) {
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(newContext.depth(), "recursively resolving " + resultWithPath + " which was the resolution of " + this.expr + " against " + source);
                }
                ResolveSource recursiveResolveSource = new ResolveSource((AbstractConfigObject)resultWithPath.pathFromRoot.last(), resultWithPath.pathFromRoot);
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(newContext.depth(), "will recursively resolve against " + recursiveResolveSource);
                }
                ResolveResult<? extends AbstractConfigValue> result = newContext.resolve((AbstractConfigValue)resultWithPath.result.value, recursiveResolveSource);
                v = result.value;
                newContext = result.context;
            } else {
                ConfigValue fallback = context.options().getResolver().lookup(this.expr.path().render());
                v = (AbstractConfigValue)fallback;
            }
        }
        catch (AbstractConfigValue.NotPossibleToResolve e) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(newContext.depth(), "not possible to resolve " + this.expr + ", cycle involved: " + e.traceString());
            }
            if (this.expr.optional()) {
                v = null;
            }
            throw new ConfigException.UnresolvedSubstitution(this.origin(), this.expr + " was part of a cycle of substitutions involving " + e.traceString(), e);
        }
        if (v == null && !this.expr.optional()) {
            if (newContext.options().getAllowUnresolved()) {
                return ResolveResult.make(newContext.removeCycleMarker(this), this);
            }
            throw new ConfigException.UnresolvedSubstitution(this.origin(), this.expr.toString());
        }
        return ResolveResult.make(newContext.removeCycleMarker(this), v);
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.UNRESOLVED;
    }

    @Override
    ConfigReference relativized(Path prefix) {
        SubstitutionExpression newExpr = this.expr.changePath(this.expr.path().prepend(prefix));
        return new ConfigReference(this.origin(), newExpr, this.prefixLength + prefix.length());
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ConfigReference;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ConfigReference) {
            return this.canEqual(other) && this.expr.equals(((ConfigReference)other).expr);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.expr.hashCode();
    }

    @Override
    protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
        sb.append(this.expr.toString());
    }

    SubstitutionExpression expression() {
        return this.expr;
    }
}

