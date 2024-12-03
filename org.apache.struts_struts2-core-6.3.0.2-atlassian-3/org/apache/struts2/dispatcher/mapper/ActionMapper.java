/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public interface ActionMapper {
    public ActionMapping getMapping(HttpServletRequest var1, ConfigurationManager var2);

    public ActionMapping getMappingFromActionName(String var1);

    public String getUriFromActionMapping(ActionMapping var1);
}

