/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.rest.dto;

public enum NodeState implements com.atlassian.zdu.internal.api.NodeState
{
    STARTING,
    ACTIVE,
    DRAINING,
    TERMINATING,
    OFFLINE,
    ERROR,
    RUNNING_FINALIZE_UPGRADE_TASKS,
    UPGRADE_TASKS_FAILED;

}

