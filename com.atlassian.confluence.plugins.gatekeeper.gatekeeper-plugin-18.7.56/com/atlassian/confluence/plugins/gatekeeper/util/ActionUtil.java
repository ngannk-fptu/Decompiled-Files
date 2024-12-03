/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.node.BaseJsonNode
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.plugins.gatekeeper.util;

import com.atlassian.confluence.plugins.gatekeeper.util.HttpUtil;
import com.atlassian.confluence.plugins.gatekeeper.util.IoUtil;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;

public class ActionUtil {
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    public static void sendJsonResponse(BaseJsonNode jsonNode) throws IOException {
        ActionUtil.sendJsonResponse(jsonNode.toString());
    }

    public static void sendJsonResponse(String json) throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding("UTF-8");
        HttpUtil.setNoCacheHeaders(response);
        ServletOutputStream os = response.getOutputStream();
        os.write(json.getBytes("UTF-8"));
    }

    public static void sendTextResponse(String data) throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType(CONTENT_TYPE_TEXT_PLAIN);
        response.setCharacterEncoding("UTF-8");
        HttpUtil.setNoCacheHeaders(response);
        ServletOutputStream os = response.getOutputStream();
        os.write(data.getBytes("UTF-8"));
        os.close();
    }

    public static void sendFileResponse(String filename, String contentType, File file, String hash) throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpUtil.setNoCacheHeaders(response);
        response.setContentType(contentType);
        filename = HttpUtil.encodeURIComponent(filename);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename + ";");
        response.setHeader("Set-Cookie", "content-export-" + hash + "=success; path=/");
        IoUtil.copyStreamToStream(new FileInputStream(file), (OutputStream)response.getOutputStream());
    }

    public static void sendBadResponse() throws IOException {
        ActionUtil.sendBadResponse(null);
    }

    public static void sendBadResponse(String message) throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpUtil.setNoCacheHeaders(response);
        response.setContentType(CONTENT_TYPE_TEXT_PLAIN);
        response.setStatus(400);
        if (message != null) {
            ServletOutputStream os = response.getOutputStream();
            os.write(message.getBytes("UTF-8"));
            os.close();
        }
    }

    public static void sendNotFoundResponse() {
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpUtil.setNoCacheHeaders(response);
        response.setContentType(CONTENT_TYPE_TEXT_PLAIN);
        response.setStatus(404);
    }

    public static void sendForbiddenResponse(String message) throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpUtil.setNoCacheHeaders(response);
        response.setContentType(CONTENT_TYPE_TEXT_PLAIN);
        response.setStatus(403);
        if (message != null) {
            ServletOutputStream os = response.getOutputStream();
            os.write(message.getBytes("UTF-8"));
            os.close();
        }
    }
}

