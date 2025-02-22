/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api;

import com.hazelcast.org.snakeyaml.engine.v2.api.DumpSettingsBuilder;
import com.hazelcast.org.snakeyaml.engine.v2.api.SettingKey;
import com.hazelcast.org.snakeyaml.engine.v2.common.FlowStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.NonPrintableStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.ScalarStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.SpecVersion;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import com.hazelcast.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import com.hazelcast.org.snakeyaml.engine.v2.serializer.AnchorGenerator;
import java.util.Map;
import java.util.Optional;

public final class DumpSettings {
    private final boolean explicitStart;
    private final boolean explicitEnd;
    private final NonPrintableStyle nonPrintableStyle;
    private final Optional<Tag> explicitRootTag;
    private final AnchorGenerator anchorGenerator;
    private final Optional<SpecVersion> yamlDirective;
    private final Map<String, String> tagDirective;
    private final ScalarResolver scalarResolver;
    private final FlowStyle defaultFlowStyle;
    private final ScalarStyle defaultScalarStyle;
    private final boolean canonical;
    private final boolean multiLineFlow;
    private final boolean useUnicodeEncoding;
    private final int indent;
    private final int indicatorIndent;
    private final int width;
    private final String bestLineBreak;
    private final boolean splitLines;
    private final int maxSimpleKeyLength;
    private final Map<SettingKey, Object> customProperties;

    DumpSettings(boolean explicitStart, boolean explicitEnd, Optional<Tag> explicitRootTag, AnchorGenerator anchorGenerator, Optional<SpecVersion> yamlDirective, Map<String, String> tagDirective, ScalarResolver scalarResolver, FlowStyle defaultFlowStyle, ScalarStyle defaultScalarStyle, NonPrintableStyle nonPrintableStyle, boolean canonical, boolean multiLineFlow, boolean useUnicodeEncoding, int indent, int indicatorIndent, int width, String bestLineBreak, boolean splitLines, int maxSimpleKeyLength, Map<SettingKey, Object> customProperties) {
        this.explicitStart = explicitStart;
        this.explicitEnd = explicitEnd;
        this.nonPrintableStyle = nonPrintableStyle;
        this.explicitRootTag = explicitRootTag;
        this.anchorGenerator = anchorGenerator;
        this.yamlDirective = yamlDirective;
        this.tagDirective = tagDirective;
        this.scalarResolver = scalarResolver;
        this.defaultFlowStyle = defaultFlowStyle;
        this.defaultScalarStyle = defaultScalarStyle;
        this.canonical = canonical;
        this.multiLineFlow = multiLineFlow;
        this.useUnicodeEncoding = useUnicodeEncoding;
        this.indent = indent;
        this.indicatorIndent = indicatorIndent;
        this.width = width;
        this.bestLineBreak = bestLineBreak;
        this.splitLines = splitLines;
        this.maxSimpleKeyLength = maxSimpleKeyLength;
        this.customProperties = customProperties;
    }

    public static final DumpSettingsBuilder builder() {
        return new DumpSettingsBuilder();
    }

    public FlowStyle getDefaultFlowStyle() {
        return this.defaultFlowStyle;
    }

    public ScalarStyle getDefaultScalarStyle() {
        return this.defaultScalarStyle;
    }

    public boolean isExplicitStart() {
        return this.explicitStart;
    }

    public AnchorGenerator getAnchorGenerator() {
        return this.anchorGenerator;
    }

    public ScalarResolver getScalarResolver() {
        return this.scalarResolver;
    }

    public boolean isExplicitEnd() {
        return this.explicitEnd;
    }

    public Optional<Tag> getExplicitRootTag() {
        return this.explicitRootTag;
    }

    public Optional<SpecVersion> getYamlDirective() {
        return this.yamlDirective;
    }

    public Map<String, String> getTagDirective() {
        return this.tagDirective;
    }

    public boolean isCanonical() {
        return this.canonical;
    }

    public boolean isMultiLineFlow() {
        return this.multiLineFlow;
    }

    public boolean isUseUnicodeEncoding() {
        return this.useUnicodeEncoding;
    }

    public int getIndent() {
        return this.indent;
    }

    public int getIndicatorIndent() {
        return this.indicatorIndent;
    }

    public int getWidth() {
        return this.width;
    }

    public String getBestLineBreak() {
        return this.bestLineBreak;
    }

    public boolean isSplitLines() {
        return this.splitLines;
    }

    public int getMaxSimpleKeyLength() {
        return this.maxSimpleKeyLength;
    }

    public NonPrintableStyle getNonPrintableStyle() {
        return this.nonPrintableStyle;
    }

    public Object getCustomProperty(SettingKey key) {
        return this.customProperties.get(key);
    }
}

