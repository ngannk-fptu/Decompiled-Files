/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.mapi.executor;

import com.atlassian.migration.agent.entity.MapiTaskMapping;
import com.atlassian.migration.agent.mapi.entity.MapiOutcome;
import com.atlassian.migration.agent.mapi.entity.MapiStatus;
import com.atlassian.migration.agent.mapi.entity.MapiStatusDto;
import com.atlassian.migration.agent.mapi.entity.MapiTaskStatus;
import com.atlassian.migration.agent.mapi.executor.MapiStatusTranslator;
import com.atlassian.migration.agent.mapi.external.MapiMigrationService;
import com.atlassian.migration.agent.service.impl.MapiTaskMappingService;
import com.atlassian.migration.agent.service.prc.model.CommandName;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapiStatusSenderService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MapiStatusSenderService.class);
    private final MapiStatusTranslator mapiStatusTranslator;
    private final MapiMigrationService mapiMigrationService;
    private final MapiTaskMappingService mapiTaskMappingService;
    public static final String PREFLIGHT_CHECK_RUNNING = "Preflight checks are running";
    public static final String PREFLIGHT_CHECKS_HAVE_PASSED = "All Preflight checks have passed.";
    public static final String PREFLIGHT_CHECKS_HAVE_PASSED_WITH_WARNING = "All Preflight checks have passed with a warning.";
    public static final String AT_LEAST_ONE_PREFLIGHT_CHECK_HAS_FAILED = "At least one Preflight check has failed.";

    public MapiStatusSenderService(MapiStatusTranslator mapiStatusTranslator, MapiMigrationService mapiMigrationService, MapiTaskMappingService mapiTaskMappingService) {
        this.mapiStatusTranslator = mapiStatusTranslator;
        this.mapiMigrationService = mapiMigrationService;
        this.mapiTaskMappingService = mapiTaskMappingService;
    }

    public void processAndSendMapiTaskStatuses(List<MapiTaskMapping> mapiTaskMappingList) {
        mapiTaskMappingList.forEach(mapiTaskMapping -> {
            try {
                this.sendPreflightCheckStatusesToMapi((MapiTaskMapping)mapiTaskMapping);
            }
            catch (Exception ex) {
                log.debug("Error while sending status to MAPI for task: " + mapiTaskMapping.getTaskId(), (Throwable)ex);
            }
        });
    }

    private void sendPreflightCheckStatusesToMapi(MapiTaskMapping mapiTaskMapping) {
        List<MapiStatusDto> mapiStatusDtoList = this.mapiStatusTranslator.translate(mapiTaskMapping);
        String taskId = mapiTaskMapping.getTaskId();
        String jobId = mapiTaskMapping.getJobId();
        String cloudId = mapiTaskMapping.getCloudId();
        String commandName = mapiTaskMapping.getCommandName();
        ArrayList<MapiStatusDto> overallMapiStatusDtoList = new ArrayList<MapiStatusDto>(mapiStatusDtoList);
        MapiStatusDto overallPreflightCheckStatusDto = this.computeOverallPreflightCheckStatus(mapiStatusDtoList, commandName);
        overallMapiStatusDtoList.add(overallPreflightCheckStatusDto);
        if (commandName.equals(CommandName.MIGRATE.getName())) {
            overallMapiStatusDtoList.add(this.getRootLevelStatusForMigrateTask(overallPreflightCheckStatusDto));
        }
        this.sendTaskStatusToMapi(overallMapiStatusDtoList, jobId, taskId, cloudId);
        if (!overallPreflightCheckStatusDto.getStatus().equals((Object)MapiStatus.IN_PROGRESS)) {
            this.mapiTaskMappingService.updateTaskMappingStatus(mapiTaskMapping, MapiTaskStatus.CHECKS_COMPLETED);
        } else {
            this.mapiTaskMappingService.updateTimestamp(mapiTaskMapping);
        }
    }

    private MapiStatusDto getRootLevelStatusForMigrateTask(MapiStatusDto overallPreflightCheckStatusDto) {
        MapiStatus rootLevelStatus = MapiStatus.IN_PROGRESS;
        MapiOutcome rootLevelOutcome = null;
        if (overallPreflightCheckStatusDto.getStatus().equals((Object)MapiStatus.FINISHED) && overallPreflightCheckStatusDto.getOutcome().equals((Object)MapiOutcome.FAILED)) {
            rootLevelStatus = MapiStatus.FINISHED;
            rootLevelOutcome = MapiOutcome.FAILED;
        }
        MapiStatusDto rootLevelMapiStatusDto = MapiStatusDto.builder().level(Collections.emptyList()).status(rootLevelStatus).outcome(rootLevelOutcome).message(overallPreflightCheckStatusDto.getMessage()).details(overallPreflightCheckStatusDto.getDetails()).build();
        return rootLevelMapiStatusDto;
    }

    void sendTaskStatusToMapi(List<MapiStatusDto> mapiStatusDtoList, String jobId, String taskId, String cloudId) {
        this.mapiMigrationService.sendTaskStatus(jobId, taskId, cloudId, mapiStatusDtoList);
    }

    MapiStatusDto computeOverallPreflightCheckStatus(List<MapiStatusDto> mapiStatusDtoList, String commandName) {
        MapiStatus overallPreFlightCheckStatus = MapiStatus.IN_PROGRESS;
        MapiOutcome overallPreFlightCheckOutcome = null;
        String statusMessage = PREFLIGHT_CHECK_RUNNING;
        Set allCheckStatuses = mapiStatusDtoList.stream().map(MapiStatusDto::getStatus).collect(Collectors.toSet());
        Set allCheckStatusOutcomes = mapiStatusDtoList.stream().map(MapiStatusDto::getOutcome).collect(Collectors.toSet());
        if (!allCheckStatuses.contains((Object)MapiStatus.IN_PROGRESS)) {
            overallPreFlightCheckStatus = MapiStatus.FINISHED;
            overallPreFlightCheckOutcome = MapiOutcome.SUCCESS;
            statusMessage = PREFLIGHT_CHECKS_HAVE_PASSED;
            if (allCheckStatusOutcomes.contains((Object)MapiOutcome.FAILED)) {
                overallPreFlightCheckOutcome = MapiOutcome.FAILED;
                statusMessage = AT_LEAST_ONE_PREFLIGHT_CHECK_HAS_FAILED;
            } else if (allCheckStatusOutcomes.contains((Object)MapiOutcome.WARNING)) {
                overallPreFlightCheckOutcome = MapiOutcome.WARNING;
                statusMessage = PREFLIGHT_CHECKS_HAVE_PASSED_WITH_WARNING;
            }
        }
        ImmutableList level = commandName.equals(CommandName.CHECK.getName()) ? Collections.emptyList() : ImmutableList.of((Object)CommandName.CHECK.getName());
        return new MapiStatusDto((List<String>)level, overallPreFlightCheckStatus, overallPreFlightCheckOutcome, statusMessage, null);
    }
}

