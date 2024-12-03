/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.TextProvider;
import java.util.ResourceBundle;

public interface TextProviderFactory {
    public TextProvider createInstance(Class var1);

    public TextProvider createInstance(ResourceBundle var1);
}

