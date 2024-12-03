/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.template.TemplateUpdateEvent
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.jira.event;

import com.atlassian.confluence.event.events.template.TemplateUpdateEvent;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.jira.event.InstructionalJiraAddedToTemplateEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;

public class JiraMacroPageTemplateEventListener {
    private static final String JIRA_ISSUE_MACRO_TYPE_REG = "<ac:placeholder ac:type=\"jira\">";
    private static final Pattern JIRA_ISSUE_MACRO_PATTERN = Pattern.compile("<ac:placeholder ac:type=\"jira\">");
    private EventPublisher eventPublisher;

    public JiraMacroPageTemplateEventListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void publishAnalyticTemplateEvent(TemplateUpdateEvent pageUpdateEvent) {
        int instances;
        if (pageUpdateEvent.getOldTemplate() == null) {
            instances = this.getNumJiraMacroInTemplate(pageUpdateEvent.getNewTemplate());
        } else {
            int numberNewInstances = this.getNumJiraMacroInTemplate(pageUpdateEvent.getNewTemplate());
            int numberOldInstances = this.getNumJiraMacroInTemplate(pageUpdateEvent.getOldTemplate());
            instances = numberNewInstances - numberOldInstances;
        }
        if (instances > 0) {
            this.eventPublisher.publish((Object)new InstructionalJiraAddedToTemplateEvent(String.valueOf(instances)));
        }
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    private int getNumJiraMacroInTemplate(PageTemplate template) {
        int numMacro = 0;
        String content = template.getContent();
        if (StringUtils.isNotBlank((CharSequence)content)) {
            Matcher matcher = JIRA_ISSUE_MACRO_PATTERN.matcher(content);
            while (matcher.find()) {
                ++numMacro;
            }
        }
        return numMacro;
    }
}

