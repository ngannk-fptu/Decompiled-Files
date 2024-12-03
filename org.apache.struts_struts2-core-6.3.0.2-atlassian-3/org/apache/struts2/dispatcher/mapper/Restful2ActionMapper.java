/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Inject;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.apache.struts2.url.UrlDecoder;

public class Restful2ActionMapper
extends DefaultActionMapper {
    private static final Logger LOG = LogManager.getLogger(Restful2ActionMapper.class);
    public static final String HTTP_METHOD_PARAM = "__http_method";
    private String idParameterName = null;
    private UrlDecoder decoder;

    public Restful2ActionMapper() {
        this.setSlashesInActionNames("true");
    }

    @Inject
    public void setDecoder(UrlDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        if (!this.isSlashesInActionNames()) {
            throw new IllegalStateException("This action mapper requires the setting 'slashesInActionNames' to be set to 'true'");
        }
        ActionMapping mapping = super.getMapping(request, configManager);
        if (mapping == null) {
            return null;
        }
        String actionName = mapping.getName();
        String id = null;
        if (StringUtils.isNotBlank((CharSequence)actionName)) {
            int actionSlashPos;
            int lastSlashPos = actionName.lastIndexOf(47);
            if (lastSlashPos > -1) {
                id = actionName.substring(lastSlashPos + 1);
            }
            if (mapping.getMethod() == null) {
                if (lastSlashPos == actionName.length() - 1) {
                    if (this.isGet(request)) {
                        mapping.setMethod("index");
                    } else if (this.isPost(request)) {
                        mapping.setMethod("create");
                    }
                } else if (lastSlashPos > -1) {
                    if (this.isGet(request) && "new".equals(id)) {
                        mapping.setMethod("editNew");
                    } else if (this.isGet(request)) {
                        mapping.setMethod("view");
                    } else if (this.isDelete(request)) {
                        mapping.setMethod("remove");
                    } else if (this.isPut(request)) {
                        mapping.setMethod("update");
                    }
                }
                if (this.idParameterName != null && lastSlashPos > -1) {
                    actionName = actionName.substring(0, lastSlashPos);
                }
            }
            if (this.idParameterName != null && id != null) {
                if (mapping.getParams() == null) {
                    mapping.setParams(new HashMap<String, Object>());
                }
                mapping.getParams().put(this.idParameterName, id);
            }
            if ((actionSlashPos = actionName.lastIndexOf(47, lastSlashPos - 1)) > 0 && actionSlashPos < lastSlashPos) {
                String params = actionName.substring(0, actionSlashPos);
                HashMap<String, String> parameters = new HashMap<String, String>();
                try {
                    StringTokenizer st = new StringTokenizer(params, "/");
                    boolean isNameTok = true;
                    String paramName = null;
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
                    if (parameters.size() > 0) {
                        if (mapping.getParams() == null) {
                            mapping.setParams(new HashMap<String, Object>());
                        }
                        mapping.getParams().putAll(parameters);
                    }
                }
                catch (Exception e) {
                    LOG.warn("Unable to determine parameters from the url", (Throwable)e);
                }
                mapping.setName(actionName.substring(actionSlashPos + 1));
            }
        }
        return mapping;
    }

    protected boolean isGet(HttpServletRequest request) {
        return "get".equalsIgnoreCase(request.getMethod());
    }

    protected boolean isPost(HttpServletRequest request) {
        return "post".equalsIgnoreCase(request.getMethod());
    }

    protected boolean isPut(HttpServletRequest request) {
        if ("put".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return this.isPost(request) && "put".equalsIgnoreCase(request.getParameter(HTTP_METHOD_PARAM));
    }

    protected boolean isDelete(HttpServletRequest request) {
        if ("delete".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return this.isPost(request) && "delete".equalsIgnoreCase(request.getParameter(HTTP_METHOD_PARAM));
    }

    public String getIdParameterName() {
        return this.idParameterName;
    }

    @Inject(required=false, value="struts.mapper.idParameterName")
    public void setIdParameterName(String idParameterName) {
        this.idParameterName = idParameterName;
    }
}

