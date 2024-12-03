/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.benryan.conversion;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.v2.macro.MacroException;
import com.benryan.conversion.Converter;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PptConverter
implements Converter {
    private static final Logger log = LoggerFactory.getLogger(PptConverter.class);
    public static final String WIDTH_KEY = "width";
    public static final String HEIGHT_KEY = "height";
    public static final String SLIDE_KEY = "slide";
    public static final String DEFAULT_HEIGHT = "507";
    public static final String DEFAULT_WIDTH = "632";
    private final VelocityHelperService velocityHelperService;

    public PptConverter(@ComponentImport VelocityHelperService velocityHelperService) {
        this.velocityHelperService = velocityHelperService;
    }

    private String normalize(String attr) {
        if (!((String)attr).endsWith("px") && !((String)attr).endsWith("%")) {
            attr = (String)attr + "px";
        }
        return HtmlUtil.htmlEncode((String)attr);
    }

    public String execute(Map args) throws MacroException {
        String pageId = (String)args.get("pageID");
        String attachment = (String)args.get("attachment");
        String editUrl = (String)args.get("editUrl");
        String contextPath = (String)args.get("context");
        String baseUrl = (String)args.get("baseUrl");
        String servletBaseUrl = (String)args.get("servletBaseUrl");
        Boolean allowEdit = (Boolean)args.get("isNews");
        Boolean useJavascript = (Boolean)args.get("useJavascript");
        Boolean usePathAuth = (Boolean)args.get("usePathAuth");
        String width = (String)args.get(WIDTH_KEY);
        String height = (String)args.get(HEIGHT_KEY);
        String slideNum = (String)args.get(SLIDE_KEY);
        Attachment obj = (Attachment)args.get("attachmentObj");
        if (slideNum == null) {
            Map context = this.velocityHelperService.createDefaultVelocityContext();
            context.put("allowEdit", allowEdit);
            context.put("resourcePath", args.get("resourcePath"));
            context.put("pageId", HtmlUtil.urlEncode((String)pageId));
            context.put("attachment", HtmlUtil.urlEncode((String)attachment));
            context.put("title", HtmlUtil.urlEncode((String)attachment));
            context.put("attachmentId", obj.getId());
            context.put("attachmentVer", obj.getVersion());
            context.put("downloadPath", obj.getDownloadPathWithoutVersion());
            context.put(WIDTH_KEY, this.normalize(width == null ? DEFAULT_WIDTH : width));
            context.put(HEIGHT_KEY, this.normalize(height == null ? DEFAULT_HEIGHT : height));
            context.put("editUrl", HtmlUtil.urlEncode((String)editUrl));
            context.put("contextPath", HtmlUtil.urlEncode((String)contextPath));
            context.put("baseUrl", HtmlUtil.urlEncode((String)baseUrl));
            context.put("useJavascript", useJavascript);
            context.put("usePathAuth", usePathAuth);
            String retVal = "";
            try {
                retVal = this.velocityHelperService.getRenderedTemplate("templates/extra/slideviewer/slideviewer.vm", context);
            }
            catch (Exception e) {
                log.error("Problem processing template for Flash Slide viewer", (Throwable)e);
                throw new MacroException((Throwable)e);
            }
            return retVal;
        }
        width = width == null ? DEFAULT_WIDTH : HtmlUtil.urlEncode((String)width);
        height = height == null ? DEFAULT_HEIGHT : HtmlUtil.urlEncode((String)height);
        return "<img width=\"" + width + "\" height=\"" + height + "\" src=\"" + servletBaseUrl + "/plugins/servlet/pptslideservlet?slide=" + HtmlUtil.htmlEncode((String)slideNum) + "&pageId=" + HtmlUtil.urlEncode((String)pageId) + "&attachment=" + HtmlUtil.urlEncode((String)attachment) + "&attachmentId=" + obj.getId() + "\" />";
    }
}

