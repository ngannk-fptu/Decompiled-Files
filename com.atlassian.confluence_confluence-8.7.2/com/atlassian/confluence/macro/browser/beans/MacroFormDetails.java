/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.macro.browser.beans;

import com.atlassian.confluence.macro.browser.beans.MacroBody;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.xhtml.MacroMigrationPoint;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationLink;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.spring.container.ContainerManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.Context;

public class MacroFormDetails {
    private final String macroName;
    private final String documentationUrl;
    private final ResourceDescriptor notationHelpResource;
    private final int schemaVersion;
    private final Set<MacroMigrationPoint> excludedSchemaMigrationPoints;
    private String notationHelp;
    private List<MacroParameter> parameters;
    private boolean freeform = false;
    private MacroBody body;
    private boolean showDefaultParamInPlaceholder = true;

    public static MacroFormDetailsBuilder builder() {
        return new MacroFormDetailsBuilder();
    }

    private MacroFormDetails(MacroFormDetailsBuilder builder) {
        this.macroName = builder.macroName;
        this.documentationUrl = builder.documentationUrl;
        this.schemaVersion = builder.schemaVersion;
        this.excludedSchemaMigrationPoints = Set.copyOf(builder.excludedSchemaMigrationPoints);
        this.parameters = List.copyOf(builder.parameters);
        this.showDefaultParamInPlaceholder = builder.showDefaultParamInPlaceholder;
        this.body = builder.body;
        this.notationHelpResource = null;
    }

    public String getMacroName() {
        return this.macroName;
    }

    public void renderForDisplay(Context context) {
        if (this.notationHelpResource == null || StringUtils.isNotBlank((CharSequence)this.notationHelp)) {
            return;
        }
        if (StringUtils.isBlank((CharSequence)this.notationHelpResource.getLocation())) {
            this.notationHelp = VelocityUtils.getRenderedContent(this.notationHelpResource.getContent(), context);
        }
        this.notationHelp = VelocityUtils.getRenderedTemplate(this.notationHelpResource.getLocation(), context);
        this.notationHelp = this.notationHelp.replaceAll("\\s+", " ");
    }

    public String getNotationHelp() {
        return this.notationHelp;
    }

    @Deprecated
    public String getDocumentationUrl() {
        if (this.documentationUrl != null) {
            DocumentationBean docBean = (DocumentationBean)ContainerManager.getComponent((String)"docBean");
            return docBean.getLink(this.documentationUrl);
        }
        return null;
    }

    public DocumentationLink getDocumentationLink() {
        if (this.documentationUrl != null) {
            return DocumentationLink.getInstance(this.documentationUrl);
        }
        return null;
    }

    public void setBody(MacroBody body) {
        this.body = body;
    }

    public MacroBody getBody() {
        return this.body;
    }

    public boolean isFreeform() {
        return this.freeform;
    }

    public boolean isShowDefaultParamInPlaceholder() {
        return this.showDefaultParamInPlaceholder;
    }

    public List<MacroParameter> getParameters() {
        return Collections.unmodifiableList(this.parameters);
    }

    public boolean hasRequiredParameters() {
        for (MacroParameter parameter : this.parameters) {
            if (!parameter.isRequired()) continue;
            return true;
        }
        return false;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public Set<MacroMigrationPoint> getExcludedSchemaMigrationPoints() {
        return this.excludedSchemaMigrationPoints;
    }

    public String toString() {
        return this.macroName;
    }

    public static class MacroFormDetailsBuilder {
        private String macroName;
        private String documentationUrl;
        private int schemaVersion = 1;
        private List<MacroParameter> parameters = new ArrayList<MacroParameter>();
        private MacroBody body;
        private boolean showDefaultParamInPlaceholder = true;
        private Set<MacroMigrationPoint> excludedSchemaMigrationPoints = new HashSet<MacroMigrationPoint>();

        private MacroFormDetailsBuilder() {
        }

        public MacroFormDetailsBuilder macroName(String macroName) {
            this.macroName = macroName;
            return this;
        }

        public MacroFormDetailsBuilder documentationUrl(String documentationUrl) {
            this.documentationUrl = documentationUrl;
            return this;
        }

        public MacroFormDetailsBuilder schemaVersion(int schemaVersion) {
            this.schemaVersion = schemaVersion;
            return this;
        }

        public MacroFormDetailsBuilder parameters(List<MacroParameter> parameters) {
            if (parameters != null) {
                this.parameters = parameters;
            }
            return this;
        }

        public MacroFormDetailsBuilder body(MacroBody body) {
            this.body = body;
            return this;
        }

        public MacroFormDetailsBuilder showDefaultParamInPlaceholder(boolean showDefaultParamInPlaceholder) {
            this.showDefaultParamInPlaceholder = showDefaultParamInPlaceholder;
            return this;
        }

        public void excludeSchemaMigrationPoint(MacroMigrationPoint migrationPoint) {
            this.excludedSchemaMigrationPoints.add(migrationPoint);
        }

        public MacroFormDetailsBuilder setExcludedSchemaMigrationPoints(Set<MacroMigrationPoint> migrationPoints) {
            this.excludedSchemaMigrationPoints = migrationPoints;
            return this;
        }

        public MacroFormDetails build() {
            return new MacroFormDetails(this);
        }
    }
}

