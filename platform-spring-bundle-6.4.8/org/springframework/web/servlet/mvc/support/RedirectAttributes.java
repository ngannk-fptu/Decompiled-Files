/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.support;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;

public interface RedirectAttributes
extends Model {
    @Override
    public RedirectAttributes addAttribute(String var1, @Nullable Object var2);

    @Override
    public RedirectAttributes addAttribute(Object var1);

    @Override
    public RedirectAttributes addAllAttributes(Collection<?> var1);

    @Override
    public RedirectAttributes mergeAttributes(Map<String, ?> var1);

    public RedirectAttributes addFlashAttribute(String var1, @Nullable Object var2);

    public RedirectAttributes addFlashAttribute(Object var1);

    public Map<String, ?> getFlashAttributes();
}

