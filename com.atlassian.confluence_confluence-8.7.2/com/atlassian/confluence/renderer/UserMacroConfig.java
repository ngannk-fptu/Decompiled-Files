/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.macro.GenericVelocityMacro;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.spring.container.ContainerManager;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class UserMacroConfig
implements Serializable {
    @Deprecated
    public static final String OUTPUT_TYPE_HTML = "html";
    @Deprecated
    public static final String OUTPUT_TYPE_WIKI = "wiki";
    public static final String VISIBLE_ALL_USERS = "true";
    public static final String VISIBLE_ADMINS_ONLY = "false";
    public static final String BODY_TYPE_RAW = "raw";
    public static final String BODY_TYPE_ESCAPE_HTML = "escapehtml";
    public static final String BODY_TYPE_RENDERED = "rendered";
    public static final String BODY_TYPE_NONE = "none";
    private String template;
    private String name;
    private String title;
    private String description;
    private Set<String> categories;
    private String iconLocation;
    private String documentationUrl;
    private boolean hasBody;
    private boolean hidden = true;
    private String outputType = "html";
    private String bodyType;
    private List<MacroParameter> parameters;

    UserMacroConfig(String template, String name, boolean hasBody, String outputType, String bodyType) {
        this.template = template;
        this.name = name;
        this.hasBody = hasBody;
        this.outputType = outputType;
        this.bodyType = bodyType;
    }

    public UserMacroConfig() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isHasBody() {
        return this.hasBody;
    }

    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }

    @Deprecated
    public String getOutputType() {
        return this.outputType;
    }

    @Deprecated
    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getBodyType() {
        return this.bodyType;
    }

    public void setBodyType(String bodyType) {
        if (!(BODY_TYPE_NONE.equals(bodyType) || BODY_TYPE_ESCAPE_HTML.equals(bodyType) || BODY_TYPE_RAW.equals(bodyType) || BODY_TYPE_RENDERED.equals(bodyType))) {
            throw new IllegalArgumentException("Unknown body type: " + bodyType);
        }
        this.bodyType = bodyType;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getCategories() {
        if (this.categories == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(this.categories);
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public String getIconLocation() {
        return this.iconLocation;
    }

    public void setIconLocation(String iconLocation) {
        this.iconLocation = iconLocation;
    }

    public String getDocumentationUrl() {
        return this.documentationUrl;
    }

    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public List<MacroParameter> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<MacroParameter> parameters) {
        this.parameters = parameters;
    }

    public Macro toMacro() {
        GenericVelocityMacro velocityMacro = (GenericVelocityMacro)ContainerManager.getInstance().getContainerContext().createComponent(GenericVelocityMacro.class);
        velocityMacro.setTemplate(this.getTemplate());
        velocityMacro.setName(this.getName());
        velocityMacro.setMacroParameters(this.getParameters());
        velocityMacro.setBodyType(UserMacroConfig.deriveBodyType(this));
        if (this.isHasBody()) {
            velocityMacro.setEscapeBody(BODY_TYPE_ESCAPE_HTML.equals(this.getBodyType()));
        }
        if (!OUTPUT_TYPE_HTML.equals(this.getOutputType())) {
            velocityMacro.setLegacyWikiTemplate(true);
        }
        return velocityMacro;
    }

    public static Macro.BodyType deriveBodyType(UserMacroConfig config) {
        if (!config.isHasBody()) {
            return Macro.BodyType.NONE;
        }
        if (BODY_TYPE_RENDERED.equals(config.getBodyType())) {
            return Macro.BodyType.RICH_TEXT;
        }
        return Macro.BodyType.PLAIN_TEXT;
    }
}

