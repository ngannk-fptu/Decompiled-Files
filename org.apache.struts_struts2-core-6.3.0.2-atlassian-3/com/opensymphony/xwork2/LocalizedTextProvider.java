/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

public interface LocalizedTextProvider
extends Serializable {
    public String findDefaultText(String var1, Locale var2);

    public String findDefaultText(String var1, Locale var2, Object[] var3);

    public ResourceBundle findResourceBundle(String var1, Locale var2);

    public String findText(Class var1, String var2, Locale var3);

    public String findText(Class var1, String var2, Locale var3, String var4, Object[] var5);

    public String findText(Class var1, String var2, Locale var3, String var4, Object[] var5, ValueStack var6);

    public String findText(ResourceBundle var1, String var2, Locale var3);

    public String findText(ResourceBundle var1, String var2, Locale var3, String var4, Object[] var5);

    public String findText(ResourceBundle var1, String var2, Locale var3, String var4, Object[] var5, ValueStack var6);

    public void addDefaultResourceBundle(String var1);
}

