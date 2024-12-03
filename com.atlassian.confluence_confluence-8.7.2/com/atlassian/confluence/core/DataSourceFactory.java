/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.user.User
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Maybe;
import com.atlassian.user.User;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import javax.activation.DataSource;

public interface DataSourceFactory {
    public DataSource getAvatar(User var1);

    public DataSource getSpaceLogo(Space var1);

    public DataSource getServletContainerResource(String var1, String var2);

    public DataSource getURLResource(URL var1, String var2);

    public DataSource getDatasource(Attachment var1, boolean var2) throws IOException;

    @Deprecated
    default public Maybe<PluginDataSourceFactory> forPlugin(String pluginKey) {
        return FugueConversionUtil.toComMaybe(this.createForPlugin(pluginKey));
    }

    public Optional<PluginDataSourceFactory> createForPlugin(String var1);
}

