/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.plugins.conversion.convert.ConversionException
 *  com.atlassian.plugins.conversion.convert.html.HtmlConversionResult
 *  com.atlassian.plugins.conversion.convert.html.word.WordConverter
 *  com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionRequest
 *  com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionResponse
 *  com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionTask
 *  com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionType
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.ClassUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.benryan.conversion;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.html.HtmlConversionResult;
import com.atlassian.plugins.conversion.convert.html.word.WordConverter;
import com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionRequest;
import com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionResponse;
import com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionTask;
import com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionType;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import com.benryan.components.AttachmentCacheKey;
import com.benryan.components.HtmlCacheManager;
import com.benryan.conversion.Converter;
import com.benryan.conversion.SandboxConversionFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DocConverter
implements Converter {
    private final HtmlCacheManager htmlCacheManager;
    private final VelocityHelperService velocityHelperService;
    private final SandboxConversionFeature sandboxConversionFeature;
    private final Sandbox sandbox;

    public DocConverter(HtmlCacheManager htmlCacheManager, VelocityHelperService velocityHelperService, SandboxConversionFeature sandboxConversionFeature, Sandbox sandbox) {
        this.htmlCacheManager = htmlCacheManager;
        this.velocityHelperService = velocityHelperService;
        this.sandboxConversionFeature = sandboxConversionFeature;
        this.sandbox = sandbox;
    }

    @Override
    public String execute(Map<String, Object> args) throws Exception {
        AttachmentCacheKey dataKey;
        HtmlConversionResult data;
        Attachment attachmentObj = (Attachment)args.get("attachmentObj");
        if (null == attachmentObj) {
            return null;
        }
        this.validate(attachmentObj, args);
        String attachmentId = String.valueOf(attachmentObj.getId());
        String contextKey = (String)args.get("context");
        String contextPath = contextKey != null ? contextKey : "";
        String pageId = (String)args.get("pageID");
        String attachment = (String)args.get("attachment");
        String path = contextPath + "/plugins/servlet/benryanconversion?pageId=" + pageId + "&attachment=" + HtmlUtil.urlEncode((String)attachment) + "&name=" + attachmentId;
        String sheetName = (String)args.get("sheet");
        if (sheetName != null) {
            path = path + "&sheetName=" + HtmlUtil.urlEncode((String)sheetName);
        }
        if ((data = this.htmlCacheManager.getHtmlConversionData(dataKey = new AttachmentCacheKey(attachmentObj, sheetName))) == null) {
            data = this.doConversion(path, args, attachmentObj.getContentsAsStream(), path + "&val=");
            this.htmlCacheManager.addHtmlConversionData(dataKey, data);
        }
        Map context = this.velocityHelperService.createDefaultVelocityContext();
        Boolean allowEdit = (Boolean)args.get("isNews");
        context.put("allowEdit", allowEdit);
        context.put("contentHtml", new HtmlFragment((Object)data.getHtml()));
        context.put("resourcePath", args.get("resourcePath"));
        context.put("title", attachment);
        context.put("attachmentId", attachmentObj.getId());
        context.put("pageId", pageId);
        String editUrl = (String)args.get("editUrl");
        context.put("editHrefHtml", HtmlUtil.htmlEncode((String)(contextPath + GeneralUtil.escapeForJavascript((String)editUrl))));
        Boolean usePathAuth = (Boolean)args.get("usePathAuth");
        context.put("usePathAuth", usePathAuth);
        return VelocityUtils.getRenderedTemplate((String)"templates/extra/conversion/conversion.vm", (Map)context);
    }

    protected void validate(Attachment attachment, Map<String, Object> args) throws ConversionException {
    }

    protected HtmlConversionResult doConversion(String imgPath, Map<String, Object> args, InputStream inputStream, String imagePath) throws IOException, ConversionException {
        if (this.isSandboxConversionEnabled()) {
            return this.performConversionInSandbox(args, inputStream, imagePath, SandboxHtmlConversionType.WORD);
        }
        return WordConverter.convertToHtml((InputStream)inputStream, (String)imagePath);
    }

    protected HtmlConversionResult performConversionInSandbox(Map<String, Object> args, InputStream inputStream, final String imagePath, SandboxHtmlConversionType sandboxHtmlConversionType) throws IOException {
        HashMap<String, Object> sandboxConversionsRequestArgs = new HashMap<String, Object>(args);
        sandboxConversionsRequestArgs.remove("attachmentObj");
        SandboxHtmlConversionRequest conversionRequest = new SandboxHtmlConversionRequest(this.extractArguments(sandboxConversionsRequestArgs), imagePath, IOUtils.toByteArray((InputStream)inputStream), sandboxHtmlConversionType){

            public String toString() {
                return new ToStringBuilder((Object)this).append("imagePath", (Object)imagePath).toString();
            }
        };
        return ((SandboxHtmlConversionResponse)this.sandbox.execute((SandboxTask)new SandboxHtmlConversionTask(), (Object)conversionRequest, Duration.ofMinutes(10L))).getHtmlConversionResult();
    }

    protected Map<String, Object> extractArguments(Map<String, Object> args) {
        return args.entrySet().stream().filter(entry -> {
            Class<?> valueClass = entry.getValue().getClass();
            return Serializable.class.isAssignableFrom(valueClass) || valueClass.isPrimitive() || ClassUtils.primitiveToWrapper(valueClass) != null && ClassUtils.primitiveToWrapper(valueClass).isPrimitive();
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    protected boolean isSandboxConversionEnabled() {
        return this.sandboxConversionFeature.isEnable();
    }
}

