/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.node.ArrayNode
 *  org.codehaus.jackson.node.JsonNodeFactory
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.atlassian.mywork.rest;

import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.NotificationBuilder;
import com.atlassian.mywork.model.Registration;
import com.atlassian.mywork.model.RegistrationBuilder;
import com.atlassian.mywork.model.Task;
import com.atlassian.mywork.model.TaskBuilder;
import com.atlassian.mywork.model.UpdateMetadata;
import com.atlassian.mywork.rest.JsonConfig;
import com.atlassian.mywork.rest.JsonCount;
import com.atlassian.mywork.rest.JsonGroupNotification;
import com.atlassian.mywork.rest.JsonNotificationGroup;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public final class Samples {
    public static final String APP_LINK_ID = "bf13be6c-926b-318e-95cc-99dc04f8597e";
    public static final String GLOBAL_ID = "appId=bf13be6c-926b-318e-95cc-99dc04f8597e&entity=issue&id=1234";
    private static final Registration REGISTRATION = new RegistrationBuilder(new Registration.RegistrationId("bf13be6c-926b-318e-95cc-99dc04f8597e", "com.atlassian.confluence")).addI18n(Locale.ENGLISH, null).actions((JsonNode)Samples.createAction("openLink", "Open")).properties(Samples.asMap("com.atlassian.confluence.blog.task.remove", "/images/icons/inline-tasks/inline-task.png", "com.atlassian.confluence.user_url", "/users/viewuserprofile.action?username={username}")).displayUrl("https://jira.atlassian.com").build();
    public static final String DESCRIPTION = "Apples &amp; <strong>oranges</strong>";
    public static final String NOTES = "Apples & oranges";
    public static final Notification NOTIFICATION = new NotificationBuilder().title("John commented on an issue").description("Apples &amp; <strong>oranges</strong>").application("com.atlassian.jira").entity("issue").action("comment").user("john.smith").globalId("appId=bf13be6c-926b-318e-95cc-99dc04f8597e&entity=issue&id=1234").pinned(false).applicationLinkId("bf13be6c-926b-318e-95cc-99dc04f8597e").metadata(Samples.createMetadata("user", "John Smith")).itemIconUrl("https://jira.atlassian.com/images/icons/bug.gif").itemTitle("JRA-1: Email addresses are case sensitive").itemUrl("https://jira.atlassian.com/browse/JRA-1").groupingId("appId=bf13be6c-926b-318e-95cc-99dc04f8597e&entity=issue&id=1234").createNotification();
    public static final Task TASK = new TaskBuilder().title("John commented on an issue").notes("Apples & oranges").application("com.atlassian.jira").entity("issue").user("john.smith").globalId("appId=bf13be6c-926b-318e-95cc-99dc04f8597e&entity=issue&id=1234").applicationLinkId("bf13be6c-926b-318e-95cc-99dc04f8597e").metadata(Samples.createMetadata("user", "John Smith")).iconUrl("https://jira.atlassian.com/images/icons/bug.gif").itemTitle("JRA-1: Email addresses are case sensitive").url("https://jira.atlassian.com/browse/JRA-1").createTask();
    public static final JsonConfig CONFIG = new JsonConfig(REGISTRATION, Locale.ENGLISH);
    public static final JsonCount COUNT = new JsonCount(7, 60, 300);
    public static final JsonGroupNotification GROUP_NOTIFICATION = new JsonGroupNotification("key", NOTIFICATION);
    public static final JsonNotificationGroup GROUP = new JsonNotificationGroup(NOTIFICATION, Arrays.asList(GROUP_NOTIFICATION), "key");
    public static final UpdateMetadata UPDATE_METADATA = new UpdateMetadata("test", Samples.createMetadata("watching", "true"), Samples.createMetadata("user", "John Smith", "watching", "false"));
    public static final List<Notification> NOTIFICATIONS = Arrays.asList(NOTIFICATION);
    public static final List<JsonNotificationGroup> GROUPS = Arrays.asList(GROUP);
    public static final List<Task> TASKS = Arrays.asList(TASK);
    public static final List<JsonConfig> CONFIGS = Arrays.asList(CONFIG);

    private static ObjectNode createAction(String id, String name) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        ObjectNode openLink = JsonNodeFactory.instance.objectNode();
        node.put(id, (JsonNode)openLink);
        openLink.put("name", name);
        openLink.put("type", "link");
        openLink.put("objectActions", (JsonNode)Samples.createArrayNode("page", "blog", "comment"));
        openLink.put("actions", (JsonNode)Samples.createArrayNode("page.comment", "blog.comment"));
        return node;
    }

    private static ArrayNode createArrayNode(String ... values) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }

    private static ObjectNode createMetadata(String ... strings) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        for (int i = 0; i < strings.length; i += 2) {
            node.put(strings[i], strings[i + 1]);
        }
        return node;
    }

    private static Map<String, String> asMap(String ... values) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }
        return map;
    }
}

