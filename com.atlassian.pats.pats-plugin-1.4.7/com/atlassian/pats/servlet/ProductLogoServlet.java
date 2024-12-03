/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.pats.servlet;

import com.atlassian.pats.utils.ProductHelper;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

public class ProductLogoServlet
extends HttpServlet {
    public static final String URL = "/plugins/servlet/personal-tokens/product-logo.png";
    private final ProductHelper productHelper;

    public ProductLogoServlet(ProductHelper productHelper) {
        this.productHelper = productHelper;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/png");
        try (InputStream is = ((Object)((Object)this)).getClass().getResourceAsStream(this.productHelper.getLogoResource());
             BufferedOutputStream os = new BufferedOutputStream((OutputStream)response.getOutputStream());){
            IOUtils.copy((InputStream)is, (OutputStream)os);
        }
    }
}

