/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 *  org.springframework.context.annotation.ImportResource
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.impl.filestore.FileStoreContextConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations={"/setupContext.xml"})
@Import(value={FileStoreContextConfig.class})
public class SetupAppConfig {
}

