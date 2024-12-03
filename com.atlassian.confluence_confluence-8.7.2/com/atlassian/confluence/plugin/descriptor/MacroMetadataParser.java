/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.macro.browser.beans.MacroFormDetails;
import com.atlassian.confluence.macro.browser.beans.MacroIcon;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroMetadataBuilder;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroParameterBuilder;
import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import com.atlassian.confluence.macro.browser.beans.MacroPropertyPanelButton;
import com.atlassian.confluence.macro.xhtml.MacroMigrationPoint;
import com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.plugin.ModuleDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroMetadataParser {
    private static final Logger log = LoggerFactory.getLogger(MacroMetadataParser.class);
    private static final String ALIAS_ELEMENT = "alias";
    private static final String CATEGORY_ELEMENT = "category";
    public static final String PARAMETERS_ELEMENT = "parameters";
    private static final String DEFAULT_PARAMETER_ELEMENT = "default";
    private static final String PARAMETER_ELEMENT = "parameter";
    private static final String EMUN_VALUE_ELEMENT = "value";
    private static final String OPTION_ELEMENT = "option";
    private static final String PROPERTY_PANEL_ELEMENT = "property-panel";
    private static final String BUTTON_ELEMENT = "button";
    private static final String SPACER_ELEMENT = "spacer";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String DEFAULT_ATTRIBUTE = "default";
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String REQUIRED_ATTRIBUTE = "required";
    private static final String MULTIPLE_ATTRIBUTE = "multiple";
    private static final String SHOW_IN_PLACEHOLDER_OPTION = "showValueInPlaceholder";
    private static final String ICON_ATTRIBUTE = "icon";
    private static final String DOCUMENTATION_URL_ATTRIBUTE = "documentation-url";
    private static final String BODY_DEPRECATED_ATTRIBUTE = "hide-body";
    private static final String HIDDEN_ATTRIBUTE = "hidden";
    private static final String KEY_ATTRIBUTE = "key";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String ID_ATTRIBUTE = "id";
    private static final String LABEL_ATTRIBUTE = "label";
    private static final String ACTION_ATTRIBUTE = "action";
    private static final String SCHEMA_VERSION_ATTRIBUTE = "schema-version";
    private static final String EXCLUDED_SCHEMA_MIGRATION_POINTS_ATTRIBUTE = "excluded-schema-migration-points";
    private DocumentationBeanFactory documentationBeanFactory;

    public MacroMetadataParser() {
    }

    @Deprecated
    public MacroMetadataParser(DocumentationBeanFactory documentationBeanFactory) {
        this.documentationBeanFactory = documentationBeanFactory;
    }

    MacroMetadata parseMacro(ModuleDescriptor descriptor, Element macroElement) {
        String desc = descriptor.getDescriptionKey();
        if (StringUtils.isBlank((CharSequence)desc)) {
            desc = descriptor.getDescription();
        }
        if (descriptor instanceof XhtmlMacroModuleDescriptor) {
            return this.parseMacro(descriptor.getPluginKey(), descriptor.getName(), descriptor.getI18nNameKey(), ((XhtmlMacroModuleDescriptor)descriptor).isAlwaysShowConfig(), desc, macroElement);
        }
        return this.parseMacro(descriptor.getPluginKey(), descriptor.getName(), descriptor.getI18nNameKey(), desc, macroElement);
    }

    MacroMetadata parseMacro(String pluginKey, Element macroElement) {
        return this.parseMacro(pluginKey, macroElement.attributeValue(NAME_ATTRIBUTE), null, null, macroElement);
    }

    private MacroMetadata parseMacro(String pluginKey, String macroName, String title, String description, Element macroElement) {
        return this.parseMacro(pluginKey, macroName, title, false, description, macroElement);
    }

    private MacroMetadata parseMacro(String pluginKey, String macroName, String title, boolean isAlwaysShowConfig, String description, Element macroElement) {
        ArrayList<MacroParameter> parameters = new ArrayList<MacroParameter>();
        if (macroElement.element(PARAMETERS_ELEMENT) == null) {
            log.debug("The '{}' macro descriptor has no 'parameters' element. The macro will not be visible in the macro browser.", (Object)macroName);
            return null;
        }
        Element defaultParamElement = macroElement.element(PARAMETERS_ELEMENT).element("default");
        Map<String, String> defaultParamOptions = defaultParamElement != null ? this.parseOptions(defaultParamElement) : Collections.singletonMap(SHOW_IN_PLACEHOLDER_OPTION, "true");
        List list = macroElement.element(PARAMETERS_ELEMENT).elements(PARAMETER_ELEMENT);
        for (Element paramElement : list) {
            parameters.add(this.parseParameter(pluginKey, macroName, paramElement));
        }
        HashSet<String> macroAliases = new HashSet<String>();
        HashSet<String> categories = new HashSet<String>();
        String icon = macroElement.attributeValue(ICON_ATTRIBUTE);
        String documentationUrl = macroElement.attributeValue(DOCUMENTATION_URL_ATTRIBUTE);
        String schemaVersionStr = macroElement.attributeValue(SCHEMA_VERSION_ATTRIBUTE);
        int schemaVersion = 1;
        if (StringUtils.isNotEmpty((CharSequence)schemaVersionStr)) {
            schemaVersion = Integer.parseInt(schemaVersionStr);
        }
        HashSet<MacroMigrationPoint> migrationPoints = new HashSet<MacroMigrationPoint>();
        String migrationPointsStr = macroElement.attributeValue(EXCLUDED_SCHEMA_MIGRATION_POINTS_ATTRIBUTE);
        if (StringUtils.isNotEmpty((CharSequence)migrationPointsStr)) {
            String[] pointStrings;
            for (String point : pointStrings = migrationPointsStr.split(",")) {
                migrationPoints.add(MacroMigrationPoint.of(point));
            }
        }
        boolean isBodyDeprecated = Boolean.parseBoolean(macroElement.attributeValue(BODY_DEPRECATED_ATTRIBUTE));
        boolean hidden = Boolean.parseBoolean(macroElement.attributeValue(HIDDEN_ATTRIBUTE));
        ArrayList<MacroPropertyPanelButton> buttons = new ArrayList<MacroPropertyPanelButton>();
        Iterator iterator = macroElement.elementIterator();
        while (iterator.hasNext()) {
            String nameAttr;
            Element element = (Element)iterator.next();
            if (ALIAS_ELEMENT.equalsIgnoreCase(element.getName())) {
                nameAttr = element.attributeValue(NAME_ATTRIBUTE);
                if (!StringUtils.isNotBlank((CharSequence)nameAttr)) continue;
                macroAliases.add(nameAttr);
                continue;
            }
            if (CATEGORY_ELEMENT.equalsIgnoreCase(element.getName())) {
                nameAttr = element.attributeValue(NAME_ATTRIBUTE);
                if (!StringUtils.isNotBlank((CharSequence)nameAttr)) continue;
                categories.add(element.attributeValue(NAME_ATTRIBUTE));
                continue;
            }
            if (!PROPERTY_PANEL_ELEMENT.equalsIgnoreCase(element.getName())) continue;
            Iterator iter = element.elementIterator();
            while (iter.hasNext()) {
                Element elem = (Element)iter.next();
                if (BUTTON_ELEMENT.equals(elem.getName())) {
                    String id = elem.attributeValue(ID_ATTRIBUTE);
                    String label = elem.attributeValue(LABEL_ATTRIBUTE);
                    String action = elem.attributeValue(ACTION_ATTRIBUTE);
                    buttons.add(new MacroPropertyPanelButton(id, label, action));
                    continue;
                }
                if (!SPACER_ELEMENT.equals(elem.getName())) continue;
                buttons.add(MacroPropertyPanelButton.SPACER);
            }
        }
        MacroFormDetails.MacroFormDetailsBuilder formDetailsBuilder = MacroFormDetails.builder();
        formDetailsBuilder.macroName(macroName).documentationUrl(this.createDocumentationUrl(documentationUrl)).showDefaultParamInPlaceholder(Boolean.valueOf(defaultParamOptions.get(SHOW_IN_PLACEHOLDER_OPTION))).schemaVersion(schemaVersion).setExcludedSchemaMigrationPoints(migrationPoints).parameters(parameters);
        MacroFormDetails formDetails = formDetailsBuilder.build();
        MacroMetadataBuilder builder = MacroMetadataBuilder.builder().setMacroName(macroName).setPluginKey(pluginKey).setTitle(title).setAlwaysShowConfig(isAlwaysShowConfig).setButtons(buttons);
        if (icon != null) {
            builder.setIcon(new MacroIcon(icon, !UrlUtils.isAbsoluteUrl(icon)));
        }
        builder.setAliases(macroAliases).setCategories(categories).setDescription(description).setBodyDeprecated(isBodyDeprecated).setHidden(hidden).setFormDetails(formDetails);
        return builder.build();
    }

    private String createDocumentationUrl(String documentationUrl) {
        if (documentationUrl != null && this.documentationBeanFactory != null) {
            DocumentationBean docBean = this.documentationBeanFactory.getDocumentationBean();
            return docBean.getLink(documentationUrl);
        }
        return documentationUrl;
    }

    private MacroParameter parseParameter(String pluginKey, String macroName, Element paramElement) {
        Element valueElement;
        String parameterName = paramElement.attributeValue(NAME_ATTRIBUTE);
        String typeName = paramElement.attributeValue(TYPE_ATTRIBUTE);
        MacroParameterType type = MacroParameterType.get(typeName);
        if (type == null) {
            log.warn("Unrecognised type '{}' for parameter '{}' of macro '{}' in plugin '{}'", new Object[]{typeName, parameterName, macroName, pluginKey});
        }
        MacroParameter parameter = MacroParameterBuilder.builder().setPluginKey(pluginKey).setMacroName(macroName).setName(parameterName).setType(type).setRequired(Boolean.parseBoolean(paramElement.attributeValue(REQUIRED_ATTRIBUTE))).setMultiple(Boolean.parseBoolean(paramElement.attributeValue(MULTIPLE_ATTRIBUTE))).setDefaultValue(paramElement.attributeValue("default")).setHidden(Boolean.parseBoolean(paramElement.attributeValue(HIDDEN_ATTRIBUTE))).build();
        if (type == MacroParameterType.ENUM) {
            for (Object o : paramElement.elements("value")) {
                valueElement = (Element)o;
                parameter.addEnumValue(valueElement.attributeValue(NAME_ATTRIBUTE));
            }
        }
        for (Object o : paramElement.elements(ALIAS_ELEMENT)) {
            valueElement = (Element)o;
            parameter.addAlias(valueElement.attributeValue(NAME_ATTRIBUTE));
        }
        parameter.addOptions(this.parseOptions(paramElement));
        return parameter;
    }

    private Map<String, String> parseOptions(Element element) {
        HashMap<String, String> options = new HashMap<String, String>();
        for (Object o : element.elements(OPTION_ELEMENT)) {
            Element optionElement = (Element)o;
            options.put(optionElement.attributeValue(KEY_ATTRIBUTE), optionElement.attributeValue("value"));
        }
        return options;
    }
}

