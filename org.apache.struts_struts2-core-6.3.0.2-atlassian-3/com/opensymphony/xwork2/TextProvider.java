/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.List;
import java.util.ResourceBundle;

public interface TextProvider {
    public boolean hasKey(String var1);

    public String getText(String var1);

    public String getText(String var1, String var2);

    public String getText(String var1, String var2, String var3);

    public String getText(String var1, List<?> var2);

    public String getText(String var1, String[] var2);

    public String getText(String var1, String var2, List<?> var3);

    public String getText(String var1, String var2, String[] var3);

    public String getText(String var1, String var2, List<?> var3, ValueStack var4);

    public String getText(String var1, String var2, String[] var3, ValueStack var4);

    public ResourceBundle getTexts(String var1);

    public ResourceBundle getTexts();
}

