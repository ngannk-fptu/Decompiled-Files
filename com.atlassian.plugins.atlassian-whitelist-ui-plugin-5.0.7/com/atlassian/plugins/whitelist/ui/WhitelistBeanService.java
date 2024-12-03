/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.plugins.whitelist.ui.WhitelistBean;
import java.util.List;

public interface WhitelistBeanService {
    public WhitelistBean add(WhitelistBean var1);

    public WhitelistBean update(int var1, WhitelistBean var2);

    public List<WhitelistBean> getAll();
}

