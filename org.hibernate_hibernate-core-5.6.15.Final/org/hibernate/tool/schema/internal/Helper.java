/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.extract.internal.DatabaseInformationImpl;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.internal.exec.ScriptSourceInputAggregate;
import org.hibernate.tool.schema.internal.exec.ScriptSourceInputFromFile;
import org.hibernate.tool.schema.internal.exec.ScriptSourceInputFromReader;
import org.hibernate.tool.schema.internal.exec.ScriptSourceInputFromUrl;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToUrl;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToWriter;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.ScriptSourceInput;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;

public class Helper {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(Helper.class);
    private static final Pattern COMMA_PATTERN = Pattern.compile("\\s*,\\s*");

    public static ScriptSourceInput interpretScriptSourceSetting(Object scriptSourceSetting, ClassLoaderService classLoaderService, String charsetName) {
        if (Reader.class.isInstance(scriptSourceSetting)) {
            return new ScriptSourceInputFromReader((Reader)scriptSourceSetting);
        }
        String scriptSourceSettingString = scriptSourceSetting.toString();
        log.debugf("Attempting to resolve script source setting : %s", scriptSourceSettingString);
        String[] paths = COMMA_PATTERN.split(scriptSourceSettingString);
        if (paths.length == 1) {
            return Helper.interpretScriptSourceSetting(scriptSourceSettingString, classLoaderService, charsetName);
        }
        ScriptSourceInput[] inputs = new ScriptSourceInput[paths.length];
        for (int i = 0; i < paths.length; ++i) {
            inputs[i] = Helper.interpretScriptSourceSetting(paths[i], classLoaderService, charsetName);
        }
        return new ScriptSourceInputAggregate(inputs);
    }

    private static ScriptSourceInput interpretScriptSourceSetting(String scriptSourceSettingString, ClassLoaderService classLoaderService, String charsetName) {
        log.trace("Trying as URL...");
        URL url = classLoaderService.locateResource(scriptSourceSettingString);
        if (url != null) {
            return new ScriptSourceInputFromUrl(url, charsetName);
        }
        File file = new File(scriptSourceSettingString);
        return new ScriptSourceInputFromFile(file, charsetName);
    }

    public static ScriptTargetOutput interpretScriptTargetSetting(Object scriptTargetSetting, ClassLoaderService classLoaderService, String charsetName, boolean append) {
        if (scriptTargetSetting == null) {
            return null;
        }
        if (Writer.class.isInstance(scriptTargetSetting)) {
            return new ScriptTargetOutputToWriter((Writer)scriptTargetSetting);
        }
        String scriptTargetSettingString = scriptTargetSetting.toString();
        log.debugf("Attempting to resolve script source setting : %s", scriptTargetSettingString);
        log.trace("Trying as URL...");
        URL url = classLoaderService.locateResource(scriptTargetSettingString);
        if (url != null) {
            return new ScriptTargetOutputToUrl(url, charsetName);
        }
        File file = new File(scriptTargetSettingString);
        return new ScriptTargetOutputToFile(file, charsetName, append);
    }

    public static boolean interpretNamespaceHandling(Map configurationValues) {
        int count = 0;
        if (configurationValues.containsKey("javax.persistence.create-database-schemas")) {
            ++count;
        }
        if (configurationValues.containsKey("jakarta.persistence.create-database-schemas")) {
            ++count;
        }
        if (configurationValues.containsKey("hibernate.hbm2ddl.create_namespaces")) {
            ++count;
        }
        if (configurationValues.containsKey("hibernate.hbm2dll.create_namespaces")) {
            ++count;
        }
        if (count > 1) {
            log.multipleSchemaCreationSettingsDefined();
        }
        return ConfigurationHelper.getBoolean("javax.persistence.create-database-schemas", configurationValues, ConfigurationHelper.getBoolean("jakarta.persistence.create-database-schemas", configurationValues, ConfigurationHelper.getBoolean("hibernate.hbm2ddl.create_namespaces", configurationValues, ConfigurationHelper.getBoolean("hibernate.hbm2dll.create_namespaces", configurationValues, false))));
    }

    public static boolean interpretFormattingEnabled(Map configurationValues) {
        return ConfigurationHelper.getBoolean("hibernate.format_sql", configurationValues, false);
    }

    public static DatabaseInformation buildDatabaseInformation(ServiceRegistry serviceRegistry, DdlTransactionIsolator ddlTransactionIsolator, SqlStringGenerationContext sqlStringGenerationContext, SchemaManagementTool tool) {
        JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        try {
            return new DatabaseInformationImpl(serviceRegistry, jdbcEnvironment, sqlStringGenerationContext, ddlTransactionIsolator, tool);
        }
        catch (SQLException e) {
            throw jdbcEnvironment.getSqlExceptionHelper().convert(e, "Unable to build DatabaseInformation");
        }
    }
}

