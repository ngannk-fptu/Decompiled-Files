/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.dto.util;

import com.atlassian.migration.agent.dto.ConfluenceSpaceTaskDto;
import com.atlassian.migration.agent.dto.MigrateUsersTaskDto;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.SpaceAttachmentsTaskDto;
import com.atlassian.migration.agent.dto.SpaceTaskDto;
import com.atlassian.migration.agent.dto.util.UserMigrationType;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PlanDtoUtil {
    private PlanDtoUtil() {
    }

    public static List<String> getSpaceKeysForScope(PlanDto planDto) {
        Optional<MigrateUsersTaskDto> maybeUserTaskDto = PlanDtoUtil.getUserTask(planDto);
        if (maybeUserTaskDto.isPresent() && maybeUserTaskDto.get().isScoped()) {
            return planDto.getTasks().stream().filter(task -> task instanceof ConfluenceSpaceTaskDto).map(task -> ((ConfluenceSpaceTaskDto)task).getSpace()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static boolean containsAttachmentsOnlyTask(PlanDto planDto) {
        return planDto.getTasks().stream().anyMatch(task -> task instanceof SpaceAttachmentsTaskDto);
    }

    public static boolean containsUsersGroupsTask(PlanDto planDto) {
        return planDto.getTasks().stream().anyMatch(MigrateUsersTaskDto.class::isInstance);
    }

    public static boolean hasScopedUserTask(PlanDto planDto) {
        Optional<MigrateUsersTaskDto> maybeUserTask = PlanDtoUtil.getUserTask(planDto);
        if (maybeUserTask.isPresent()) {
            return maybeUserTask.get().isScoped();
        }
        return false;
    }

    public static long calculateTotalSpaceTaskDuration(PlanDto planDto) {
        return planDto.getTasks().stream().filter(task -> task instanceof SpaceAttachmentsTaskDto || task instanceof ConfluenceSpaceTaskDto).map(task -> {
            ProgressDto progressDto = task.getProgress();
            if (progressDto.getStartTime() != null && progressDto.getEndTime() != null) {
                return Duration.between(progressDto.getStartTime(), progressDto.getEndTime());
            }
            return Duration.ZERO;
        }).reduce(Duration.ZERO, Duration::plus).toMillis();
    }

    public static long totalElapsedTimeInSeconds(PlanDto planDto) {
        return Duration.between(planDto.getProgress().getStartTime(), planDto.getProgress().getEndTime()).getSeconds();
    }

    public static UserMigrationType userMigrationType(PlanDto planDto) {
        Optional<MigrateUsersTaskDto> maybeUserTask = PlanDtoUtil.getUserTask(planDto);
        if (maybeUserTask.isPresent()) {
            if (maybeUserTask.get().isScoped()) {
                return UserMigrationType.SCOPED;
            }
            return UserMigrationType.ALL;
        }
        return UserMigrationType.NONE;
    }

    public static Set<String> getSpaceKeys(PlanDto planDto) {
        return planDto.getTasks().stream().filter(task -> task instanceof ConfluenceSpaceTaskDto).map(task -> ((ConfluenceSpaceTaskDto)task).getSpace()).collect(Collectors.toSet());
    }

    public static boolean hasSpaceTask(PlanDto planDto) {
        return planDto.getTasks().stream().anyMatch(SpaceTaskDto.class::isInstance);
    }

    public static boolean hasConfluenceSpaceTask(PlanDto planDto) {
        return planDto.getTasks().stream().anyMatch(ConfluenceSpaceTaskDto.class::isInstance);
    }

    public static boolean isAttachmentOnlyPlan(PlanDto planDto) {
        return planDto.getTasks().stream().allMatch(SpaceAttachmentsTaskDto.class::isInstance);
    }

    private static Optional<MigrateUsersTaskDto> getUserTask(PlanDto planDto) {
        return planDto.getTasks().stream().filter(task -> task instanceof MigrateUsersTaskDto).map(task -> (MigrateUsersTaskDto)task).findAny();
    }
}

