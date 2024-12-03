/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.EnumSet;
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
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.TargetTypeHelper;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.internal.ExceptionHandlerCollectingImpl;
import org.hibernate.tool.schema.internal.ExceptionHandlerHaltImpl;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.hibernate.tool.schema.spi.TargetDescriptor;

public class SchemaUpdate {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SchemaUpdate.class);
    private final List<Exception> exceptions = new ArrayList<Exception>();
    boolean haltOnError = false;
    private String outputFile;
    private boolean append = true;
    private String delimiter;
    private boolean format;

    public void execute(EnumSet<TargetType> targetTypes, Metadata metadata) {
        this.execute(targetTypes, metadata, ((MetadataImplementor)metadata).getMetadataBuildingOptions().getServiceRegistry());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute(EnumSet<TargetType> targetTypes, Metadata metadata, ServiceRegistry serviceRegistry) {
        if (targetTypes.isEmpty()) {
            LOG.debug("Skipping SchemaExport as no targets were specified");
            return;
        }
        this.exceptions.clear();
        LOG.runningHbm2ddlSchemaUpdate();
        HashMap<String, Object> config = new HashMap<String, Object>(serviceRegistry.getService(ConfigurationService.class).getSettings());
        config.put("hibernate.hbm2ddl.delimiter", this.delimiter);
        config.put("hibernate.format_sql", this.format);
        SchemaManagementTool tool = serviceRegistry.getService(SchemaManagementTool.class);
        ExceptionHandler exceptionHandler = this.haltOnError ? ExceptionHandlerHaltImpl.INSTANCE : new ExceptionHandlerCollectingImpl();
        ExecutionOptions executionOptions = SchemaManagementToolCoordinator.buildExecutionOptions(config, exceptionHandler);
        TargetDescriptor targetDescriptor = SchemaExport.buildTargetDescriptor(targetTypes, this.outputFile, this.append, serviceRegistry);
        try {
            tool.getSchemaMigrator(config).doMigration(metadata, executionOptions, targetDescriptor);
        }
        finally {
            if (exceptionHandler instanceof ExceptionHandlerCollectingImpl) {
                this.exceptions.addAll(((ExceptionHandlerCollectingImpl)exceptionHandler).getExceptions());
            }
        }
    }

    public List getExceptions() {
        return this.exceptions;
    }

    public SchemaUpdate setHaltOnError(boolean haltOnError) {
        this.haltOnError = haltOnError;
        return this;
    }

    public SchemaUpdate setFormat(boolean format) {
        this.format = format;
        return this;
    }

    public SchemaUpdate setOutputFile(String outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public SchemaUpdate setOverrideOutputFileContent() {
        this.append = false;
        return this;
    }

    public SchemaUpdate setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) {
        try {
            CommandLineArgs parsedArgs = CommandLineArgs.parseCommandLineArgs(args);
            StandardServiceRegistry serviceRegistry = SchemaUpdate.buildStandardServiceRegistry(parsedArgs);
            try {
                MetadataImplementor metadata = SchemaUpdate.buildMetadata(parsedArgs, serviceRegistry);
                new SchemaUpdate().setOutputFile(parsedArgs.outputFile).setDelimiter(parsedArgs.delimiter).execute(parsedArgs.targetTypes, metadata, serviceRegistry);
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
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(parsedArgs.propertiesFile);){
                props.load(fis);
            }
            ssrBuilder.applySettings(props);
        }
        return ssrBuilder.build();
    }

    private static MetadataImplementor buildMetadata(CommandLineArgs parsedArgs, ServiceRegistry serviceRegistry) throws Exception {
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        for (String filename : parsedArgs.hbmXmlFiles) {
            metadataSources.addFile(filename);
        }
        for (String filename : parsedArgs.jarFiles) {
            metadataSources.addJar(new File(filename));
        }
        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
        StrategySelector strategySelector = serviceRegistry.getService(StrategySelector.class);
        if (parsedArgs.implicitNamingStrategyImplName != null) {
            metadataBuilder.applyImplicitNamingStrategy(strategySelector.resolveStrategy(ImplicitNamingStrategy.class, parsedArgs.implicitNamingStrategyImplName));
        }
        if (parsedArgs.physicalNamingStrategyImplName != null) {
            metadataBuilder.applyPhysicalNamingStrategy(strategySelector.resolveStrategy(PhysicalNamingStrategy.class, parsedArgs.physicalNamingStrategyImplName));
        }
        return (MetadataImplementor)metadataBuilder.build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MetadataImplementor buildMetadataFromMainArgs(String[] args) throws Exception {
        CommandLineArgs commandLineArgs = CommandLineArgs.parseCommandLineArgs(args);
        StandardServiceRegistry serviceRegistry = SchemaUpdate.buildStandardServiceRegistry(commandLineArgs);
        try {
            MetadataImplementor metadataImplementor = SchemaUpdate.buildMetadata(commandLineArgs, serviceRegistry);
            return metadataImplementor;
        }
        finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    private static class CommandLineArgs {
        EnumSet<TargetType> targetTypes;
        String propertiesFile = null;
        String cfgXmlFile = null;
        String outputFile = null;
        String delimiter = null;
        String implicitNamingStrategyImplName = null;
        String physicalNamingStrategyImplName = null;
        List<String> hbmXmlFiles = new ArrayList<String>();
        List<String> jarFiles = new ArrayList<String>();

        private CommandLineArgs() {
        }

        public static CommandLineArgs parseCommandLineArgs(String[] args) {
            CommandLineArgs parsedArgs = new CommandLineArgs();
            String targetText = null;
            boolean script = true;
            boolean doUpdate = true;
            for (String arg : args) {
                if (arg.startsWith("--")) {
                    if (arg.equals("--quiet")) {
                        script = false;
                        continue;
                    }
                    if (arg.startsWith("--text")) {
                        doUpdate = false;
                        continue;
                    }
                    if (arg.startsWith("--target=")) {
                        targetText = arg.substring(9);
                        continue;
                    }
                    if (arg.startsWith("--properties=")) {
                        parsedArgs.propertiesFile = arg.substring(13);
                        continue;
                    }
                    if (arg.startsWith("--config=")) {
                        parsedArgs.cfgXmlFile = arg.substring(9);
                        continue;
                    }
                    if (arg.startsWith("--output=")) {
                        parsedArgs.outputFile = arg.substring(9);
                        continue;
                    }
                    if (arg.startsWith("--naming=")) {
                        DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategyArgument();
                        continue;
                    }
                    if (arg.startsWith("--delimiter=")) {
                        parsedArgs.delimiter = arg.substring(12);
                        continue;
                    }
                    if (arg.startsWith("--implicit-naming=")) {
                        parsedArgs.implicitNamingStrategyImplName = arg.substring(18);
                        continue;
                    }
                    if (!arg.startsWith("--physical-naming=")) continue;
                    parsedArgs.physicalNamingStrategyImplName = arg.substring(18);
                    continue;
                }
                if (arg.endsWith(".jar")) {
                    parsedArgs.jarFiles.add(arg);
                    continue;
                }
                parsedArgs.hbmXmlFiles.add(arg);
            }
            if (targetText == null) {
                parsedArgs.targetTypes = TargetTypeHelper.parseLegacyCommandLineOptions(script, doUpdate, parsedArgs.outputFile);
            } else {
                if (!script || !doUpdate) {
                    LOG.warn("--text or --quiet was used; prefer --target=none|(stdout|database|script)*");
                }
                parsedArgs.targetTypes = TargetTypeHelper.parseCommandLineOptions(targetText);
            }
            return parsedArgs;
        }
    }
}

