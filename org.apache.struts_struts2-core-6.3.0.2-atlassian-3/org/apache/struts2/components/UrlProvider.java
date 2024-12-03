/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.ExtraParameterProvider;
import org.apache.struts2.components.UrlRenderer;

public interface UrlProvider {
    public static final String NONE = "none";
    public static final String GET = "get";
    public static final String ALL = "all";

    public boolean isPutInContext();

    public String getVar();

    public String getValue();

    public String findString(String var1);

    public void setValue(String var1);

    public String getUrlIncludeParams();

    public String getIncludeParams();

    public Map<String, Object> getParameters();

    public HttpServletRequest getHttpServletRequest();

    public String getAction();

    public ExtraParameterProvider getExtraParameterProvider();

    public String getScheme();

    public String getNamespace();

    public String getMethod();

    public HttpServletResponse getHttpServletResponse();

    public boolean isIncludeContext();

    public boolean isEncode();

    public boolean isForceAddSchemeHostAndPort();

    public boolean isEscapeAmp();

    public String getPortletMode();

    public String getWindowState();

    public String determineActionURL(String var1, String var2, String var3, HttpServletRequest var4, HttpServletResponse var5, Map<String, ?> var6, String var7, boolean var8, boolean var9, boolean var10, boolean var11);

    public String determineNamespace(String var1, ValueStack var2, HttpServletRequest var3);

    public String getAnchor();

    public String getPortletUrlType();

    public ValueStack getStack();

    public void setUrlIncludeParams(String var1);

    public void setHttpServletRequest(HttpServletRequest var1);

    public void setHttpServletResponse(HttpServletResponse var1);

    public void setUrlRenderer(UrlRenderer var1);

    public void setExtraParameterProvider(ExtraParameterProvider var1);

    public void setIncludeParams(String var1);

    public void setScheme(String var1);

    public void setAction(String var1);

    public void setPortletMode(String var1);

    public void setNamespace(String var1);

    public void setMethod(String var1);

    public void setEncode(boolean var1);

    public void setIncludeContext(boolean var1);

    public void setWindowState(String var1);

    public void setPortletUrlType(String var1);

    public void setAnchor(String var1);

    public void setEscapeAmp(boolean var1);

    public void setForceAddSchemeHostAndPort(boolean var1);

    public void putInContext(String var1);
}

