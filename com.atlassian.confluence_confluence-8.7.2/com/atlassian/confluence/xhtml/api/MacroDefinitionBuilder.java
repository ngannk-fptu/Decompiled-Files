/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroId;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.fugue.Option;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MacroDefinitionBuilder {
    private static final String DEFAULT_STORAGE_VERSION = "2";
    private String name;
    private int schemaVersion = 1;
    private MacroBody macroBody;
    private Map<String, String> parameters = new LinkedHashMap<String, String>();
    private Map<String, Object> typedParameters = new LinkedHashMap<String, Object>();
    private String storageVersion = "2";
    private @Nullable MacroId macroId = null;

    MacroDefinitionBuilder() {
    }

    public MacroDefinitionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MacroDefinitionBuilder withMacroBody(MacroBody macroBody) {
        this.macroBody = macroBody;
        return this;
    }

    public MacroDefinitionBuilder withParameters(Map<String, String> parameters) {
        this.parameters.clear();
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        return this;
    }

    public MacroDefinitionBuilder withParameter(String key, String value) {
        if (value != null) {
            this.parameters.put(key, value);
        } else {
            this.parameters.remove(key);
        }
        return this;
    }

    public MacroDefinitionBuilder withTypedParameters(Map<String, Object> typedParameters) {
        this.typedParameters.clear();
        if (typedParameters != null) {
            this.typedParameters.putAll(typedParameters);
        }
        return this;
    }

    public MacroDefinitionBuilder withTypedParameter(String key, Object value) {
        if (value != null) {
            this.typedParameters.put(key, value);
        } else {
            this.typedParameters.remove(key);
        }
        return this;
    }

    public MacroDefinitionBuilder withStorageVersion(String storageVersion) {
        this.storageVersion = storageVersion;
        return this;
    }

    @Deprecated
    public MacroDefinitionBuilder withMacroId(Option<MacroId> macroId) {
        this.macroId = (MacroId)Objects.requireNonNull(macroId, "macroId should not be null").getOrNull();
        return this;
    }

    public MacroDefinitionBuilder withMacroId(MacroId macroId) {
        this.macroId = macroId;
        return this;
    }

    public MacroDefinitionBuilder withMacroId(String macroId) {
        if (macroId == null) {
            throw new IllegalArgumentException("macroId must not be null");
        }
        this.macroId = MacroId.fromString(macroId);
        return this;
    }

    public MacroDefinitionBuilder withMacroIdentifier(MacroId macroId) {
        return this.withMacroId(macroId);
    }

    public MacroDefinitionBuilder withMacroIdentifier(String macroId) {
        return this.withMacroId(macroId);
    }

    public MacroDefinitionBuilder withSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public MacroBody getMacroBody() {
        return this.macroBody;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public Map<String, Object> getTypedParameters() {
        return this.typedParameters;
    }

    public String getStorageVersion() {
        return this.storageVersion;
    }

    @Deprecated
    public Option<MacroId> getMacroId() {
        return Option.option((Object)this.macroId);
    }

    public MacroId getMacroIdentifier() {
        return this.macroId;
    }

    public MacroDefinition build() {
        return new MacroDefinition(this);
    }

    public void setDefaultParameterValue(String defaultParameterValue) {
        if (defaultParameterValue == null) {
            this.parameters.remove("");
        } else {
            this.parameters.put("", defaultParameterValue);
        }
    }
}

