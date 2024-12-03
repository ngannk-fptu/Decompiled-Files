/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.codehaus.jackson.Version
 *  org.codehaus.jackson.map.module.SimpleModule
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ui.rest.json;

import com.atlassian.confluence.ui.rest.json.LegacyOptionSerializer;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.stereotype.Component;

@Deprecated
@ExportAsService(value={SimpleModule.class})
@Component(value="customSerializers")
public class LegacyCustomSerializer
extends SimpleModule {
    public LegacyCustomSerializer() {
        super("Custom Serializers", new Version(1, 0, 0, null));
        this.addSerializer(new LegacyOptionSerializer());
    }
}

