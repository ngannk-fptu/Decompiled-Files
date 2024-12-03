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
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.TargetTypeHelper;
import org.hibernate.tool.schema.SourceType;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.internal.ExceptionHandlerCollectingImpl;
import org.hibernate.tool.schema.internal.ExceptionHandlerHaltImpl;
import org.hibernate.tool.schema.internal.Helper;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.hibernate.tool.schema.spi.ScriptSourceInput;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.hibernate.tool.schema.spi.SourceDescriptor;
import org.hibernate.tool.schema.spi.TargetDescriptor;

public class SchemaExport {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SchemaExport.class);
    boolean append = true;
    boolean haltOnError = false;
    boolean format = false;
    boolean manageNamespaces = false;
    String delimiter = null;
    String outputFile = null;
    private String importFiles;
    private final List<Exception> exceptions = new ArrayList<Exception>();

    public SchemaExport setOutputFile(String filename) {
        this.outputFile = filename;
        return this;
    }

    public SchemaExport setOverrideOutputFileContent() {
        this.append = false;
        return this;
    }

    public SchemaExport setImportFiles(String importFiles) {
        this.importFiles = importFiles;
        return this;
    }

    public SchemaExport setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public SchemaExport setFormat(boolean format) {
        this.format = format;
        return this;
    }

    public SchemaExport setHaltOnError(boolean haltOnError) {
        this.haltOnError = haltOnError;
        return this;
    }

    public SchemaExport setManageNamespaces(boolean manageNamespaces) {
        this.manageNamespaces = manageNamespaces;
        return this;
    }

    public void drop(EnumSet<TargetType> targetTypes, Metadata metadata) {
        this.execute(targetTypes, Action.DROP, metadata);
    }

    public void create(EnumSet<TargetType> targetTypes, Metadata metadata) {
        this.execute(targetTypes, Action.BOTH, metadata);
    }

    public void createOnly(EnumSet<TargetType> targetTypes, Metadata metadata) {
        this.execute(targetTypes, Action.CREATE, metadata);
    }

    public void execute(EnumSet<TargetType> targetTypes, Action action, Metadata metadata) {
        this.execute(targetTypes, action, metadata, ((MetadataImplementor)metadata).getMetadataBuildingOptions().getServiceRegistry());
    }

    public void execute(EnumSet<TargetType> targetTypes, Action action, Metadata metadata, ServiceRegistry serviceRegistry) {
        if (action == Action.NONE) {
            LOG.debug("Skipping SchemaExport as Action.NONE was passed");
            return;
        }
        if (targetTypes.isEmpty()) {
            LOG.debug("Skipping SchemaExport as no targets were specified");
            return;
        }
        this.exceptions.clear();
        LOG.runningHbm2ddlSchemaExport();
        TargetDescriptor targetDescriptor = SchemaExport.buildTargetDescriptor(targetTypes, this.outputFile, this.append, serviceRegistry);
        this.doExecution(action, this.needsJdbcConnection(targetTypes), metadata, serviceRegistry, targetDescriptor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doExecution(Action action, boolean needsJdbc, Metadata metadata, ServiceRegistry serviceRegistry, TargetDescriptor targetDescriptor) {
        HashMap<String, Object> config = new HashMap<String, Object>(serviceRegistry.getService(ConfigurationService.class).getSettings());
        config.put("hibernate.hbm2ddl.delimiter", this.delimiter);
        config.put("hibernate.format_sql", this.format);
        config.put("hibernate.hbm2ddl.import_files", this.importFiles);
        SchemaManagementTool tool = serviceRegistry.getService(SchemaManagementTool.class);
        ExceptionHandler exceptionHandler = this.haltOnError ? ExceptionHandlerHaltImpl.INSTANCE : new ExceptionHandlerCollectingImpl();
        ExecutionOptions executionOptions = SchemaManagementToolCoordinator.buildExecutionOptions(config, exceptionHandler);
        SourceDescriptor sourceDescriptor = new SourceDescriptor(){

            @Override
            public SourceType getSourceType() {
                return SourceType.METADATA;
            }

            @Override
            public ScriptSourceInput getScriptSourceInput() {
                return null;
            }
        };
        try {
            if (action.doDrop()) {
                tool.getSchemaDropper(config).doDrop(metadata, executionOptions, sourceDescriptor, targetDescriptor);
            }
            if (action.doCreate()) {
                tool.getSchemaCreator(config).doCreation(metadata, executionOptions, sourceDescriptor, targetDescriptor);
            }
        }
        finally {
            if (exceptionHandler instanceof ExceptionHandlerCollectingImpl) {
                this.exceptions.addAll(((ExceptionHandlerCollectingImpl)exceptionHandler).getExceptions());
            }
        }
    }

    private boolean needsJdbcConnection(EnumSet<TargetType> targetTypes) {
        return targetTypes.contains((Object)TargetType.DATABASE);
    }

    public static TargetDescriptor buildTargetDescriptor(EnumSet<TargetType> targetTypes, String outputFile, ServiceRegistry serviceRegistry) {
        return SchemaExport.buildTargetDescriptor(targetTypes, outputFile, true, serviceRegistry);
    }

    public static TargetDescriptor buildTargetDescriptor(EnumSet<TargetType> targetTypes, String outputFile, boolean append, ServiceRegistry serviceRegistry) {
        ScriptTargetOutput scriptTarget;
        if (targetTypes.contains((Object)TargetType.SCRIPT)) {
            if (outputFile == null) {
                throw new SchemaManagementException("Writing to script was requested, but no script file was specified");
            }
            scriptTarget = Helper.interpretScriptTargetSetting(outputFile, serviceRegistry.getService(ClassLoaderService.class), (String)serviceRegistry.getService(ConfigurationService.class).getSettings().get("hibernate.hbm2ddl.charset_name"), append);
        } else {
            scriptTarget = null;
        }
        return new TargetDescriptorImpl(targetTypes, scriptTarget);
    }

    public void perform(Action action, Metadata metadata, ScriptTargetOutput target) {
        this.doExecution(action, false, metadata, ((MetadataImplementor)metadata).getMetadataBuildingOptions().getServiceRegistry(), new TargetDescriptorImpl(EnumSet.of(TargetType.SCRIPT), target));
    }

    public static void main(String[] args) {
        try {
            CommandLineArgs commandLineArgs = CommandLineArgs.parseCommandLineArgs(args);
            SchemaExport.execute(commandLineArgs);
        }
        catch (Exception e) {
            LOG.unableToCreateSchema(e);
        }
    }

    public static void execute(CommandLineArgs commandLineArgs) throws Exception {
        StandardServiceRegistry serviceRegistry = SchemaExport.buildStandardServiceRegistry(commandLineArgs);
        try {
            MetadataImplementor metadata = SchemaExport.buildMetadata(commandLineArgs, serviceRegistry);
            new SchemaExport().setHaltOnError(commandLineArgs.halt).setOutputFile(commandLineArgs.outputFile).setDelimiter(commandLineArgs.delimiter).setFormat(commandLineArgs.format).setManageNamespaces(commandLineArgs.manageNamespaces).setImportFiles(commandLineArgs.importFile).execute(commandLineArgs.targetTypes, commandLineArgs.action, metadata, serviceRegistry);
        }
        finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    private static StandardServiceRegistry buildStandardServiceRegistry(CommandLineArgs commandLineArgs) throws Exception {
        BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
        StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder(bsr);
        if (commandLineArgs.cfgXmlFile != null) {
            ssrBuilder.configure(commandLineArgs.cfgXmlFile);
        }
        Properties properties = new Properties();
        if (commandLineArgs.propertiesFile != null) {
            try (FileInputStream fis = new FileInputStream(commandLineArgs.propertiesFile);){
                properties.load(fis);
            }
        }
        ssrBuilder.applySettings(properties);
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
        StandardServiceRegistry serviceRegistry = SchemaExport.buildStandardServiceRegistry(commandLineArgs);
        try {
            MetadataImplementor metadataImplementor = SchemaExport.buildMetadata(commandLineArgs, serviceRegistry);
            return metadataImplementor;
        }
        finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    public List getExceptions() {
        return this.exceptions;
    }

    private static class TargetDescriptorImpl
    implements TargetDescriptor {
        private final EnumSet<TargetType> targetTypes;
        private final ScriptTargetOutput scriptTarget;

        public TargetDescriptorImpl(EnumSet<TargetType> targetTypes, ScriptTargetOutput scriptTarget) {
            this.targetTypes = targetTypes;
            this.scriptTarget = scriptTarget;
        }

        @Override
        public EnumSet<TargetType> getTargetTypes() {
            return this.targetTypes;
        }

        @Override
        public ScriptTargetOutput getScriptTargetOutput() {
            return this.scriptTarget;
        }
    }

    private static class CommandLineArgs {
        EnumSet<TargetType> targetTypes;
        Action action;
        boolean halt = false;
        boolean format = false;
        boolean manageNamespaces = false;
        String delimiter = null;
        String outputFile = null;
        String importFile = "/import.sql";
        String propertiesFile = null;
        String cfgXmlFile = null;
        String implicitNamingStrategyImplName = null;
        String physicalNamingStrategyImplName = null;
        List<String> hbmXmlFiles = new ArrayList<String>();
        List<String> jarFiles = new ArrayList<String>();

        private CommandLineArgs() {
        }

        public static CommandLineArgs parseCommandLineArgs(String[] args) {
            String targetText = null;
            boolean script = true;
            boolean export = true;
            String actionText = null;
            boolean drop = false;
            boolean create = false;
            CommandLineArgs parsedArgs = new CommandLineArgs();
            for (String arg : args) {
                if (arg.startsWith("--")) {
                    if (arg.equals("--quiet")) {
                        script = false;
                        continue;
                    }
                    if (arg.equals("--text")) {
                        export = false;
                        continue;
                    }
                    if (arg.equals("--drop")) {
                        drop = true;
                        continue;
                    }
                    if (arg.equals("--create")) {
                        create = true;
                        continue;
                    }
                    if (arg.startsWith("--action=")) {
                        actionText = arg.substring(9);
                        continue;
                    }
                    if (arg.startsWith("--target=")) {
                        targetText = arg.substring(9);
                        continue;
                    }
                    if (arg.equals("--schemas")) {
                        parsedArgs.manageNamespaces = true;
                        continue;
                    }
                    if (arg.equals("--haltonerror")) {
                        parsedArgs.halt = true;
                        continue;
                    }
                    if (arg.startsWith("--output=")) {
                        parsedArgs.outputFile = arg.substring(9);
                        continue;
                    }
                    if (arg.startsWith("--import=")) {
                        parsedArgs.importFile = arg.substring(9);
                        continue;
                    }
                    if (arg.startsWith("--properties=")) {
                        parsedArgs.propertiesFile = arg.substring(13);
                        continue;
                    }
                    if (arg.equals("--format")) {
                        parsedArgs.format = true;
                        continue;
                    }
                    if (arg.startsWith("--delimiter=")) {
                        parsedArgs.delimiter = arg.substring(12);
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
            if (actionText == null) {
                parsedArgs.action = Action.interpret(drop, create);
            } else {
                if (drop || create) {
                    LOG.warn("--drop or --create was used; prefer --action=none|create|drop|drop-and-create instead");
                }
                parsedArgs.action = Action.parseCommandLineOption(actionText);
            }
            if (targetText == null) {
                parsedArgs.targetTypes = TargetTypeHelper.parseLegacyCommandLineOptions(script, export, parsedArgs.outputFile);
            } else {
                if (!script || !export) {
                    LOG.warn("--text or --quiet was used; prefer --target=none|(stdout|database|script)*");
                }
                parsedArgs.targetTypes = TargetTypeHelper.parseCommandLineOptions(targetText);
            }
            return parsedArgs;
        }
    }

    public static enum Action {
        NONE,
        CREATE,
        DROP,
        BOTH;


        public boolean doCreate() {
            return this == BOTH || this == CREATE;
        }

        public boolean doDrop() {
            return this == BOTH || this == DROP;
        }

        private static Action interpret(boolean justDrop, boolean justCreate) {
            if (justDrop) {
                return DROP;
            }
            if (justCreate) {
                return CREATE;
            }
            return BOTH;
        }

        public static Action parseCommandLineOption(String actionText) {
            if (actionText.equalsIgnoreCase("create")) {
                return CREATE;
            }
            if (actionText.equalsIgnoreCase("drop")) {
                return DROP;
            }
            if (actionText.equalsIgnoreCase("drop-and-create")) {
                return BOTH;
            }
            return NONE;
        }
    }

    public static enum Type {
        CREATE(Action.CREATE),
        DROP(Action.DROP),
        NONE(Action.NONE),
        BOTH(Action.BOTH);

        private final Action actionReplacement;

        private Type(Action actionReplacement) {
            this.actionReplacement = actionReplacement;
        }

        public boolean doCreate() {
            return this.actionReplacement.doCreate();
        }

        public boolean doDrop() {
            return this.actionReplacement.doDrop();
        }
    }
}

