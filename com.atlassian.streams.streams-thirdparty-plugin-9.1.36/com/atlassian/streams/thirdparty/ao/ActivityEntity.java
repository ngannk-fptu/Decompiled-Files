/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 */
package com.atlassian.streams.thirdparty.ao;

import com.atlassian.streams.thirdparty.ao.ActorEntity;
import com.atlassian.streams.thirdparty.ao.MediaLinkEntity;
import com.atlassian.streams.thirdparty.ao.ObjectEntity;
import com.atlassian.streams.thirdparty.ao.TargetEntity;
import java.net.URI;
import java.util.Date;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;

@Preload
public interface ActivityEntity
extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey(value="activity_id")
    public long getActivityId();

    public URI getId();

    public void setId(URI var1);

    public ActorEntity getActor();

    public void setActor(ActorEntity var1);

    public String getUsername();

    public void setUsername(String var1);

    public String getPoster();

    public void setPoster(String var1);

    @StringLength(value=-1)
    public String getContent();

    public void setContent(String var1);

    public URI getGeneratorId();

    public void setGeneratorId(URI var1);

    public String getGeneratorDisplayName();

    public void setGeneratorDisplayName(String var1);

    public MediaLinkEntity getIcon();

    public void setIcon(MediaLinkEntity var1);

    public ObjectEntity getObject();

    public void setObject(ObjectEntity var1);

    public Date getPublished();

    public void setPublished(Date var1);

    public TargetEntity getTarget();

    public void setTarget(TargetEntity var1);

    public String getTitle();

    public void setTitle(String var1);

    public URI getUrl();

    public void setUrl(URI var1);

    public URI getVerb();

    public void setVerb(URI var1);

    public String getProjectKey();

    public void setProjectKey(String var1);

    public String getIssueKey();

    public void setIssueKey(String var1);
}

