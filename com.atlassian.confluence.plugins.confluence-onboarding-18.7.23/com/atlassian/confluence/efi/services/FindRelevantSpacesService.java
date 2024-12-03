/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.efi.services;

import com.atlassian.confluence.efi.rest.beans.RelevantSpaceBean;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface FindRelevantSpacesService {
    public List<RelevantSpaceBean> getRelevantSpaces(HttpServletRequest var1);

    public List<RelevantSpaceBean> getRelevantSpaces(String var1, HttpServletRequest var2);
}

