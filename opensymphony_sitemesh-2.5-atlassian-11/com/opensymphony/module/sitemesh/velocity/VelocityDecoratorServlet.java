/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.velocity.Template
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.tools.view.servlet.VelocityViewServlet
 */
package com.opensymphony.module.sitemesh.velocity;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.util.OutputConverter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;

public class VelocityDecoratorServlet
extends VelocityViewServlet {
    public Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context context) throws Exception {
        String template;
        HTMLPage htmlPage = (HTMLPage)request.getAttribute(RequestConstants.PAGE);
        context.put("base", (Object)request.getContextPath());
        context.put("req", (Object)request);
        context.put("res", (Object)response);
        if (htmlPage == null) {
            context.put("title", (Object)"Title?");
            context.put("body", (Object)"<p>Body?</p>");
            context.put("head", (Object)"<!-- head -->");
            template = request.getServletPath();
        } else {
            context.put("title", (Object)OutputConverter.convert(htmlPage.getTitle()));
            StringWriter buffer = new StringWriter();
            htmlPage.writeBody(OutputConverter.getWriter(buffer));
            context.put("body", (Object)buffer.toString());
            buffer = new StringWriter();
            htmlPage.writeHead(OutputConverter.getWriter(buffer));
            context.put("head", (Object)buffer.toString());
            context.put("page", (Object)htmlPage);
            DecoratorMapper decoratorMapper = this.getDecoratorMapper();
            Decorator decorator = decoratorMapper.getDecorator(request, htmlPage);
            template = decorator.getPage();
        }
        return this.getTemplate(template);
    }

    private DecoratorMapper getDecoratorMapper() {
        Factory factory = Factory.getInstance(new Config(this.getServletConfig()));
        DecoratorMapper decoratorMapper = factory.getDecoratorMapper();
        return decoratorMapper;
    }
}

