/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.model.NotificationFilter
 *  com.google.common.base.Joiner
 *  net.java.ao.Query
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.mywork.host.dao.ao;

import com.atlassian.mywork.model.NotificationFilter;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.java.ao.Query;
import org.apache.commons.lang3.StringUtils;

public class ConditionUtil {
    public static final String QUESTION_MARK_CHARACTER = "?";
    public static final String SEPARATED_PARAM_CHARACTER = ", ";

    public static Query buildQuery(NotificationFilter condition) {
        List notificationIds;
        List pageIds;
        List actions;
        ArrayList<String> conditions = new ArrayList<String>();
        ArrayList<Object> params = new ArrayList<Object>();
        if (StringUtils.isNotBlank((CharSequence)condition.getUserKey())) {
            conditions.add("USER = ?");
            params.add(condition.getUserKey());
        }
        if (condition.getFromCreatedDate() != null) {
            conditions.add("CREATED >= ?");
            params.add(condition.getFromCreatedDate());
        }
        if (condition.getToCreatedDate() != null) {
            conditions.add("CREATED <= ?");
            params.add(condition.getToCreatedDate());
        }
        if (StringUtils.isNotBlank((CharSequence)condition.getAppId())) {
            conditions.add("GLOBAL_ID LIKE ?");
            params.add(condition.getAppId() + "%");
        }
        if ((actions = condition.getActions()) != null && !actions.isEmpty()) {
            conditions.add(ConditionUtil.buildInConditionQuery("ACTION", actions));
            params.addAll(actions);
        }
        if ((pageIds = condition.getPageIds()) != null && !pageIds.isEmpty()) {
            conditions.add(ConditionUtil.buildLikeConditionQuery("METADATA", pageIds));
            pageIds.forEach(pageId -> params.add("%\"pageId\":" + pageId + "%"));
        }
        if ((notificationIds = condition.getNotificationIds()) != null && !notificationIds.isEmpty()) {
            conditions.add(ConditionUtil.buildInConditionQuery("ID", notificationIds));
            params.addAll(notificationIds);
        }
        return Query.select().where(Joiner.on((String)" AND ").join(conditions), params.toArray());
    }

    private static String buildInConditionQuery(String fieldName, List<? extends Object> values) {
        String inCondition = Joiner.on((String)SEPARATED_PARAM_CHARACTER).join((Iterable)values.stream().map(value -> QUESTION_MARK_CHARACTER).collect(Collectors.toList()));
        return fieldName + " IN (" + inCondition + ")";
    }

    private static String buildLikeConditionQuery(String fieldName, List<? extends Object> values) {
        String likeCondition = Joiner.on((String)" OR ").join((Iterable)values.stream().map(value -> "(" + fieldName + " LIKE ?)").collect(Collectors.toList()));
        return "(" + likeCondition + ")";
    }
}

