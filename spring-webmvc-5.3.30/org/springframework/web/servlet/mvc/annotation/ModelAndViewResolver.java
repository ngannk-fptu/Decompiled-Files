/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.ui.ExtendedModelMap
 *  org.springframework.web.context.request.NativeWebRequest
 */
package org.springframework.web.servlet.mvc.annotation;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

public interface ModelAndViewResolver {
    public static final ModelAndView UNRESOLVED = new ModelAndView();

    public ModelAndView resolveModelAndView(Method var1, Class<?> var2, @Nullable Object var3, ExtendedModelMap var4, NativeWebRequest var5);
}

