/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api;

import com.hazelcast.org.snakeyaml.engine.v2.api.DumpSettings;
import com.hazelcast.org.snakeyaml.engine.v2.api.SettingKey;
import com.hazelcast.org.snakeyaml.engine.v2.common.FlowStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.NonPrintableStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.ScalarStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.SpecVersion;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.EmitterException;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import com.hazelcast.org.snakeyaml.engine.v2.resolver.JsonScalarResolver;
import com.hazelcast.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import com.hazelcast.org.snakeyaml.engine.v2.serializer.AnchorGenerator;
import com.hazelcast.org.snakeyaml.engine.v2.serializer.NumberAnchorGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class DumpSettingsBuilder {
    private boolean explicitStart = false;
    private boolean explicitEnd = false;
    private NonPrintableStyle nonPrintableStyle;
    private Optional<Tag> explicitRootTag;
    private AnchorGenerator anchorGenerator;
    private Optional<SpecVersion> yamlDirective;
    private Map<String, String> tagDirective;
    private ScalarResolver scalarResolver;
    private FlowStyle defaultFlowStyle;
    private ScalarStyle defaultScalarStyle;
    private boolean canonical = false;
    private boolean multiLineFlow;
    private boolean useUnicodeEncoding = true;
    private int indent = 2;
    private int indicatorIndent = 0;
    private int width = 80;
    private String bestLineBreak = "\n";
    private boolean splitLines = true;
    private int maxSimpleKeyLength = 128;
    Map<SettingKey, Object> customProperties = new HashMap<SettingKey, Object>();

    DumpSettingsBuilder() {
        this.explicitRootTag = Optional.empty();
        this.tagDirective = new HashMap<String, String>();
        this.scalarResolver = new JsonScalarResolver();
        this.anchorGenerator = new NumberAnchorGenerator(1);
        this.yamlDirective = Optional.empty();
        this.defaultFlowStyle = FlowStyle.AUTO;
        this.defaultScalarStyle = ScalarStyle.PLAIN;
    }

    public DumpSettingsBuilder setDefaultFlowStyle(FlowStyle defaultFlowStyle) {
        this.defaultFlowStyle = defaultFlowStyle;
        return this;
    }

    public DumpSettingsBuilder setDefaultScalarStyle(ScalarStyle defaultScalarStyle) {
        this.defaultScalarStyle = defaultScalarStyle;
        return this;
    }

    public DumpSettingsBuilder setExplicitStart(boolean explicitStart) {
        this.explicitStart = explicitStart;
        return this;
    }

    public DumpSettingsBuilder setAnchorGenerator(AnchorGenerator anchorGenerator) {
        Objects.requireNonNull(anchorGenerator, "anchorGenerator cannot be null");
        this.anchorGenerator = anchorGenerator;
        return this;
    }

    public DumpSettingsBuilder setScalarResolver(ScalarResolver scalarResolver) {
        Objects.requireNonNull(scalarResolver, "scalarResolver cannot be null");
        this.scalarResolver = scalarResolver;
        return this;
    }

    public DumpSettingsBuilder setExplicitRootTag(Optional<Tag> explicitRootTag) {
        Objects.requireNonNull(explicitRootTag, "explicitRootTag cannot be null");
        this.explicitRootTag = explicitRootTag;
        return this;
    }

    public DumpSettingsBuilder setExplicitEnd(boolean explicitEnd) {
        this.explicitEnd = explicitEnd;
        return this;
    }

    public DumpSettingsBuilder setYamlDirective(Optional<SpecVersion> yamlDirective) {
        Objects.requireNonNull(yamlDirective, "yamlDirective cannot be null");
        this.yamlDirective = yamlDirective;
        return this;
    }

    public DumpSettingsBuilder setTagDirective(Map<String, String> tagDirective) {
        Objects.requireNonNull(tagDirective, "tagDirective cannot be null");
        this.tagDirective = tagDirective;
        return this;
    }

    public DumpSettingsBuilder setCanonical(boolean canonical) {
        this.canonical = canonical;
        return this;
    }

    public DumpSettingsBuilder setMultiLineFlow(boolean multiLineFlow) {
        this.multiLineFlow = multiLineFlow;
        return this;
    }

    public DumpSettingsBuilder setUseUnicodeEncoding(boolean useUnicodeEncoding) {
        this.useUnicodeEncoding = useUnicodeEncoding;
        return this;
    }

    public DumpSettingsBuilder setIndent(int indent) {
        if (indent < 1) {
            throw new EmitterException("Indent must be at least 1");
        }
        if (indent > 10) {
            throw new EmitterException("Indent must be at most 10");
        }
        this.indent = indent;
        return this;
    }

    public DumpSettingsBuilder setIndicatorIndent(int indicatorIndent) {
        if (indicatorIndent < 0) {
            throw new EmitterException("Indicator indent must be non-negative.");
        }
        if (indicatorIndent > 9) {
            throw new EmitterException("Indicator indent must be at most Emitter.MAX_INDENT-1: 9");
        }
        this.indicatorIndent = indicatorIndent;
        return this;
    }

    public DumpSettingsBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    public DumpSettingsBuilder setBestLineBreak(String bestLineBreak) {
        Objects.requireNonNull(bestLineBreak, "bestLineBreak cannot be null");
        this.bestLineBreak = bestLineBreak;
        return this;
    }

    public DumpSettingsBuilder setSplitLines(boolean splitLines) {
        this.splitLines = splitLines;
        return this;
    }

    public DumpSettingsBuilder setMaxSimpleKeyLength(int maxSimpleKeyLength) {
        if (maxSimpleKeyLength > 1024) {
            throw new YamlEngineException("The simple key must not span more than 1024 stream characters. See https://yaml.org/spec/1.2/spec.html#id2798057");
        }
        this.maxSimpleKeyLength = maxSimpleKeyLength;
        return this;
    }

    public DumpSettingsBuilder setNonPrintableStyle(NonPrintableStyle nonPrintableStyle) {
        this.nonPrintableStyle = nonPrintableStyle;
        return this;
    }

    public DumpSettingsBuilder setCustomProperty(SettingKey key, Object value) {
        this.customProperties.put(key, value);
        return this;
    }

    public DumpSettings build() {
        return new DumpSettings(this.explicitStart, this.explicitEnd, this.explicitRootTag, this.anchorGenerator, this.yamlDirective, this.tagDirective, this.scalarResolver, this.defaultFlowStyle, this.defaultScalarStyle, this.nonPrintableStyle, this.canonical, this.multiLineFlow, this.useUnicodeEncoding, this.indent, this.indicatorIndent, this.width, this.bestLineBreak, this.splitLines, this.maxSimpleKeyLength, this.customProperties);
    }
}

