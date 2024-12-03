/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import groovy.text.GStringTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyPackageDoc;
import org.codehaus.groovy.groovydoc.GroovyRootDoc;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager;
import org.codehaus.groovy.tools.groovydoc.GroovyDocTool;
import org.codehaus.groovy.tools.groovydoc.ResourceManager;

public class GroovyDocTemplateEngine {
    private TemplateEngine engine;
    private ResourceManager resourceManager;
    private Properties properties;
    private Map<String, Template> docTemplates;
    private List<String> docTemplatePaths;
    private Map<String, Template> packageTemplates;
    private List<String> packageTemplatePaths;
    private Map<String, Template> classTemplates;
    private List<String> classTemplatePaths;

    public GroovyDocTemplateEngine(GroovyDocTool tool, ResourceManager resourceManager, String classTemplate) {
        this(tool, resourceManager, new String[0], new String[0], new String[]{classTemplate}, new Properties());
    }

    public GroovyDocTemplateEngine(GroovyDocTool tool, ResourceManager resourceManager, String[] docTemplates, String[] packageTemplates, String[] classTemplates, Properties properties) {
        this.resourceManager = resourceManager;
        this.properties = properties;
        this.docTemplatePaths = Arrays.asList(docTemplates);
        this.packageTemplatePaths = Arrays.asList(packageTemplates);
        this.classTemplatePaths = Arrays.asList(classTemplates);
        this.docTemplates = new LinkedHashMap<String, Template>();
        this.packageTemplates = new LinkedHashMap<String, Template>();
        this.classTemplates = new LinkedHashMap<String, Template>();
        this.engine = new GStringTemplateEngine();
    }

    String applyClassTemplates(GroovyClassDoc classDoc) {
        String templatePath = this.classTemplatePaths.get(0);
        String templateWithBindingApplied = "";
        try {
            Template t = this.classTemplates.get(templatePath);
            if (t == null) {
                t = this.engine.createTemplate(this.resourceManager.getReader(templatePath));
                this.classTemplates.put(templatePath, t);
            }
            LinkedHashMap<String, Object> binding = new LinkedHashMap<String, Object>();
            binding.put("classDoc", classDoc);
            binding.put("props", this.properties);
            templateWithBindingApplied = t.make(binding).toString();
        }
        catch (Exception e) {
            System.out.println("Error processing class template for: " + classDoc.getFullPathName());
            e.printStackTrace();
        }
        return templateWithBindingApplied;
    }

    String applyPackageTemplate(String template, GroovyPackageDoc packageDoc) {
        String templateWithBindingApplied = "";
        try {
            Template t = this.packageTemplates.get(template);
            if (t == null) {
                t = this.engine.createTemplate(this.resourceManager.getReader(template));
                this.packageTemplates.put(template, t);
            }
            LinkedHashMap<String, Object> binding = new LinkedHashMap<String, Object>();
            binding.put("packageDoc", packageDoc);
            binding.put("props", this.properties);
            templateWithBindingApplied = t.make(binding).toString();
        }
        catch (Exception e) {
            System.out.println("Error processing package template for: " + packageDoc.name());
            e.printStackTrace();
        }
        return templateWithBindingApplied;
    }

    String applyRootDocTemplate(String template, GroovyRootDoc rootDoc) {
        String templateWithBindingApplied = "";
        try {
            Template t = this.docTemplates.get(template);
            if (t == null) {
                t = this.engine.createTemplate(this.resourceManager.getReader(template));
                this.docTemplates.put(template, t);
            }
            LinkedHashMap<String, Object> binding = new LinkedHashMap<String, Object>();
            binding.put("rootDoc", rootDoc);
            binding.put("props", this.properties);
            templateWithBindingApplied = t.make(binding).toString();
        }
        catch (Exception e) {
            System.out.println("Error processing root doc template");
            e.printStackTrace();
        }
        return templateWithBindingApplied;
    }

    Iterator<String> classTemplatesIterator() {
        return this.classTemplatePaths.iterator();
    }

    Iterator<String> packageTemplatesIterator() {
        return this.packageTemplatePaths.iterator();
    }

    Iterator<String> docTemplatesIterator() {
        return this.docTemplatePaths.iterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void copyBinaryResource(String template, String destFileName) {
        if (this.resourceManager instanceof ClasspathResourceManager) {
            FileOutputStream outputStream = null;
            try {
                InputStream inputStream = ((ClasspathResourceManager)this.resourceManager).getInputStream(template);
                outputStream = new FileOutputStream(destFileName);
                IOGroovyMethods.leftShift((OutputStream)outputStream, inputStream);
            }
            catch (IOException e) {
                System.err.println("Resource " + template + " skipped due to: " + e.getMessage());
                DefaultGroovyMethodsSupport.closeQuietly(outputStream);
            }
            catch (NullPointerException e2) {
                System.err.println("Resource " + template + " not found so skipped");
                {
                    catch (Throwable throwable) {
                        DefaultGroovyMethodsSupport.closeQuietly(outputStream);
                        throw throwable;
                    }
                }
                DefaultGroovyMethodsSupport.closeQuietly(outputStream);
            }
            DefaultGroovyMethodsSupport.closeQuietly(outputStream);
        }
    }
}

