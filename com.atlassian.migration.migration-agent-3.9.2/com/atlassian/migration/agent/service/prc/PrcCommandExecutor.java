/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.prc.model.Command
 *  com.google.common.collect.ImmutableMap
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.prc;

import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.mapi.executor.CloudExecutorService;
import com.atlassian.migration.agent.service.prc.model.CommandName;
import com.atlassian.migration.agent.service.prc.model.CommandPayload;
import com.atlassian.migration.prc.model.Command;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrcCommandExecutor {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(PrcCommandExecutor.class);
    private CloudExecutorService cloudExecutorService;
    private final Map<String, Consumer<Command>> commandExecutorMap = ImmutableMap.of((Object)CommandName.CHECK.getName(), this::executeCheckCommand, (Object)CommandName.MIGRATE.getName(), this::executeMigrateCommand);

    public PrcCommandExecutor(CloudExecutorService cloudExecutorService) {
        this.cloudExecutorService = cloudExecutorService;
    }

    public void executeCommand(Command command) {
        try {
            this.commandExecutorMap.get(command.getName().toLowerCase()).accept(command);
        }
        catch (NullPointerException e) {
            throw new IllegalArgumentException("Unknown command: " + command.getName());
        }
        catch (Exception e) {
            log.error("Failed to execute command: " + command.getName(), (Throwable)e);
        }
    }

    private void executeCheckCommand(Command command) {
        String jobId = this.getJobId(command.getPayload());
        String taskId = this.getTaskId(command.getPayload());
        String cloudId = this.getCloudId(command.getPayload());
        this.cloudExecutorService.executePreflightChecks(jobId, taskId, cloudId);
    }

    private void executeMigrateCommand(Command command) {
        String jobId = this.getJobId(command.getPayload());
        String taskId = this.getTaskId(command.getPayload());
        String cloudId = this.getCloudId(command.getPayload());
        this.cloudExecutorService.executeMigration(jobId, taskId, cloudId);
    }

    private String getJobId(String payload) {
        CommandPayload commandPayload = Jsons.readValue(payload, CommandPayload.class);
        return commandPayload.getJobId();
    }

    private String getTaskId(String payload) {
        CommandPayload commandPayload = Jsons.readValue(payload, CommandPayload.class);
        return commandPayload.getTaskId();
    }

    private String getCloudId(String payload) {
        CommandPayload commandPayload = Jsons.readValue(payload, CommandPayload.class);
        return commandPayload.getDestId();
    }
}

