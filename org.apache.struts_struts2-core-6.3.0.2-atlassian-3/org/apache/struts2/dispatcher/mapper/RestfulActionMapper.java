/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Inject;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.url.UrlDecoder;

public class RestfulActionMapper
implements ActionMapper {
    protected static final Logger LOG = LogManager.getLogger(RestfulActionMapper.class);
    private UrlDecoder decoder;

    @Inject
    public void setDecoder(UrlDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        String uri = RequestUtils.getServletPath(request);
        int nextSlash = uri.indexOf(47, 1);
        if (nextSlash == -1) {
            return null;
        }
        String actionName = uri.substring(1, nextSlash);
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        try {
            StringTokenizer st = new StringTokenizer(uri.substring(nextSlash), "/");
            boolean isNameTok = true;
            String paramName = null;
            if (st.countTokens() % 2 != 0) {
                isNameTok = false;
                paramName = actionName + "Id";
            }
            while (st.hasMoreTokens()) {
                if (isNameTok) {
                    paramName = this.decoder.decode(st.nextToken(), "UTF-8", false);
                    isNameTok = false;
                    continue;
                }
                String paramValue = this.decoder.decode(st.nextToken(), "UTF-8", false);
                if (paramName != null && paramName.length() > 0) {
                    parameters.put(paramName, paramValue);
                }
                isNameTok = true;
            }
        }
        catch (Exception e) {
            LOG.warn("Cannot determine url parameters", (Throwable)e);
        }
        return new ActionMapping(actionName, "", "", parameters);
    }

    @Override
    public ActionMapping getMappingFromActionName(String actionName) {
        return new ActionMapping(actionName, null, null, null);
    }

    @Override
    public String getUriFromActionMapping(ActionMapping mapping) {
        StringBuilder retVal = new StringBuilder();
        retVal.append(mapping.getNamespace());
        retVal.append(mapping.getName());
        Object value = mapping.getParams().get(mapping.getName() + "Id");
        if (value != null) {
            retVal.append("/");
            retVal.append(value);
        }
        return retVal.toString();
    }
}

