/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.links.persistence;

import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.links.persistence.dao.LinkDao;

public interface LinkDaoInternal
extends LinkDao,
ObjectDaoInternal<OutgoingLink> {
}

