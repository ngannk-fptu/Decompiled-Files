/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserImpersonator
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.notification;

import com.atlassian.confluence.plugins.tasklist.notification.RecommendedEmailTaskRenderer;
import com.atlassian.confluence.user.AuthenticatedUserImpersonator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecommendedEmailTasksWebPanel
implements WebPanel {
    private final RecommendedEmailTaskRenderer recommendedEmailTaskRenderer;

    @Autowired
    public RecommendedEmailTasksWebPanel(RecommendedEmailTaskRenderer recommendedEmailTaskRenderer) {
        this.recommendedEmailTaskRenderer = recommendedEmailTaskRenderer;
    }

    public void writeHtml(Writer writer, Map<String, Object> context) throws IOException {
        writer.append(this.getHtml(context));
    }

    public String getHtml(final Map<String, Object> context) {
        final ConfluenceUser user = (ConfluenceUser)context.get("summary-recipient");
        return (String)AuthenticatedUserImpersonator.REQUEST_AGNOSTIC.asUser((Callable)new Callable<String>(){

            @Override
            public String call() throws Exception {
                return RecommendedEmailTasksWebPanel.this.recommendedEmailTaskRenderer.renderUpcomingTasksForMail(user, context);
            }
        }, (User)user);
    }
}

