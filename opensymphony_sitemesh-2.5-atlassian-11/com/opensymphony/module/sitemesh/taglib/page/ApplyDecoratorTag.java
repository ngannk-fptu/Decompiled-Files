/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.BodyTagSupport
 */
package com.opensymphony.module.sitemesh.taglib.page;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.SitemeshBufferWriter;
import com.opensymphony.module.sitemesh.filter.PageRequestWrapper;
import com.opensymphony.module.sitemesh.filter.PageResponseWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ApplyDecoratorTag
extends BodyTagSupport
implements RequestConstants {
    private String page = null;
    private String decorator = null;
    private String contentType = null;
    private String encoding = null;
    private Map params = new HashMap(6);
    private Config config = null;
    private DecoratorMapper decoratorMapper = null;
    private Factory factory;

    public void setPage(String page) {
        this.page = page;
    }

    void addParam(String name, String value) {
        this.params.put(name, value);
    }

    public void setTitle(String title) {
        this.addParam("title", title);
    }

    public void setId(String id) {
        this.addParam("id", id);
    }

    public void setName(String decorator) {
        if (decorator != null) {
            this.decorator = decorator;
        }
    }

    public void setDecorator(String decorator) {
        this.setName(decorator);
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int doStartTag() {
        if (this.config == null) {
            this.config = new Config(this.pageContext.getServletConfig());
            this.factory = Factory.getInstance(this.config);
            this.decoratorMapper = this.factory.getDecoratorMapper();
        }
        return 2;
    }

    public int doAfterBody() throws JspException {
        return 0;
    }

    public int doEndTag() throws JspException {
        try {
            Page pageObj;
            Page oldPage = (Page)this.pageContext.getRequest().getAttribute(PAGE);
            PageParser parser = this.getParserSelector().getPageParser(this.contentType != null ? this.contentType : "text/html");
            if (this.page == null) {
                if (this.bodyContent != null) {
                    SitemeshBufferWriter sitemeshWriter = new SitemeshBufferWriter();
                    this.bodyContent.writeOut((Writer)sitemeshWriter);
                    pageObj = parser.parse(sitemeshWriter.getSitemeshBuffer());
                } else {
                    pageObj = parser.parse(new DefaultSitemeshBuffer(new char[0]));
                }
            } else if (this.page.startsWith("http://") || this.page.startsWith("https://")) {
                try {
                    int moved;
                    URL url = new URL(this.page);
                    URLConnection urlConn = url.openConnection();
                    urlConn.setUseCaches(true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    SitemeshBufferWriter sitemeshWriter = new SitemeshBufferWriter();
                    char[] buf = new char[1000];
                    while ((moved = in.read(buf)) >= 0) {
                        sitemeshWriter.write(buf, 0, moved);
                    }
                    in.close();
                    pageObj = parser.parse(sitemeshWriter.getSitemeshBuffer());
                }
                catch (MalformedURLException e) {
                    throw new JspException((Throwable)e);
                }
                catch (IOException e) {
                    throw new JspException((Throwable)e);
                }
            } else {
                String fullPath = this.page;
                if (fullPath.length() > 0 && fullPath.charAt(0) != '/') {
                    int dotdot;
                    HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
                    String thisPath = request.getServletPath();
                    if (thisPath == null) {
                        String requestURI = request.getRequestURI();
                        thisPath = request.getPathInfo() != null ? requestURI.substring(0, requestURI.indexOf(request.getPathInfo())) : requestURI;
                    }
                    fullPath = thisPath.substring(0, thisPath.lastIndexOf(47) + 1) + fullPath;
                    while ((dotdot = fullPath.indexOf("..")) > -1) {
                        int prevSlash = fullPath.lastIndexOf(47, dotdot - 2);
                        fullPath = fullPath.substring(0, prevSlash) + fullPath.substring(dotdot + 2);
                    }
                }
                RequestDispatcher rd = this.pageContext.getServletContext().getRequestDispatcher(fullPath);
                PageRequestWrapper pageRequest = new PageRequestWrapper((HttpServletRequest)this.pageContext.getRequest());
                PageResponseWrapper pageResponse = new PageResponseWrapper((HttpServletResponse)this.pageContext.getResponse(), (HttpServletRequest)pageRequest, (PageParserSelector)this.factory);
                StringBuffer sb = new StringBuffer(this.contentType != null ? this.contentType : "text/html");
                if (this.encoding != null) {
                    sb.append(";charset=").append(this.encoding);
                }
                pageResponse.setContentType(sb.toString());
                if (rd == null) {
                    throw new ApplyDecoratorException("The specified resource in applyDecorator tag (" + fullPath + ") was not found.");
                }
                rd.include((ServletRequest)pageRequest, (ServletResponse)pageResponse);
                pageObj = pageResponse.getPage();
            }
            if (pageObj == null) {
                throw new ApplyDecoratorException(this.page + " did not create a valid page to decorate.");
            }
            for (String k : this.params.keySet()) {
                String v = (String)this.params.get(k);
                pageObj.addProperty(k, v);
            }
            if (this.decorator == null) {
                this.decorator = "";
            }
            pageObj.setRequest((HttpServletRequest)this.pageContext.getRequest());
            this.pageContext.getRequest().setAttribute(DECORATOR, (Object)this.decorator);
            Decorator d = this.decoratorMapper.getDecorator((HttpServletRequest)this.pageContext.getRequest(), pageObj);
            this.pageContext.getRequest().removeAttribute(DECORATOR);
            if (d == null || d.getPage() == null) {
                throw new JspException("Cannot locate inline Decorator: " + this.decorator);
            }
            this.pageContext.getRequest().setAttribute(PAGE, (Object)pageObj);
            this.pageContext.include(d.getPage());
            this.pageContext.getRequest().setAttribute(PAGE, (Object)oldPage);
            this.params.clear();
        }
        catch (IOException e) {
            throw new JspException((Throwable)e);
        }
        catch (ServletException e) {
            throw new JspException((Throwable)e);
        }
        catch (ApplyDecoratorException e) {
            try {
                this.pageContext.getOut().println(e.getMessage());
            }
            catch (IOException ioe) {
                System.err.println("IOException thrown in applyDecorator tag: " + e.toString());
            }
        }
        return 6;
    }

    private PageParserSelector getParserSelector() {
        return Factory.getInstance(this.config);
    }

    class ApplyDecoratorException
    extends Exception {
        public ApplyDecoratorException(String s) {
            super(s);
        }
    }
}

