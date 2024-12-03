/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 */
package org.springframework.web.servlet;

import javax.servlet.ServletException;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

public class ModelAndViewDefiningException
extends ServletException {
    private final ModelAndView modelAndView;

    public ModelAndViewDefiningException(ModelAndView modelAndView) {
        Assert.notNull((Object)modelAndView, "ModelAndView must not be null in ModelAndViewDefiningException");
        this.modelAndView = modelAndView;
    }

    public ModelAndView getModelAndView() {
        return this.modelAndView;
    }
}

