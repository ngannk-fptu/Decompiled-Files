/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroId;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.fugue.Option;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MacroDefinition {
    public static final String STORAGE_VERSION_1 = "1";
    public static final String STORAGE_VERSION_2 = "2";
    public static final int INITIAL_SCHEMA_VERSION = 1;
    private String name;
    private MacroBody body;
    private Map<String, String> parameters;
    private Map<String, Object> typedParameters;
    private String storageVersion;
    private @Nullable MacroId macroId;
    private int schemaVersion;

    public static MacroDefinitionBuilder builder() {
        return new MacroDefinitionBuilder();
    }

    public static MacroDefinitionBuilder builder(String macroName) {
        return new MacroDefinitionBuilder().withName(macroName);
    }

    MacroDefinition(MacroDefinitionBuilder builder) {
        if (builder.getName() == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = builder.getName();
        this.body = builder.getMacroBody();
        this.parameters = MacroDefinition.newHashMapNullable(builder.getParameters());
        this.typedParameters = MacroDefinition.newHashMapNullable(builder.getTypedParameters());
        this.storageVersion = builder.getStorageVersion();
        this.macroId = builder.getMacroIdentifier();
        this.schemaVersion = builder.getSchemaVersion();
    }

    public MacroDefinition(MacroDefinition macroDefinition) {
        this(MacroDefinition.builder(macroDefinition.getName()).withMacroBody(macroDefinition.getBody()).withParameters(macroDefinition.getParameters()).withTypedParameters(macroDefinition.getTypedParameters()).withStorageVersion(macroDefinition.getStorageVersion()).withMacroIdentifier((MacroId)macroDefinition.getMacroIdentifier().orElse(null)).withSchemaVersion(macroDefinition.getSchemaVersion()));
    }

    public String getName() {
        return this.name;
    }

    public String getDefaultParameterValue() {
        return this.parameters == null ? null : this.parameters.get("");
    }

    public String getBodyText() {
        return this.body == null ? "" : Streamables.writeToString(this.body.getBodyStream());
    }

    public Streamable getBodyStream() {
        return this.body == null ? Streamables.empty() : this.body.getBodyStream();
    }

    public String getStorageBodyText() {
        return this.body == null ? "" : Streamables.writeToString(this.body.getStorageBodyStream());
    }

    public Streamable getStorageBodyStream() {
        return this.body == null ? Streamables.empty() : this.body.getStorageBodyStream();
    }

    public Streamable getTransformedBodyStream() {
        return this.body == null ? Streamables.empty() : this.body.getTransformedBodyStream();
    }

    public MacroBody getBody() {
        return this.body;
    }

    public Macro.BodyType getBodyType() {
        if (this.body instanceof RichTextMacroBody) {
            return Macro.BodyType.RICH_TEXT;
        }
        if (this.body instanceof PlainTextLinkBody) {
            return Macro.BodyType.PLAIN_TEXT;
        }
        if (this.body instanceof PlainTextMacroBody) {
            return Macro.BodyType.PLAIN_TEXT;
        }
        return Macro.BodyType.NONE;
    }

    public boolean hasBody() {
        return this.body != null;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public Map<String, Object> getTypedParameters() {
        return new LinkedHashMap<String, Object>(this.typedParameters);
    }

    public String getParameter(String name) {
        return this.parameters.get(name);
    }

    public <T> T getTypedParameter(String name, Class<T> type) {
        return type.cast(this.typedParameters.get(name));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefaultParameterValue(String defaultParameterValue) {
        if (defaultParameterValue == null) {
            this.parameters.remove("");
        } else {
            this.parameters.put("", defaultParameterValue);
        }
    }

    public void setBody(MacroBody body) {
        this.body = body;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = MacroDefinition.newHashMapNullable(parameters);
    }

    public void setTypedParameters(Map<String, Object> typedParameters) {
        this.typedParameters = MacroDefinition.newHashMapNullable(typedParameters);
    }

    public void setParameter(String name, String value) {
        if (value == null) {
            this.parameters.remove(name);
        } else {
            this.parameters.put(name, value);
        }
    }

    public void setTypedParameter(String name, Object value) {
        if (value == null) {
            this.typedParameters.remove(name);
        } else {
            this.typedParameters.put(name, value);
        }
    }

    public String getStorageVersion() {
        return this.storageVersion;
    }

    public void setStorageVersion(String storageVersion) {
        this.storageVersion = storageVersion;
    }

    @Deprecated
    public Option<MacroId> getMacroId() {
        return Option.option((Object)this.macroId);
    }

    public Optional<MacroId> getMacroIdentifier() {
        return FugueConversionUtil.toOptional(this.getMacroId());
    }

    @Deprecated
    public void setMacroId(Option<MacroId> macroId) {
        this.macroId = (MacroId)macroId.getOrNull();
    }

    public void setMacroIdentifier(@Nullable MacroId macroId) {
        this.setMacroId((Option<MacroId>)Option.option((Object)macroId));
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public boolean isValid() {
        return StringUtils.isNotBlank((CharSequence)this.name);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MacroDefinition that = (MacroDefinition)o;
        return Objects.equals(this.body, that.body) && Objects.equals(this.name, that.name) && Objects.equals(this.parameters, that.parameters) && Objects.equals(this.macroId, that.macroId) && Objects.equals(this.typedParameters, that.typedParameters);
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.name).append((Object)this.body).append(this.parameters).append(this.typedParameters).append((Object)this.macroId).toHashCode();
    }

    public String macroHash() {
        if (StringUtils.isBlank((CharSequence)this.getBodyText())) {
            return null;
        }
        String text = Streamables.writeToString(this.body.getStorageBodyStream());
        return DigestUtils.md5Hex((String)text);
    }

    private static <K, V> HashMap<K, V> newHashMapNullable(@Nullable Map<K, V> map) {
        return map == null ? new LinkedHashMap() : new LinkedHashMap<K, V>(map);
    }
}

