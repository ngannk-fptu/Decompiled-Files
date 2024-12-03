/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.image.effects.ImageFilterUtils;
import com.atlassian.plugins.conversion.convert.FileFormat;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ImageFilterFilter
implements Filter {
    private static Pattern downloadPattern = Pattern.compile("/download/(?<type>attachments|thumbnails)/(?<id>\\d+)/(?<filename>[^\\?\\n\\r]+)(\\?version\\=(?<version>\\d+))?");

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Object effects = ImageFilterUtils.applyExifRotateEffect((HttpServletRequest)request, request.getParameter("effects"));
        if (effects == null) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpReq = (HttpServletRequest)request;
        Matcher matcher = downloadPattern.matcher(httpReq.getRequestURL().append("?").append(httpReq.getQueryString()));
        if (!matcher.find()) {
            chain.doFilter(request, response);
            return;
        }
        String type = matcher.group("type");
        String contentId = matcher.group("id");
        String fileName = matcher.group("filename");
        if (this.isImageFormatNotSupported(fileName)) {
            chain.doFilter(request, response);
            return;
        }
        Matcher thumbMatcher = downloadPattern.matcher(fileName);
        while (thumbMatcher.find()) {
            fileName = thumbMatcher.group(3);
            thumbMatcher = downloadPattern.matcher(fileName);
        }
        String version = matcher.group(5);
        if (type.equals("thumbnails")) {
            effects = (String)effects + ",thumbnail";
        }
        String newUrl = "/plugins/servlet/imgFilter?ceo=" + contentId + "&image=" + fileName + "&effects=" + (String)effects + (String)(version != null ? "&version=" + version : "");
        RequestDispatcher dispatcher = request.getRequestDispatcher(newUrl);
        dispatcher.forward(request, response);
    }

    private boolean isImageFormatNotSupported(String fileName) {
        FileFormat fileFormat = FileFormat.fromFileName((String)fileName);
        return fileFormat == FileFormat.PSD && !this.isPsdEnabled() || fileFormat == FileFormat.TIF && !this.isTifEnabled();
    }

    private boolean isTifEnabled() {
        return Boolean.getBoolean("confluence.document.conversion.imaging.enabled.tif");
    }

    private boolean isPsdEnabled() {
        return Boolean.getBoolean("confluence.document.conversion.imaging.enabled.psd");
    }
}

