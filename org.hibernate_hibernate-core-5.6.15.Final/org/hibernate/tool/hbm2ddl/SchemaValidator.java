/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.internal.ExceptionHandlerHaltImpl;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;

public class SchemaValidator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SchemaValidator.class);

    public void validate(Metadata metadata) {
        this.validate(metadata, ((MetadataImplementor)metadata).getMetadataBuildingOptions().getServiceRegistry());
    }

    public void validate(Metadata metadata, ServiceRegistry serviceRegistry) {
        LOG.runningSchemaValidator();
        HashMap config = new HashMap(serviceRegistry.getService(ConfigurationService.class).getSettings());
        SchemaManagementTool tool = serviceRegistry.getService(SchemaManagementTool.class);
        ExecutionOptions executionOptions = SchemaManagementToolCoordinator.buildExecutionOptions(config, ExceptionHandlerHaltImpl.INSTANCE);
        tool.getSchemaValidator(config).doValidation(metadata, executionOptions);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) {
        try {
            CommandLineArgs parsedArgs = CommandLineArgs.parseCommandLineArgs(args);
            StandardServiceRegistry serviceRegistry = SchemaValidator.buildStandardServiceRegistry(parsedArgs);
            try {
                MetadataImplementor metadata = SchemaValidator.buildMetadata(parsedArgs, serviceRegistry);
                new SchemaValidator().validate(metadata, serviceRegistry);
            }
            finally {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
            }
        }
        catch (Exception e) {
            LOG.unableToRunSchemaUpdate(e);
        }
    }

    private static StandardServiceRegistry buildStandardServiceRegistry(CommandLineArgs parsedArgs) throws Exception {
        BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
        StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder(bsr);
        if (parsedArgs.cfgXmlFile != null) {
            ssrBuilder.configure(parsedArgs.cfgXmlFile);
        }
        if (parsedArgs.propertiesFile != null) {
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(parsedArgs.propertiesFile);){
                properties.load(fis);
            }
            ssrBuilder.applySettings(properties);
        }
        return ssrBuilder.build();
    }

    private static MetadataImplementor buildMetadata(CommandLineArgs parsedArgs, StandardServiceRegistry serviceRegistry) throws Exception {
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        for (String filename : parsedArgs.hbmXmlFiles) {
            metadataSources.addFile(filename);
        }
        for (String filename : parsedArgs.jarFiles) {
            metadataSources.addJar(new File(filename));
        }
        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
        StrategySelector strategySelector = serviceRegistry.getService(StrategySelector.class);
        if (parsedArgs.implicitNamingStrategy != null) {
            metadataBuilder.applyImplicitNamingStrategy(strategySelector.resolveStrategy(ImplicitNamingStrategy.class, parsedArgs.implicitNamingStrategy));
        }
        if (parsedArgs.physicalNamingStrategy != null) {
            metadataBuilder.applyPhysicalNamingStrategy(strategySelector.resolveStrategy(PhysicalNamingStrategy.class, parsedArgs.physicalNamingStrategy));
        }
        return (MetadataImplementor)metadataBuilder.build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MetadataImplementor buildMetadataFromMainArgs(String[] args) throws Exception {
        CommandLineArgs commandLineArgs = CommandLineArgs.parseCommandLineArgs(args);
        StandardServiceRegistry serviceRegistry = SchemaValidator.buildStandardServiceRegistry(commandLineArgs);
        try {
            MetadataImplementor metadataImplementor = SchemaValidator.buildMetadata(commandLineArgs, serviceRegistry);
            return metadataImplementor;
        }
        finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    private static class CommandLineArgs {
        String implicitNamingStrategy = null;
        String physicalNamingStrategy = null;
        String propertiesFile = null;
        String cfgXmlFile = null;
        List<String> hbmXmlFiles = new ArrayList<String>();
        List<String> jarFiles = new ArrayList<String>();

        private CommandLineArgs() {
        }

        public static CommandLineArgs parseCommandLineArgs(String[] args) {
            CommandLineArgs parsedArgs = new CommandLineArgs();
            for (String arg : args) {
                if (arg.startsWith("--")) {
                    if (arg.startsWith("--properties=")) {
                        parsedArgs.propertiesFile = arg.substring(13);
                        continue;
                    }
                    if (arg.startsWith("--config=")) {
                        parsedArgs.cfgXmlFile = arg.substring(9);
                        continue;
                    }
                    if (arg.startsWith("--naming=")) {
                        DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategyArgument();
                        continue;
                    }
                    if (arg.startsWith("--implicit-naming=")) {
                        parsedArgs.implicitNamingStrategy = arg.substring(18);
                        continue;
                    }
                    if (!arg.startsWith("--physical-naming=")) continue;
                    parsedArgs.physicalNamingStrategy = arg.substring(18);
                    continue;
                }
                if (arg.endsWith(".jar")) {
                    parsedArgs.jarFiles.add(arg);
                    continue;
                }
                parsedArgs.hbmXmlFiles.add(arg);
            }
            return parsedArgs;
        }
    }
}

