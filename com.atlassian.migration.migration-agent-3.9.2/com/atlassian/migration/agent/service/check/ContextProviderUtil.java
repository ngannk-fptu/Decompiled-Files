/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.migration.agent.entity.GlobalEntityType;
import java.util.Map;
import java.util.Optional;

public class ContextProviderUtil {
    private static final String SPACE_KEY_ERROR = "Space keys not found in the parameters.";
    private static final String CLOUD_ID_ERROR = "CloudId not found in the parameters.";
    private static final String EXECUTION_ID_ERROR = "Execution id not found in the parameters.";
    private static final String CLOUD_ID_KEY = "cloudId";
    private static final String COMMA_DELIMITER = ",";
    public static final String SPACE_KEYS_KEY = "spaceKeys";
    public static final String PLAN_ID_KEY = "planId";
    private static final String PLAN_ID_ERROR = "PlanId not found in the parameters.";
    public static final String PLAN_NAME_KEY = "planName";
    private static final String PLAN_NAME_ERROR = "PlanName not found in the parameters.";
    private static final String PLAN_MIGRATION_TAG = "planMigrationTag";
    public static final String EXECUTION_ID = "executionId";
    public static final String APPS_KEY = "appsKey";
    private static final String APPS_KEY_ERROR = "Apps key not found in the parameters.";
    public static final String TEMPLATE_TYPES_KEY = "templateTypes";
    private static final String TEMPLATE_TYPE_ERROR = "Template type not found in parameters";

    private ContextProviderUtil() {
        throw new IllegalStateException("ContextProviderUtil should not be instantiated.");
    }

    public static String[] getSpaceKeys(Map<String, Object> parameters) {
        return ContextProviderUtil.getValueFromParams(parameters, SPACE_KEYS_KEY, SPACE_KEY_ERROR).split(COMMA_DELIMITER);
    }

    public static String getGlobalEntityType(Map<String, Object> parameters) {
        return ContextProviderUtil.getValueFromParams(parameters, TEMPLATE_TYPES_KEY, TEMPLATE_TYPE_ERROR);
    }

    public static boolean containsSpaceKeys(Map<String, Object> parameters) {
        return parameters.containsKey(SPACE_KEYS_KEY);
    }

    public static Optional<GlobalEntityType> checkAndGetGlobalEntityType(Map<String, Object> parameters) {
        if (parameters.containsKey(TEMPLATE_TYPES_KEY)) {
            return Optional.of(GlobalEntityType.valueOf(ContextProviderUtil.getGlobalEntityType(parameters)));
        }
        return Optional.empty();
    }

    public static String getCloudId(Map<String, Object> parameters) {
        return ContextProviderUtil.getValueFromParams(parameters, CLOUD_ID_KEY, CLOUD_ID_ERROR);
    }

    public static String getExecutionId(Map<String, Object> parameters) {
        return ContextProviderUtil.getValueFromParams(parameters, EXECUTION_ID, EXECUTION_ID_ERROR);
    }

    public static String getPlanId(Map<String, Object> parameters) {
        return ContextProviderUtil.getValueFromParams(parameters, PLAN_ID_KEY, PLAN_ID_ERROR);
    }

    public static String getPlanName(Map<String, Object> parameters) {
        return ContextProviderUtil.getValueFromParams(parameters, PLAN_NAME_KEY, PLAN_NAME_ERROR);
    }

    public static String getPlanMigrationTag(Map<String, Object> parameters) {
        return (String)parameters.get(PLAN_MIGRATION_TAG);
    }

    public static String[] getAppsKey(Map<String, Object> parameters) {
        String appKeys = ContextProviderUtil.getValueFromParams(parameters, APPS_KEY, APPS_KEY_ERROR);
        return appKeys.equals("") ? new String[]{} : appKeys.split(COMMA_DELIMITER);
    }

    private static String getValueFromParams(Map<String, Object> parameters, String key, String errorMessage) {
        String value = (String)parameters.get(key);
        if (value == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }
}

