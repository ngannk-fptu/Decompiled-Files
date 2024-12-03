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
 */
package com.opensymphony.module.sitemesh.multipass;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.filter.PageResponseWrapper;
import com.opensymphony.module.sitemesh.multipass.MultipassReplacementPageParser;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MultipassFilter
extends SiteMeshFilter {
    protected void writeDecorator(final HttpServletResponse response, final Page page, RequestDispatcher dispatcher, HttpServletRequest request) throws ServletException, IOException {
        PageResponseWrapper pageResponse = new PageResponseWrapper(response, request, new PageParserSelector(){

            public boolean shouldParsePage(String contentType) {
                return true;
            }

            public PageParser getPageParser(String contentType) {
                return new MultipassReplacementPageParser(page, response);
            }
        });
        pageResponse.activateSiteMesh("text/html", "");
        dispatcher.include((ServletRequest)request, (ServletResponse)pageResponse);
        pageResponse.getPage();
    }
}

