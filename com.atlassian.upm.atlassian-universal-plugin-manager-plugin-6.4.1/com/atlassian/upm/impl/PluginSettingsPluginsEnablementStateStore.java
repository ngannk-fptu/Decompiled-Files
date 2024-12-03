/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.impl;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.upm.PluginsEnablementStateStore;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.PluginsEnablementState;
import com.atlassian.upm.core.PluginsEnablementStateAccessor;
import com.atlassian.upm.core.impl.PluginSettingsPluginsEnablementStateAccessor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class PluginSettingsPluginsEnablementStateStore
extends PluginSettingsPluginsEnablementStateAccessor
implements PluginsEnablementStateStore {
    public PluginSettingsPluginsEnablementStateStore(PluginSettingsFactory pluginSettingsFactory) {
        super(pluginSettingsFactory);
    }

    @Override
    public void saveConfiguration(PluginsEnablementState configuration) throws PluginsEnablementStateAccessor.PluginsEnablementStateStoreException {
        if (!StringUtils.isEmpty((CharSequence)this.getConfigurationString().getOrElse(""))) {
            throw new PluginsEnablementStateAccessor.PluginsEnablementStateStoreException("Cannot update the plugins configuration when one already exists");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter((OutputStream)new GZIPOutputStream(bos), StandardCharsets.UTF_8);
            osw.write(this.getMapper().writeValueAsString((Object)configuration));
            IOUtils.closeQuietly((Writer)osw);
            this.getPluginSettings().put("upm_configuration", (Object)new String(Base64.encodeBase64((byte[])bos.toByteArray()), StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            try {
                throw new PluginsEnablementStateAccessor.PluginsEnablementStateStoreException("Unable to save plugins configuration", e);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(osw);
                throw throwable;
            }
        }
        IOUtils.closeQuietly((Writer)osw);
    }

    @Override
    public Option<PluginsEnablementState> removeSavedConfiguration() throws PluginsEnablementStateAccessor.PluginsEnablementStateStoreException {
        try {
            for (Object val : Option.option(this.getPluginSettings().remove("upm_configuration"))) {
                if (!(val instanceof String)) continue;
                return this.getConfigurationFromString(String.valueOf(val));
            }
        }
        catch (Exception e) {
            throw new PluginsEnablementStateAccessor.PluginsEnablementStateStoreException("Unable to clear saved configuration", e);
        }
        return Option.none();
    }

    private Option<PluginsEnablementState> getConfigurationFromString(String value) throws IOException {
        String decompressedValue;
        byte[] bytes = this.getBase64Util().tryDecodeBase64(value);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             GZIPInputStream gzipIn = new GZIPInputStream(bis);){
            decompressedValue = IOUtils.toString((InputStream)gzipIn, (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException error) {
            return Option.some(this.getMapper().readValue(value, PluginsEnablementState.class));
        }
        return Option.some(this.getMapper().readValue(decompressedValue, PluginsEnablementState.class));
    }
}

