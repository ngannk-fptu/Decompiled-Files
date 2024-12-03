/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.apache.poi.util.DefaultTempFileCreationStrategy
 *  org.apache.poi.util.TempFile
 *  org.apache.poi.util.TempFileCreationStrategy
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import java.nio.file.Path;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.apache.poi.util.DefaultTempFileCreationStrategy;
import org.apache.poi.util.TempFile;
import org.apache.poi.util.TempFileCreationStrategy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\b\u001a\u00020\tH\u0016J\b\u0010\n\u001a\u00020\tH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2={"Lcom/addonengine/addons/analytics/AddonInitialiser;", "Lorg/springframework/beans/factory/InitializingBean;", "applicationProperties", "Lcom/atlassian/sal/api/ApplicationProperties;", "(Lcom/atlassian/sal/api/ApplicationProperties;)V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "afterPropertiesSet", "", "initExcelTempFileCreationStrategy", "analytics"})
public final class AddonInitialiser
implements InitializingBean {
    @NotNull
    private final ApplicationProperties applicationProperties;
    private final Logger log;

    @Autowired
    public AddonInitialiser(@ComponentImport @NotNull ApplicationProperties applicationProperties) {
        Intrinsics.checkNotNullParameter((Object)applicationProperties, (String)"applicationProperties");
        this.applicationProperties = applicationProperties;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    public void afterPropertiesSet() {
        this.initExcelTempFileCreationStrategy();
    }

    private final void initExcelTempFileCreationStrategy() {
        Path tempPath = ((Path)this.applicationProperties.getLocalHomeDirectory().get()).resolve("temp");
        TempFile.setTempFileCreationStrategy((TempFileCreationStrategy)((TempFileCreationStrategy)new DefaultTempFileCreationStrategy(tempPath.toFile())));
        this.log.debug("Excel temp file path set to '" + tempPath + '\'');
    }
}

