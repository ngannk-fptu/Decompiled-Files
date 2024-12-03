/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.StringUtils
 *  io.micrometer.core.instrument.config.MeterRegistryConfig
 *  io.micrometer.core.instrument.config.MeterRegistryConfigValidator
 *  io.micrometer.core.instrument.config.validate.InvalidReason
 *  io.micrometer.core.instrument.config.validate.PropertyValidator
 *  io.micrometer.core.instrument.config.validate.Validated
 *  io.micrometer.core.instrument.step.StepRegistryConfig
 */
package io.micrometer.influx;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.config.MeterRegistryConfig;
import io.micrometer.core.instrument.config.MeterRegistryConfigValidator;
import io.micrometer.core.instrument.config.validate.InvalidReason;
import io.micrometer.core.instrument.config.validate.PropertyValidator;
import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import io.micrometer.influx.InfluxApiVersion;
import io.micrometer.influx.InfluxConsistency;
import java.util.function.Function;

public interface InfluxConfig
extends StepRegistryConfig {
    public static final InfluxConfig DEFAULT = k -> null;

    default public String prefix() {
        return "influx";
    }

    default public String db() {
        return (String)PropertyValidator.getString((MeterRegistryConfig)this, (String)"db").orElse((Object)"mydb");
    }

    default public InfluxConsistency consistency() {
        return (InfluxConsistency)((Object)PropertyValidator.getEnum((MeterRegistryConfig)this, InfluxConsistency.class, (String)"consistency").orElse((Object)InfluxConsistency.ONE));
    }

    @Nullable
    default public String userName() {
        return (String)PropertyValidator.getSecret((MeterRegistryConfig)this, (String)"userName").orElse(null);
    }

    @Nullable
    default public String password() {
        return (String)PropertyValidator.getSecret((MeterRegistryConfig)this, (String)"password").orElse(null);
    }

    @Nullable
    default public String retentionPolicy() {
        return (String)PropertyValidator.getString((MeterRegistryConfig)this, (String)"retentionPolicy").orElse(null);
    }

    @Nullable
    default public String retentionDuration() {
        return (String)PropertyValidator.getString((MeterRegistryConfig)this, (String)"retentionDuration").orElse(null);
    }

    @Nullable
    default public Integer retentionReplicationFactor() {
        return (Integer)PropertyValidator.getInteger((MeterRegistryConfig)this, (String)"retentionReplicationFactor").orElse(null);
    }

    @Nullable
    default public String retentionShardDuration() {
        return (String)PropertyValidator.getString((MeterRegistryConfig)this, (String)"retentionShardDuration").orElse(null);
    }

    default public String uri() {
        return (String)PropertyValidator.getUrlString((MeterRegistryConfig)this, (String)"uri").orElse((Object)"http://localhost:8086");
    }

    default public boolean compressed() {
        return (Boolean)PropertyValidator.getBoolean((MeterRegistryConfig)this, (String)"compressed").orElse((Object)true);
    }

    default public boolean autoCreateDb() {
        return (Boolean)PropertyValidator.getBoolean((MeterRegistryConfig)this, (String)"autoCreateDb").orElse((Object)true);
    }

    default public InfluxApiVersion apiVersion() {
        return (InfluxApiVersion)((Object)PropertyValidator.getEnum((MeterRegistryConfig)this, InfluxApiVersion.class, (String)"apiVersion").orElseGet(() -> {
            if (StringUtils.isNotBlank((String)this.org())) {
                return InfluxApiVersion.V2;
            }
            return InfluxApiVersion.V1;
        }));
    }

    @Nullable
    default public String org() {
        return (String)PropertyValidator.getString((MeterRegistryConfig)this, (String)"org").orElse(null);
    }

    default public String bucket() {
        return (String)PropertyValidator.getString((MeterRegistryConfig)this, (String)"bucket").flatMap((bucket, valid) -> {
            if (StringUtils.isNotBlank((String)bucket)) {
                return Validated.valid((String)valid.getProperty(), (Object)bucket);
            }
            String db = this.db();
            if (StringUtils.isNotBlank((String)db)) {
                return Validated.valid((String)valid.getProperty(), (Object)db);
            }
            return Validated.invalid((String)valid.getProperty(), (Object)bucket, (String)"db or bucket should be specified", (InvalidReason)InvalidReason.MISSING);
        }).get();
    }

    @Nullable
    default public String token() {
        return (String)PropertyValidator.getString((MeterRegistryConfig)this, (String)"token").orElse(null);
    }

    default public Validated<?> validate() {
        return MeterRegistryConfigValidator.checkAll((MeterRegistryConfig)this, (Function[])new Function[]{c -> StepRegistryConfig.validate((StepRegistryConfig)c), MeterRegistryConfigValidator.checkRequired((String)"db", InfluxConfig::db), MeterRegistryConfigValidator.checkRequired((String)"bucket", InfluxConfig::bucket), MeterRegistryConfigValidator.checkRequired((String)"consistency", InfluxConfig::consistency), MeterRegistryConfigValidator.checkRequired((String)"apiVersion", InfluxConfig::apiVersion).andThen(v -> v.invalidateWhen(a -> a == InfluxApiVersion.V2 && StringUtils.isBlank((String)this.org()), "requires 'org' is also configured", InvalidReason.MISSING)).andThen(v -> v.invalidateWhen(a -> a == InfluxApiVersion.V2 && StringUtils.isBlank((String)this.token()), "requires 'token' is also configured", InvalidReason.MISSING)), MeterRegistryConfigValidator.checkRequired((String)"uri", InfluxConfig::uri)});
    }
}

