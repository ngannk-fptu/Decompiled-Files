/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 *  org.osgi.framework.Version
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.plugins.email.VersionUtil;
import com.atlassian.fugue.Maybe;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.Version;

public class NattoFunction
implements SoyServerFunction<String> {
    private static ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forCaller();
    private Maybe<Version> maybeApplicationVersion;

    public NattoFunction(ApplicationProperties applicationProperties) {
        this.maybeApplicationVersion = VersionUtil.INSTANCE.parseApplicationVersion(applicationProperties);
    }

    public String apply(Object ... args) {
        if (this.noop()) {
            return "";
        }
        Map argMap = (Map)args[0];
        String soyTemplateFileName = (String)argMap.get("soy-template-file-name");
        Version deprecateInVersion = (Version)VersionUtil.INSTANCE.parseVersion((String)argMap.get("deprecate-in-version")).get();
        Version removeInVersion = (Version)VersionUtil.INSTANCE.parseVersion((String)argMap.get("remove-in-version")).get();
        String explanationMessageTemplate = args.length < 2 ? "" : (String)args[1];
        Object[] explanationMessageArgs = args.length < 3 ? new Object[]{} : ((List)args[2]).toArray();
        Version applicationVersion = (Version)this.maybeApplicationVersion.get();
        if (applicationVersion.compareTo(removeInVersion) >= 0) {
            throw new IllegalStateException(this.createDeprecationMessage(soyTemplateFileName, deprecateInVersion, removeInVersion, explanationMessageTemplate, explanationMessageArgs));
        }
        if (applicationVersion.compareTo(deprecateInVersion) >= 0) {
            log.warnOrDebug(this.createDeprecationMessage(soyTemplateFileName, deprecateInVersion, removeInVersion, explanationMessageTemplate, explanationMessageArgs), new Object[0]);
        }
        return "";
    }

    private boolean noop() {
        return !ConfluenceSystemProperties.isDevMode() || this.maybeApplicationVersion.isEmpty();
    }

    public String getName() {
        return "natto";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)1, (Object)2, (Object)3);
    }

    private String versionToString(Version version) {
        return version.getMajor() + "." + version.getMinor();
    }

    private String createDeprecationMessage(String soyTemplateFileName, Version deprecateInVersion, Version removeInVersion, String explanationMessageTemplate, Object[] explanationMessageArgs) {
        String deprecationMessage = String.format("Soy template [%s] is deprecated since Confluence [%s] and might be removed in Confluence [%s].", soyTemplateFileName, this.versionToString(deprecateInVersion), this.versionToString(removeInVersion));
        String explanationMessage = String.format(explanationMessageTemplate, explanationMessageArgs);
        return explanationMessage.length() == 0 ? deprecationMessage : deprecationMessage + " " + explanationMessage;
    }
}

