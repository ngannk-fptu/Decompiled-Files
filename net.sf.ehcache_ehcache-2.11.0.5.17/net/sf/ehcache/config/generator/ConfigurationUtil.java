/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator;

import java.io.PrintWriter;
import java.io.StringWriter;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.generator.model.NodeElementVisitor;
import net.sf.ehcache.config.generator.model.XMLGeneratorVisitor;
import net.sf.ehcache.config.generator.model.elements.CacheConfigurationElement;
import net.sf.ehcache.config.generator.model.elements.ConfigurationElement;

public abstract class ConfigurationUtil {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String generateCacheManagerConfigurationText(CacheManager cacheManager) {
        StringWriter output = new StringWriter();
        try (PrintWriter writer = new PrintWriter(output);){
            XMLGeneratorVisitor configGenerator = new XMLGeneratorVisitor(writer);
            configGenerator.disableOutputBehavior(XMLGeneratorVisitor.OutputBehavior.OUTPUT_OPTIONAL_ATTRIBUTES_WITH_DEFAULT_VALUES);
            ConfigurationUtil.visitConfiguration(cacheManager, (NodeElementVisitor)configGenerator);
            writer.flush();
        }
        return output.toString();
    }

    static void visitConfiguration(CacheManager cacheManager, NodeElementVisitor visitor) {
        ConfigurationElement configElement = new ConfigurationElement(cacheManager);
        configElement.accept(visitor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String generateCacheManagerConfigurationText(Configuration configuration) {
        StringWriter output = new StringWriter();
        try (PrintWriter writer = new PrintWriter(output);){
            XMLGeneratorVisitor configGenerator = new XMLGeneratorVisitor(writer);
            configGenerator.disableOutputBehavior(XMLGeneratorVisitor.OutputBehavior.OUTPUT_OPTIONAL_ATTRIBUTES_WITH_DEFAULT_VALUES);
            ConfigurationUtil.visitConfiguration(configuration, (NodeElementVisitor)configGenerator);
            writer.flush();
        }
        return output.toString();
    }

    static void visitConfiguration(Configuration configuration, NodeElementVisitor visitor) {
        ConfigurationElement configElement = new ConfigurationElement(configuration);
        configElement.accept(visitor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String generateCacheConfigurationText(Configuration configuration, CacheConfiguration cacheConfiguration) {
        StringWriter output = new StringWriter();
        try (PrintWriter writer = new PrintWriter(output);){
            XMLGeneratorVisitor configGenerator = new XMLGeneratorVisitor(writer);
            configGenerator.disableOutputBehavior(XMLGeneratorVisitor.OutputBehavior.OUTPUT_OPTIONAL_ATTRIBUTES_WITH_DEFAULT_VALUES);
            ConfigurationUtil.visitCacheConfiguration(configuration, cacheConfiguration, configGenerator);
            writer.flush();
        }
        return output.toString();
    }

    static void visitCacheConfiguration(Configuration configuration, CacheConfiguration cacheConfiguration, NodeElementVisitor configGenerator) {
        CacheConfigurationElement element = new CacheConfigurationElement(null, configuration, cacheConfiguration);
        element.accept(configGenerator);
    }
}

