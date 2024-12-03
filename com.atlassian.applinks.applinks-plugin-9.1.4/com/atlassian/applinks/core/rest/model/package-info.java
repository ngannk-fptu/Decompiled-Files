/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters
 *  org.osgi.framework.Version
 */
@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlJavaTypeAdapters(value={@XmlJavaTypeAdapter(value=ApplicationIdAdapter.class, type=ApplicationId.class), @XmlJavaTypeAdapter(value=VersionAdapter.class, type=Version.class)})
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.core.rest.model.adapter.ApplicationIdAdapter;
import com.atlassian.applinks.core.rest.model.adapter.VersionAdapter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.osgi.framework.Version;


