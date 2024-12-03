/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.content.ContentTypeManager
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.content.apisupport.ContentTypeApiSupport
 *  com.atlassian.confluence.content.custom.CustomContentType
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.event.events.plugin.PluginEvent
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.content.apisupport.ContentTypeApiSupport;
import com.atlassian.confluence.content.custom.CustomContentType;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.event.events.plugin.PluginEvent;
import com.atlassian.confluence.plugins.cql.rest.DisplayableType;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchTypeManager
implements InitializingBean,
DisposableBean {
    public static final String SPACE_TYPE = "space";
    public static final String USER_TYPE = "user";
    private static final String SPACE_I18N_KEY = "space.name";
    private static final String USER_I18N_KEY = "user.name";
    private static final String TEAM_CALENDAR_MODULE_KEY = "com.atlassian.confluence.extra.team-calendars:calendar-content-type";
    private static final String QUESTIONS_MODULE_KEY = "com.atlassian.confluence.plugins.confluence-questions:question";
    private static final String ANSWERS_MODULE_KEY = "com.atlassian.confluence.plugins.confluence-questions:answer";
    private static final Logger log = LoggerFactory.getLogger(SearchTypeManager.class);
    private static final Function<DisplayableType, String> TYPE_KEY_FUNC = v -> v.getType().toLowerCase();
    private final ContentTypeManager contentTypeManager;
    private final CustomContentManager customContentManager;
    private final I18nResolver i18nResolver;
    private final ContentUiSupport contentUiSupport;
    private final PluginEventManager pluginEventManager;
    private Set<ContentType> malfunctioningTypes = Sets.newHashSet();
    private static final ImmutableList<ContentType> SUPPORTED_BUILT_IN_CONTENT_TYPES = ImmutableList.of((Object)ContentType.PAGE, (Object)ContentType.BLOG_POST, (Object)ContentType.COMMENT, (Object)ContentType.ATTACHMENT);

    @Autowired
    public SearchTypeManager(@ComponentImport ContentTypeManager contentTypeManager, @ComponentImport CustomContentManager customContentManager, @ComponentImport I18nResolver i18nResolver, @ComponentImport ContentUiSupport contentUiSupport, @ComponentImport PluginEventManager pluginEventManager) {
        this.contentTypeManager = contentTypeManager;
        this.customContentManager = customContentManager;
        this.i18nResolver = i18nResolver;
        this.contentUiSupport = contentUiSupport;
        this.pluginEventManager = pluginEventManager;
    }

    public void destroy() {
        this.pluginEventManager.unregister((Object)this);
    }

    public void afterPropertiesSet() {
        this.pluginEventManager.register((Object)this);
    }

    public Map<String, DisplayableType> getContentTypes() {
        return Collections.unmodifiableMap(Stream.concat(SUPPORTED_BUILT_IN_CONTENT_TYPES.stream().map(this::transformBuiltIn), this.getCustomContentTypes().stream()).collect(Collectors.toMap(TYPE_KEY_FUNC, Function.identity())));
    }

    private Map<String, DisplayableType> getBuiltinContentTypesByLabel() {
        return Collections.unmodifiableMap(SUPPORTED_BUILT_IN_CONTENT_TYPES.stream().map(this::transformBuiltIn).collect(Collectors.toMap(v -> v.getLabel().toLowerCase(), Function.identity())));
    }

    private List<DisplayableType> getCustomContentTypes() {
        Map<String, DisplayableType> builtInLabels = this.getBuiltinContentTypesByLabel();
        ArrayList<DisplayableType> types = new ArrayList<DisplayableType>();
        for (CustomContentType type : this.contentTypeManager.getEnabledCustomContentTypes()) {
            try {
                if (this.malfunctioningTypes.contains(type.getApiSupport().getHandledType()) || !type.getPermissionDelegate().canView((User)AuthenticatedUserThreadLocal.get())) continue;
                ContentTypeApiSupport support = type.getApiSupport();
                CustomContentEntityObject ceo = this.customContentManager.newPluginContentEntityObject(support.getHandledType().serialise());
                if (!type.getPermissionDelegate().canView((User)AuthenticatedUserThreadLocal.get())) continue;
                String i18nKey = type.getContentUiSupport().getContentTypeI18NKey((ConfluenceEntityObject)ceo);
                String label = this.i18nResolver.getText(i18nKey);
                if (StringUtils.isBlank((CharSequence)label) && TEAM_CALENDAR_MODULE_KEY.equals(ceo.getPluginModuleKey())) {
                    label = "Calendars";
                } else if (i18nKey.equals("question") && QUESTIONS_MODULE_KEY.equals(ceo.getPluginModuleKey())) {
                    i18nKey = "question.searchtype.name";
                    label = this.i18nResolver.getText(i18nKey);
                } else if (i18nKey.equals("answer") && ANSWERS_MODULE_KEY.equals(ceo.getPluginModuleKey())) {
                    i18nKey = "answer.searchtype.name";
                    label = this.i18nResolver.getText(i18nKey);
                }
                if (label == null) continue;
                if (!builtInLabels.containsKey(label.toLowerCase())) {
                    types.add(DisplayableType.builder().type(support.getHandledType()).i18nKey(i18nKey).label(label).build());
                    continue;
                }
                ContentType contentType = type.getApiSupport().getHandledType();
                log.error("A plugin installed content type has duplicated a built-in content type label ({}), removing custom content type from CQL handled types : {}", (Object)label, (Object)contentType);
                this.malfunctioningTypes.add(contentType);
            }
            catch (Exception ex) {
                ContentType contentType = type.getApiSupport().getHandledType();
                log.error("A plugin installed content type caused an error for the CQL plugin, removing custom content type from CQL handled types : " + contentType, (Throwable)ex);
                this.malfunctioningTypes.add(contentType);
            }
        }
        return types;
    }

    public Map<String, DisplayableType> getTypes() {
        DisplayableType spaceType = DisplayableType.builder().type(SPACE_TYPE).label(this.i18nResolver.getText(SPACE_I18N_KEY)).i18nKey(SPACE_I18N_KEY).build();
        DisplayableType userType = DisplayableType.builder().type(USER_TYPE).label(this.i18nResolver.getText(USER_I18N_KEY)).i18nKey(USER_I18N_KEY).build();
        return ImmutableMap.builder().put((Object)TYPE_KEY_FUNC.apply(spaceType), (Object)spaceType).put((Object)TYPE_KEY_FUNC.apply(userType), (Object)userType).putAll(this.getContentTypes()).build();
    }

    public boolean hasType(String type) {
        return this.getTypes().containsKey(type.toLowerCase());
    }

    @PluginEventListener
    public void clearMalfunctioningTypesDueToChangeInPluginState(PluginEvent e) {
        this.malfunctioningTypes.clear();
    }

    public String getIconCssClass(SearchResult result) {
        Object iconCssClass = this.contentUiSupport.getIconCssClass(result);
        boolean noIconClass = Strings.isNullOrEmpty((String)iconCssClass);
        if (noIconClass || ((String)iconCssClass).contains("icon-question")) {
            String contentPluginKey = result.getField("contentPluginKey");
            String pluginKeyIcon = "icon-" + contentPluginKey.replaceAll("[\\.:]", "-");
            iconCssClass = noIconClass ? pluginKeyIcon : (String)iconCssClass + " " + pluginKeyIcon;
        } else {
            iconCssClass = ((String)iconCssClass).replace("aui-icon aui-icon-small ", "");
        }
        return iconCssClass;
    }

    private DisplayableType transformBuiltIn(ContentType input) {
        String i18nKey = "content-type.label." + input.serialise();
        return DisplayableType.builder().type(input).i18nKey(i18nKey).label(this.i18nResolver.getText(i18nKey)).build();
    }
}

