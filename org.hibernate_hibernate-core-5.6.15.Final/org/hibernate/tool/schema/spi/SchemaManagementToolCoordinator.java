/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.spi;

import java.util.EnumSet;
import java.util.Map;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.internal.util.NullnessHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.SourceType;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.internal.ExceptionHandlerHaltImpl;
import org.hibernate.tool.schema.internal.ExceptionHandlerLoggedImpl;
import org.hibernate.tool.schema.internal.Helper;
import org.hibernate.tool.schema.spi.DelayedDropRegistry;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.JpaTargetAndSourceDescriptor;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.ScriptSourceInput;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.jboss.logging.Logger;

public class SchemaManagementToolCoordinator {
    private static final Logger log = Logger.getLogger(SchemaManagementToolCoordinator.class);

    public static void process(Metadata metadata, ServiceRegistry serviceRegistry, Map configurationValues, DelayedDropRegistry delayedDropRegistry) {
        ActionGrouping actions = ActionGrouping.interpret(configurationValues);
        if (actions.getDatabaseAction() == Action.NONE && actions.getScriptAction() == Action.NONE) {
            log.debug((Object)"No actions specified; doing nothing");
            return;
        }
        SchemaManagementTool tool = serviceRegistry.getService(SchemaManagementTool.class);
        ConfigurationService configService = serviceRegistry.getService(ConfigurationService.class);
        boolean haltOnError = configService.getSetting("hibernate.hbm2ddl.halt_on_error", StandardConverters.BOOLEAN, Boolean.valueOf(false));
        ExecutionOptions executionOptions = SchemaManagementToolCoordinator.buildExecutionOptions(configurationValues, haltOnError ? ExceptionHandlerHaltImpl.INSTANCE : ExceptionHandlerLoggedImpl.INSTANCE);
        SchemaManagementToolCoordinator.performScriptAction(actions.getScriptAction(), metadata, tool, serviceRegistry, executionOptions, configService);
        SchemaManagementToolCoordinator.performDatabaseAction(actions.getDatabaseAction(), metadata, tool, serviceRegistry, executionOptions);
        if (actions.getDatabaseAction() == Action.CREATE_DROP) {
            delayedDropRegistry.registerOnCloseAction(tool.getSchemaDropper(configurationValues).buildDelayedAction(metadata, executionOptions, SchemaManagementToolCoordinator.buildDatabaseTargetDescriptor(configurationValues, DropSettingSelector.INSTANCE, serviceRegistry)));
        }
    }

    public static ExecutionOptions buildExecutionOptions(final Map configurationValues, final ExceptionHandler exceptionHandler) {
        return new ExecutionOptions(){

            @Override
            public boolean shouldManageNamespaces() {
                return Helper.interpretNamespaceHandling(configurationValues);
            }

            @Override
            public Map getConfigurationValues() {
                return configurationValues;
            }

            @Override
            public ExceptionHandler getExceptionHandler() {
                return exceptionHandler;
            }
        };
    }

    private static void performDatabaseAction(Action action, Metadata metadata, SchemaManagementTool tool, ServiceRegistry serviceRegistry, ExecutionOptions executionOptions) {
        switch (action) {
            case CREATE_ONLY: {
                JpaTargetAndSourceDescriptor createDescriptor = SchemaManagementToolCoordinator.buildDatabaseTargetDescriptor(executionOptions.getConfigurationValues(), CreateSettingSelector.INSTANCE, serviceRegistry);
                tool.getSchemaCreator(executionOptions.getConfigurationValues()).doCreation(metadata, executionOptions, createDescriptor, createDescriptor);
                break;
            }
            case CREATE: 
            case CREATE_DROP: {
                JpaTargetAndSourceDescriptor dropDescriptor = SchemaManagementToolCoordinator.buildDatabaseTargetDescriptor(executionOptions.getConfigurationValues(), DropSettingSelector.INSTANCE, serviceRegistry);
                tool.getSchemaDropper(executionOptions.getConfigurationValues()).doDrop(metadata, executionOptions, dropDescriptor, dropDescriptor);
                JpaTargetAndSourceDescriptor createDescriptor = SchemaManagementToolCoordinator.buildDatabaseTargetDescriptor(executionOptions.getConfigurationValues(), CreateSettingSelector.INSTANCE, serviceRegistry);
                tool.getSchemaCreator(executionOptions.getConfigurationValues()).doCreation(metadata, executionOptions, createDescriptor, createDescriptor);
                break;
            }
            case DROP: {
                JpaTargetAndSourceDescriptor dropDescriptor = SchemaManagementToolCoordinator.buildDatabaseTargetDescriptor(executionOptions.getConfigurationValues(), DropSettingSelector.INSTANCE, serviceRegistry);
                tool.getSchemaDropper(executionOptions.getConfigurationValues()).doDrop(metadata, executionOptions, dropDescriptor, dropDescriptor);
                break;
            }
            case UPDATE: {
                JpaTargetAndSourceDescriptor migrateDescriptor = SchemaManagementToolCoordinator.buildDatabaseTargetDescriptor(executionOptions.getConfigurationValues(), MigrateSettingSelector.INSTANCE, serviceRegistry);
                tool.getSchemaMigrator(executionOptions.getConfigurationValues()).doMigration(metadata, executionOptions, migrateDescriptor);
                break;
            }
            case VALIDATE: {
                tool.getSchemaValidator(executionOptions.getConfigurationValues()).doValidation(metadata, executionOptions);
            }
        }
    }

    private static JpaTargetAndSourceDescriptor buildDatabaseTargetDescriptor(Map configurationValues, SettingSelector settingSelector, ServiceRegistry serviceRegistry) {
        boolean includesScripts;
        Object scriptSourceSetting = settingSelector.getScriptSourceSetting(configurationValues);
        final SourceType sourceType = SourceType.interpret(settingSelector.getSourceTypeSetting(configurationValues), scriptSourceSetting != null ? SourceType.SCRIPT : SourceType.METADATA);
        boolean bl = includesScripts = sourceType != SourceType.METADATA;
        if (includesScripts && scriptSourceSetting == null) {
            throw new SchemaManagementException("Schema generation configuration indicated to include CREATE scripts, but no script was specified");
        }
        final ScriptSourceInput scriptSourceInput = includesScripts ? Helper.interpretScriptSourceSetting(scriptSourceSetting, serviceRegistry.getService(ClassLoaderService.class), (String)configurationValues.get("hibernate.hbm2ddl.charset_name")) : null;
        return new JpaTargetAndSourceDescriptor(){

            @Override
            public EnumSet<TargetType> getTargetTypes() {
                return EnumSet.of(TargetType.DATABASE);
            }

            @Override
            public ScriptTargetOutput getScriptTargetOutput() {
                return null;
            }

            @Override
            public SourceType getSourceType() {
                return sourceType;
            }

            @Override
            public ScriptSourceInput getScriptSourceInput() {
                return scriptSourceInput;
            }
        };
    }

    private static void performScriptAction(Action scriptAction, Metadata metadata, SchemaManagementTool tool, ServiceRegistry serviceRegistry, ExecutionOptions executionOptions, ConfigurationService configurationService) {
        switch (scriptAction) {
            case CREATE_ONLY: {
                JpaTargetAndSourceDescriptor createDescriptor = SchemaManagementToolCoordinator.buildScriptTargetDescriptor(executionOptions.getConfigurationValues(), CreateSettingSelector.INSTANCE, serviceRegistry, configurationService);
                tool.getSchemaCreator(executionOptions.getConfigurationValues()).doCreation(metadata, executionOptions, createDescriptor, createDescriptor);
                break;
            }
            case CREATE: 
            case CREATE_DROP: {
                JpaTargetAndSourceDescriptor dropDescriptor = SchemaManagementToolCoordinator.buildScriptTargetDescriptor(executionOptions.getConfigurationValues(), DropSettingSelector.INSTANCE, serviceRegistry, configurationService);
                tool.getSchemaDropper(executionOptions.getConfigurationValues()).doDrop(metadata, executionOptions, dropDescriptor, dropDescriptor);
                JpaTargetAndSourceDescriptor createDescriptor = SchemaManagementToolCoordinator.buildScriptTargetDescriptor(executionOptions.getConfigurationValues(), CreateSettingSelector.INSTANCE, serviceRegistry, configurationService);
                tool.getSchemaCreator(executionOptions.getConfigurationValues()).doCreation(metadata, executionOptions, createDescriptor, createDescriptor);
                break;
            }
            case DROP: {
                JpaTargetAndSourceDescriptor dropDescriptor = SchemaManagementToolCoordinator.buildScriptTargetDescriptor(executionOptions.getConfigurationValues(), DropSettingSelector.INSTANCE, serviceRegistry, configurationService);
                tool.getSchemaDropper(executionOptions.getConfigurationValues()).doDrop(metadata, executionOptions, dropDescriptor, dropDescriptor);
                break;
            }
            case UPDATE: {
                JpaTargetAndSourceDescriptor migrateDescriptor = SchemaManagementToolCoordinator.buildScriptTargetDescriptor(executionOptions.getConfigurationValues(), MigrateSettingSelector.INSTANCE, serviceRegistry, configurationService);
                tool.getSchemaMigrator(executionOptions.getConfigurationValues()).doMigration(metadata, executionOptions, migrateDescriptor);
                break;
            }
            case VALIDATE: {
                throw new SchemaManagementException("VALIDATE is not valid SchemaManagementTool action for script output");
            }
        }
    }

    private static JpaTargetAndSourceDescriptor buildScriptTargetDescriptor(Map configurationValues, SettingSelector settingSelector, ServiceRegistry serviceRegistry, ConfigurationService configurationService) {
        boolean includesScripts;
        Object scriptSourceSetting = settingSelector.getScriptSourceSetting(configurationValues);
        final SourceType sourceType = SourceType.interpret(settingSelector.getSourceTypeSetting(configurationValues), scriptSourceSetting != null ? SourceType.SCRIPT : SourceType.METADATA);
        boolean bl = includesScripts = sourceType != SourceType.METADATA;
        if (includesScripts && scriptSourceSetting == null) {
            throw new SchemaManagementException("Schema generation configuration indicated to include CREATE scripts, but no script was specified");
        }
        String charsetName = (String)configurationValues.get("hibernate.hbm2ddl.charset_name");
        final ScriptSourceInput scriptSourceInput = includesScripts ? Helper.interpretScriptSourceSetting(scriptSourceSetting, serviceRegistry.getService(ClassLoaderService.class), charsetName) : null;
        boolean append = configurationService.getSetting("hibernate.hbm2ddl.schema-generation.script.append", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        final ScriptTargetOutput scriptTargetOutput = Helper.interpretScriptTargetSetting(settingSelector.getScriptTargetSetting(configurationValues), serviceRegistry.getService(ClassLoaderService.class), charsetName, append);
        return new JpaTargetAndSourceDescriptor(){

            @Override
            public EnumSet<TargetType> getTargetTypes() {
                return EnumSet.of(TargetType.SCRIPT);
            }

            @Override
            public ScriptTargetOutput getScriptTargetOutput() {
                return scriptTargetOutput;
            }

            @Override
            public SourceType getSourceType() {
                return sourceType;
            }

            @Override
            public ScriptSourceInput getScriptSourceInput() {
                return scriptSourceInput;
            }
        };
    }

    public static class ActionGrouping {
        private final Action databaseAction;
        private final Action scriptAction;

        public ActionGrouping(Action databaseAction, Action scriptAction) {
            this.databaseAction = databaseAction;
            this.scriptAction = scriptAction;
        }

        public Action getDatabaseAction() {
            return this.databaseAction;
        }

        public Action getScriptAction() {
            return this.scriptAction;
        }

        public static ActionGrouping interpret(Map configurationValues) {
            Action databaseActionToUse = ActionGrouping.determineJpaDbActionSetting(configurationValues);
            Action scriptActionToUse = ActionGrouping.determineJpaScriptActionSetting(configurationValues);
            Action autoAction = ActionGrouping.determineAutoSettingImpliedAction(configurationValues, null);
            if (databaseActionToUse == null && scriptActionToUse == null && autoAction != null) {
                databaseActionToUse = autoAction;
            }
            if (databaseActionToUse == null) {
                databaseActionToUse = Action.NONE;
            }
            if (scriptActionToUse == null) {
                scriptActionToUse = Action.NONE;
            }
            if (databaseActionToUse == Action.NONE && scriptActionToUse == Action.NONE) {
                log.debugf("No schema actions specified", new Object[0]);
            }
            return new ActionGrouping(databaseActionToUse, scriptActionToUse);
        }

        private static Action determineJpaDbActionSetting(Map<?, ?> configurationValues) {
            Object scriptsActionSetting = NullnessHelper.coalesceSuppliedValues(() -> configurationValues.get("jakarta.persistence.schema-generation.database.action"), () -> {
                Object setting = configurationValues.get("javax.persistence.schema-generation.database.action");
                return setting;
            });
            return scriptsActionSetting == null ? null : Action.interpretJpaSetting(scriptsActionSetting);
        }

        private static Action determineJpaScriptActionSetting(Map<?, ?> configurationValues) {
            Object scriptsActionSetting = NullnessHelper.coalesceSuppliedValues(() -> configurationValues.get("jakarta.persistence.schema-generation.scripts.action"), () -> {
                Object setting = configurationValues.get("javax.persistence.schema-generation.scripts.action");
                return setting;
            });
            return scriptsActionSetting == null ? null : Action.interpretJpaSetting(scriptsActionSetting);
        }

        public static Action determineAutoSettingImpliedAction(Map<?, ?> settings, Action defaultValue) {
            Object autoActionSetting = settings.get("hibernate.hbm2ddl.auto");
            if (autoActionSetting == null) {
                return defaultValue;
            }
            return Action.interpretHbm2ddlSetting(autoActionSetting);
        }
    }

    private static class MigrateSettingSelector
    implements SettingSelector {
        public static final MigrateSettingSelector INSTANCE = new MigrateSettingSelector();

        private MigrateSettingSelector() {
        }

        @Override
        public Object getSourceTypeSetting(Map configurationValues) {
            return SourceType.METADATA;
        }

        @Override
        public Object getScriptSourceSetting(Map configurationValues) {
            return null;
        }

        @Override
        public Object getScriptTargetSetting(Map configurationValues) {
            Object setting = configurationValues.get("javax.persistence.schema-generation.scripts.create-target");
            if (setting == null) {
                setting = configurationValues.get("jakarta.persistence.schema-generation.scripts.create-target");
            }
            return setting;
        }
    }

    private static class DropSettingSelector
    implements SettingSelector {
        public static final DropSettingSelector INSTANCE = new DropSettingSelector();

        private DropSettingSelector() {
        }

        @Override
        public Object getSourceTypeSetting(Map configurationValues) {
            Object setting = configurationValues.get("javax.persistence.schema-generation.drop-source");
            if (setting == null) {
                setting = configurationValues.get("jakarta.persistence.schema-generation.drop-source");
            }
            return setting;
        }

        @Override
        public Object getScriptSourceSetting(Map configurationValues) {
            Object setting = configurationValues.get("javax.persistence.schema-generation.drop-script-source");
            if (setting == null) {
                setting = configurationValues.get("jakarta.persistence.schema-generation.drop-script-source");
            }
            return setting;
        }

        @Override
        public Object getScriptTargetSetting(Map configurationValues) {
            Object setting = configurationValues.get("javax.persistence.schema-generation.scripts.drop-target");
            if (setting == null) {
                setting = configurationValues.get("jakarta.persistence.schema-generation.scripts.drop-target");
            }
            return setting;
        }
    }

    private static class CreateSettingSelector
    implements SettingSelector {
        public static final CreateSettingSelector INSTANCE = new CreateSettingSelector();

        private CreateSettingSelector() {
        }

        @Override
        public Object getSourceTypeSetting(Map configurationValues) {
            Object setting = configurationValues.get("javax.persistence.schema-generation.create-source");
            if (setting == null) {
                setting = configurationValues.get("jakarta.persistence.schema-generation.create-source");
            }
            return setting;
        }

        @Override
        public Object getScriptSourceSetting(Map configurationValues) {
            Object setting = configurationValues.get("javax.persistence.schema-generation.create-script-source");
            if (setting == null) {
                setting = configurationValues.get("jakarta.persistence.schema-generation.create-script-source");
            }
            return setting;
        }

        @Override
        public Object getScriptTargetSetting(Map configurationValues) {
            Object setting = configurationValues.get("javax.persistence.schema-generation.scripts.create-target");
            if (setting == null) {
                setting = configurationValues.get("jakarta.persistence.schema-generation.scripts.create-target");
            }
            return setting;
        }
    }

    private static interface SettingSelector {
        public Object getSourceTypeSetting(Map var1);

        public Object getScriptSourceSetting(Map var1);

        public Object getScriptTargetSetting(Map var1);
    }
}

