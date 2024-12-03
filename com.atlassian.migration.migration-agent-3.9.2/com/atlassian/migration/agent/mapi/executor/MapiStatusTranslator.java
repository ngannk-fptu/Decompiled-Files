/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.google.common.collect.ImmutableList
 *  lombok.Generated
 *  org.apache.commons.lang3.tuple.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.mapi.executor;

import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.migration.agent.entity.MapiTaskMapping;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.mapi.entity.MapiCheckDetailsDto;
import com.atlassian.migration.agent.mapi.entity.MapiOutcome;
import com.atlassian.migration.agent.mapi.entity.MapiStatus;
import com.atlassian.migration.agent.mapi.entity.MapiStatusDto;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.PreflightService;
import com.atlassian.migration.agent.service.prc.model.CommandName;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Generated;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapiStatusTranslator {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MapiStatusTranslator.class);
    private final PreflightService preflightService;
    public static final int MAX_OCCURRENCES_TO_SEND = 100;

    public MapiStatusTranslator(PreflightService preflightService) {
        this.preflightService = preflightService;
    }

    public List<MapiStatusDto> translate(MapiTaskMapping mapiTaskMapping) {
        String planId = mapiTaskMapping.getPlanId();
        String commandName = mapiTaskMapping.getCommandName();
        List<CheckResultDto> results = this.preflightService.getCheckExecutionStatus(planId);
        return results.stream().map(checkResultDto -> this.convertCheckResultDtoToMapiStatusDto((CheckResultDto)checkResultDto, commandName)).collect(Collectors.toList());
    }

    private MapiStatusDto convertCheckResultDtoToMapiStatusDto(CheckResultDto checkResultDto, String commandName) {
        Pair<MapiStatus, MapiOutcome> mapiStatus = this.convertCheckResultStatusToMapiStatus(checkResultDto.getStatus());
        MapiCheckDetailsDto mapiCheckDetailsDto = null;
        List<String> occurrencesAsString = this.getCheckResultDetails(checkResultDto);
        if (occurrencesAsString != null && !occurrencesAsString.isEmpty()) {
            mapiCheckDetailsDto = new MapiCheckDetailsDto(occurrencesAsString);
        }
        ImmutableList level = commandName.equals(CommandName.CHECK.getName()) ? ImmutableList.of((Object)checkResultDto.getCheckType()) : ImmutableList.of((Object)"check", (Object)checkResultDto.getCheckType());
        return new MapiStatusDto((List<String>)level, (MapiStatus)((Object)mapiStatus.getLeft()), (MapiOutcome)((Object)mapiStatus.getRight()), checkResultDto.getDescription(), mapiCheckDetailsDto);
    }

    private List<String> getCheckResultDetails(CheckResultDto checkResultDto) {
        List listOfOccurrences;
        List list = listOfOccurrences = checkResultDto.getDetails() != null ? checkResultDto.getDetails().getListOfOccurrences() : null;
        if (listOfOccurrences == null || listOfOccurrences.isEmpty()) {
            return Collections.emptyList();
        }
        String checkType = checkResultDto.getCheckType();
        boolean allStrings = listOfOccurrences.stream().allMatch(element -> element instanceof String);
        if (allStrings) {
            return listOfOccurrences.stream().map(element -> (String)element).limit(100L).collect(Collectors.toList());
        }
        if (checkType.equals(CheckType.INVALID_EMAILS.value()) || checkType.equals(CheckType.SHARED_EMAILS.value())) {
            return this.getOccurrencesWithSpecificAttribute(listOfOccurrences, "email");
        }
        List<String> occurrencesWithKey = this.getOccurrencesWithSpecificAttribute(listOfOccurrences, "key");
        if (occurrencesWithKey.isEmpty()) {
            return this.getOccurrencesWithSpecificAttribute(listOfOccurrences, "name");
        }
        return occurrencesWithKey;
    }

    private List<String> getOccurrencesWithSpecificAttribute(List<Object> listOfOccurrences, String attribute) {
        ArrayList<String> occurrences = new ArrayList<String>();
        for (Object detail : listOfOccurrences) {
            try {
                Map map = (Map)Jsons.OBJECT_MAPPER.convertValue(detail, Map.class);
                if (map.containsKey(attribute)) {
                    occurrences.add(map.get(attribute).toString());
                }
                if (occurrences.size() < 100) continue;
                return occurrences;
            }
            catch (Exception e) {
                log.error("Unable to convert listOfOccurrences to map", (Throwable)e);
                return Collections.emptyList();
            }
        }
        return occurrences;
    }

    private Pair<MapiStatus, MapiOutcome> convertCheckResultStatusToMapiStatus(Status status) {
        switch (status) {
            case RUNNING: {
                return Pair.of((Object)((Object)MapiStatus.IN_PROGRESS), null);
            }
            case SUCCESS: {
                return Pair.of((Object)((Object)MapiStatus.FINISHED), (Object)((Object)MapiOutcome.SUCCESS));
            }
            case WARNING: {
                return Pair.of((Object)((Object)MapiStatus.FINISHED), (Object)((Object)MapiOutcome.WARNING));
            }
            case ERROR: 
            case EXECUTION_ERROR: {
                return Pair.of((Object)((Object)MapiStatus.FINISHED), (Object)((Object)MapiOutcome.FAILED));
            }
        }
        throw new IllegalArgumentException("Unknown execution status " + status.name());
    }
}

