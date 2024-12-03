/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.ext.servlet.FreemarkerServlet
 *  freemarker.template.SimpleHash
 *  freemarker.template.Template
 *  freemarker.template.TemplateModel
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.opensymphony.module.sitemesh.freemarker;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.RequestConstants;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import java.io.IOException;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FreemarkerDecoratorServlet
extends FreemarkerServlet {
    protected boolean preTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel templateModel) throws ServletException, IOException {
        String head;
        String body;
        String title;
        boolean result = super.preTemplateProcess(request, response, template, templateModel);
        SimpleHash hash = (SimpleHash)templateModel;
        HTMLPage htmlPage = (HTMLPage)request.getAttribute(RequestConstants.PAGE);
        if (htmlPage == null) {
            title = "No Title";
            body = "No Body";
            head = "<!-- No head -->";
        } else {
            title = htmlPage.getTitle();
            StringWriter buffer = new StringWriter();
            htmlPage.writeBody(buffer);
            body = buffer.toString();
            buffer = new StringWriter();
            htmlPage.writeHead(buffer);
            head = buffer.toString();
            hash.put("page", (Object)htmlPage);
        }
        hash.put("title", (Object)title);
        hash.put("body", (Object)body);
        hash.put("head", (Object)head);
        hash.put("base", (Object)request.getContextPath());
        return result;
    }
}

