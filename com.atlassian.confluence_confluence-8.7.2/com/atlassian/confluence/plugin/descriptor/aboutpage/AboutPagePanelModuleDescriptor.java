/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.collect.Lists
 *  com.google.common.io.CharStreams
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor.aboutpage;

import com.atlassian.confluence.plugin.descriptor.aboutpage.BomParser;
import com.atlassian.confluence.plugin.descriptor.aboutpage.BomParserImpl;
import com.atlassian.confluence.plugin.descriptor.aboutpage.Material;
import com.atlassian.confluence.plugin.descriptor.aboutpage.PluginAndMaterials;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AboutPagePanelModuleDescriptor
extends AbstractModuleDescriptor<Void> {
    private static final Logger log = LoggerFactory.getLogger(AboutPagePanelModuleDescriptor.class);
    private static final String LOCATION_KEY = "location";
    private static final String MODULE_KEY = "module-key";
    private static final String FUNCTION_KEY = "function";
    private final TemplateRenderer templateRenderer = (TemplateRenderer)ContainerManager.getComponent((String)"templateRenderer");
    private final BomParser bomParser = new BomParserImpl();
    private String introduction = "";
    private String licenses = "";
    private String conclusion = "";
    private String introductionModule = "";
    private String conclusionModule = "";

    public AboutPagePanelModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        Element introduction = element.element("introduction");
        Element licenses = element.element("licenses");
        Element conclusion = element.element("conclusion");
        if (introduction == null && licenses == null && conclusion == null) {
            throw new PluginParseException("An introduction template, licenses file or conclusion template must be provided.");
        }
        if (introduction != null) {
            this.introduction = this.assertFunctionPresent(introduction, "Introduction");
            this.introductionModule = this.assertModuleKeyPresent(introduction, "Introduction");
        }
        if (licenses != null) {
            this.licenses = this.assertLocationPresent(licenses, "Licenses");
        }
        if (conclusion != null) {
            this.conclusion = this.assertFunctionPresent(conclusion, "Conclusion");
            this.conclusionModule = this.assertModuleKeyPresent(conclusion, "Conclusion");
        }
    }

    public Void getModule() {
        return null;
    }

    private String assertModuleKeyPresent(Element introduction, String field) {
        String location = introduction.attributeValue(MODULE_KEY);
        if (StringUtils.isEmpty((CharSequence)location)) {
            throw new PluginParseException(field + " module key must be specified");
        }
        return location;
    }

    private String assertLocationPresent(Element introduction, String field) {
        String location = introduction.attributeValue(LOCATION_KEY);
        if (StringUtils.isEmpty((CharSequence)location)) {
            throw new PluginParseException(field + " license file must be specified");
        }
        return location;
    }

    private String assertFunctionPresent(Element introduction, String field) {
        String location = introduction.attributeValue(FUNCTION_KEY);
        if (StringUtils.isEmpty((CharSequence)location)) {
            throw new PluginParseException(field + " function must be specified");
        }
        return location;
    }

    public String getPluginSectionHtml() {
        PluginAndMaterials pluginAndMaterials = this.getPluginAndMaterials();
        if (pluginAndMaterials == null) {
            return "";
        }
        HashMap<String, Object> soyContext = new HashMap<String, Object>();
        soyContext.put("pluginEntry", pluginAndMaterials);
        return this.renderToTemplate("confluence.web.resources:about", "Confluence.Templates.About.renderModule.soy", soyContext);
    }

    private String renderToTemplate(String location, String name, HashMap<String, Object> soyContext) {
        StringBuilder content = new StringBuilder();
        this.templateRenderer.renderTo(content, location, name, soyContext);
        return content.toString();
    }

    private PluginAndMaterials getPluginAndMaterials() {
        try {
            ArrayList<Material> materials = this.getMaterials(this.plugin);
            String introduction = this.generateIntroduction();
            String conclusion = this.generateConclusion();
            return new PluginAndMaterials(this.plugin.getPluginInformation().getVersion(), this.plugin.getName(), introduction, conclusion, materials);
        }
        catch (RuntimeException e) {
            log.info("Could not load license information for " + this.plugin.getName() + " " + this.plugin.getKey(), (Throwable)e);
            return null;
        }
    }

    private ArrayList<Material> getMaterials(Plugin plugin) {
        Set<Material> materials = this.loadMaterials(plugin);
        ArrayList materialsCopy = Lists.newArrayList(materials);
        Collections.sort(materialsCopy);
        return materialsCopy;
    }

    private Set<Material> loadMaterials(Plugin plugin) {
        HashSet<Material> materials = new HashSet<Material>();
        if (!StringUtils.isEmpty((CharSequence)this.getLicensesLocation())) {
            ClassLoader classLoader = plugin.getClassLoader();
            try (InputStream resourceAsStream = classLoader.getResourceAsStream(this.getLicensesLocation());){
                String bomContents = CharStreams.toString((Readable)new InputStreamReader(resourceAsStream, Charset.defaultCharset()));
                materials.addAll(this.bomParser.extractLgplMaterials(bomContents));
            }
            catch (IOException e) {
                log.debug("Could not load detailed license information for " + plugin.getName() + " " + plugin.getKey() + " : " + this.getName() + " at " + this.getLicensesLocation(), (Throwable)e);
            }
        }
        return materials;
    }

    private String generateIntroduction() {
        return this.renderItem(this.introduction, this.getIntroductionModuleKey());
    }

    private String generateConclusion() {
        return this.renderItem(this.conclusion, this.getConclusionModuleKey());
    }

    private String renderItem(String templateName, String itemModuleKey) {
        if (StringUtils.isEmpty((CharSequence)templateName) || StringUtils.isEmpty((CharSequence)itemModuleKey)) {
            return "";
        }
        return this.renderToTemplate(itemModuleKey, templateName + ".soy", new HashMap<String, Object>());
    }

    public String getIntroductionModuleKey() {
        return this.introductionModule;
    }

    public String getLicensesLocation() {
        return this.licenses;
    }

    public String getConclusionModuleKey() {
        return this.conclusionModule;
    }
}

