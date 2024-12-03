/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  net.java.ao.DBParam
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.db;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.AbstractHistoryDao;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.Utils;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.ao.SynchronyRequests;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Date;
import java.util.Map;
import net.java.ao.DBParam;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynchronyRequestsHistoryDao
extends AbstractHistoryDao<SynchronyRequests> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public SynchronyRequestsHistoryDao(@ComponentImport ActiveObjects ao, Utils dbUtils) {
        super(ao, dbUtils);
    }

    public void add(long contentId, String type, String url, Map<String, Object> payload, boolean isSuccessful) {
        this.dbUtils.executeInTransaction(false, contentId, () -> {
            SynchronyRequests entity = (SynchronyRequests)this.ao.create(SynchronyRequests.class, new DBParam[]{new DBParam("CONTENT_ID", (Object)contentId), new DBParam("TYPE", (Object)type), new DBParam("URL", (Object)url), new DBParam("PAYLOAD", (Object)objectMapper.writeValueAsString((Object)payload)), new DBParam("SUCCESSFUL", (Object)isSuccessful), new DBParam("INSERTED", (Object)new Date())});
            entity.save();
            return null;
        });
    }
}

