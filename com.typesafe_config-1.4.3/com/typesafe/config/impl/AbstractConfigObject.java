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
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigDelayedMergeObject;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.Container;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveContext;
import com.typesafe.config.impl.ResolveResult;
import com.typesafe.config.impl.ResolveSource;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SimpleConfig;
import com.typesafe.config.impl.SimpleConfigOrigin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

abstract class AbstractConfigObject
extends AbstractConfigValue
implements ConfigObject,
Container {
    private final SimpleConfig config = new SimpleConfig(this);

    protected AbstractConfigObject(ConfigOrigin origin) {
        super(origin);
    }

    @Override
    public SimpleConfig toConfig() {
        return this.config;
    }

    @Override
    public AbstractConfigObject toFallbackValue() {
        return this;
    }

    @Override
    public abstract AbstractConfigObject withOnlyKey(String var1);

    @Override
    public abstract AbstractConfigObject withoutKey(String var1);

    @Override
    public abstract AbstractConfigObject withValue(String var1, ConfigValue var2);

    protected abstract AbstractConfigObject withOnlyPathOrNull(Path var1);

    abstract AbstractConfigObject withOnlyPath(Path var1);

    abstract AbstractConfigObject withoutPath(Path var1);

    abstract AbstractConfigObject withValue(Path var1, ConfigValue var2);

    protected final AbstractConfigValue peekAssumingResolved(String key, Path originalPath) {
        try {
            return this.attemptPeekWithPartialResolve(key);
        }
        catch (ConfigException.NotResolved e) {
            throw ConfigImpl.improveNotResolved(originalPath, e);
        }
    }

    abstract AbstractConfigValue attemptPeekWithPartialResolve(String var1);

    protected AbstractConfigValue peekPath(Path path) {
        return AbstractConfigObject.peekPath(this, path);
    }

    private static AbstractConfigValue peekPath(AbstractConfigObject self, Path path) {
        try {
            Path next = path.remainder();
            AbstractConfigValue v = self.attemptPeekWithPartialResolve(path.first());
            if (next == null) {
                return v;
            }
            if (v instanceof AbstractConfigObject) {
                return AbstractConfigObject.peekPath((AbstractConfigObject)v, next);
            }
            return null;
        }
        catch (ConfigException.NotResolved e) {
            throw ConfigImpl.improveNotResolved(path, e);
        }
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.OBJECT;
    }

    protected abstract AbstractConfigObject newCopy(ResolveStatus var1, ConfigOrigin var2);

    @Override
    protected AbstractConfigObject newCopy(ConfigOrigin origin) {
        return this.newCopy(this.resolveStatus(), origin);
    }

    @Override
    protected AbstractConfigObject constructDelayedMerge(ConfigOrigin origin, List<AbstractConfigValue> stack) {
        return new ConfigDelayedMergeObject(origin, stack);
    }

    @Override
    protected abstract AbstractConfigObject mergedWithObject(AbstractConfigObject var1);

    @Override
    public AbstractConfigObject withFallback(ConfigMergeable mergeable) {
        return (AbstractConfigObject)super.withFallback(mergeable);
    }

    static ConfigOrigin mergeOrigins(Collection<? extends AbstractConfigValue> stack) {
        if (stack.isEmpty()) {
            throw new ConfigException.BugOrBroken("can't merge origins on empty list");
        }
        ArrayList<SimpleConfigOrigin> origins = new ArrayList<SimpleConfigOrigin>();
        SimpleConfigOrigin firstOrigin = null;
        int numMerged = 0;
        for (AbstractConfigValue abstractConfigValue : stack) {
            if (firstOrigin == null) {
                firstOrigin = abstractConfigValue.origin();
            }
            if (abstractConfigValue instanceof AbstractConfigObject && ((AbstractConfigObject)abstractConfigValue).resolveStatus() == ResolveStatus.RESOLVED && ((ConfigObject)((Object)abstractConfigValue)).isEmpty()) continue;
            origins.add(abstractConfigValue.origin());
            ++numMerged;
        }
        if (numMerged == 0) {
            origins.add(firstOrigin);
        }
        return SimpleConfigOrigin.mergeOrigins(origins);
    }

    static ConfigOrigin mergeOrigins(AbstractConfigObject ... stack) {
        return AbstractConfigObject.mergeOrigins(Arrays.asList(stack));
    }

    abstract ResolveResult<? extends AbstractConfigObject> resolveSubstitutions(ResolveContext var1, ResolveSource var2) throws AbstractConfigValue.NotPossibleToResolve;

    @Override
    abstract AbstractConfigObject relativized(Path var1);

    @Override
    public abstract AbstractConfigValue get(Object var1);

    @Override
    protected abstract void render(StringBuilder var1, int var2, boolean var3, ConfigRenderOptions var4);

    private static UnsupportedOperationException weAreImmutable(String method) {
        return new UnsupportedOperationException("ConfigObject is immutable, you can't call Map." + method);
    }

    @Override
    public void clear() {
        throw AbstractConfigObject.weAreImmutable("clear");
    }

    @Override
    public ConfigValue put(String arg0, ConfigValue arg1) {
        throw AbstractConfigObject.weAreImmutable("put");
    }

    @Override
    public void putAll(Map<? extends String, ? extends ConfigValue> arg0) {
        throw AbstractConfigObject.weAreImmutable("putAll");
    }

    @Override
    public ConfigValue remove(Object arg0) {
        throw AbstractConfigObject.weAreImmutable("remove");
    }

    @Override
    public AbstractConfigObject withOrigin(ConfigOrigin origin) {
        return (AbstractConfigObject)super.withOrigin(origin);
    }
}

