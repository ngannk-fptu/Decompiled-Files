/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.macro.schema;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.macro.schema.MacroSchemaMigrator;
import com.atlassian.confluence.impl.macro.schema.MacroSchemaMigratorModuleDescriptor;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DefaultMacroSchemaMigrator
implements MacroSchemaMigrator {
    private PluginAccessor pluginAccessor;
    private final Ordering<MacroSchemaMigratorModuleDescriptor> versionOrdering = new Ordering<MacroSchemaMigratorModuleDescriptor>(){

        public int compare(@NonNull MacroSchemaMigratorModuleDescriptor left, @NonNull MacroSchemaMigratorModuleDescriptor right) {
            return left.getSchemaVersion() - right.getSchemaVersion();
        }
    };

    public DefaultMacroSchemaMigrator(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public MacroDefinition migrateSchemaIfNecessary(MacroDefinition macroDefinition, ConversionContext context) throws XhtmlException {
        for (MacroSchemaMigratorModuleDescriptor migrationModuleDescriptor : this.getSortedMigrationsForMacro(macroDefinition)) {
            int incomingVersion = macroDefinition.getSchemaVersion();
            if (incomingVersion != migrationModuleDescriptor.getSchemaVersion()) continue;
            try {
                macroDefinition = this.migrateSchema(migrationModuleDescriptor.getModule(), macroDefinition, context);
            }
            catch (Exception e) {
                ContentEntityObject entity;
                ContentId contentId = null;
                if (context != null && (entity = context.getEntity()) != null) {
                    contentId = entity.getContentId();
                }
                StringBuilder message = new StringBuilder(e.getMessage());
                for (Throwable cause = e.getCause(); cause != null; cause = cause.getCause()) {
                    message.append(" > ").append(cause.getMessage());
                }
                String msg = String.format("Error migrating macro '%s' in content '%s' from schema version %s. Message: %s", macroDefinition.getName(), contentId, incomingVersion, message);
                throw new XhtmlException(msg, e);
            }
        }
        return macroDefinition;
    }

    private MacroDefinition migrateSchema(MacroMigration macroMigration, MacroDefinition macroDefinition, ConversionContext context) {
        MacroDefinition copyOfDefinition = new MacroDefinition(macroDefinition);
        MacroDefinition migratedDefinition = macroMigration.migrate(copyOfDefinition, context);
        if (migratedDefinition.getSchemaVersion() <= macroDefinition.getSchemaVersion()) {
            throw new IllegalStateException("Migrated macro definition did not increase macro definition schema version");
        }
        return migratedDefinition;
    }

    private List<MacroSchemaMigratorModuleDescriptor> getSortedMigrationsForMacro(MacroDefinition macroDefinition) {
        List moduleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(MacroSchemaMigratorModuleDescriptor.class);
        return this.versionOrdering.immutableSortedCopy(Iterables.filter((Iterable)moduleDescriptors, moduleDescriptor -> moduleDescriptor.getSchemaVersion() >= macroDefinition.getSchemaVersion() && moduleDescriptor.getMacroName().equals(macroDefinition.getName())));
    }
}

