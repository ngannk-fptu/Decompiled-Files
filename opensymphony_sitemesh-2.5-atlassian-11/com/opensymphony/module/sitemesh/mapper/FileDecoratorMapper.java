/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import com.opensymphony.module.sitemesh.mapper.DefaultDecorator;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;

public class FileDecoratorMapper
extends AbstractDecoratorMapper {
    private boolean pathNotAvailable = false;

    public Decorator getNamedDecorator(HttpServletRequest req, String name) {
        URL resourcePath;
        if (this.pathNotAvailable || name == null) {
            return super.getNamedDecorator(req, name);
        }
        try {
            resourcePath = this.config.getServletContext().getResource('/' + name);
        }
        catch (MalformedURLException e) {
            return super.getNamedDecorator(req, name);
        }
        String filePath = this.config.getServletContext().getRealPath(name);
        if (filePath == null && resourcePath == null) {
            this.pathNotAvailable = true;
            return super.getNamedDecorator(req, name);
        }
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists() && file.canRead() && file.isFile()) {
                return new DefaultDecorator(name, name, null);
            }
            return super.getNamedDecorator(req, name);
        }
        return new DefaultDecorator(name, name, null);
    }
}

