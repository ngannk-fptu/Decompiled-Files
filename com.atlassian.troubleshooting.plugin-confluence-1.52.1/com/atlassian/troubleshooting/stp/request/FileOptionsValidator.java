/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.action.Validateable;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequest;
import java.io.Serializable;
import java.util.Map;

public class FileOptionsValidator
implements Validateable {
    private final SupportApplicationInfo applicationInfo;

    public FileOptionsValidator(SupportApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    @Override
    public void validate(Map<String, Object> context, SafeHttpServletRequest req, ValidationLog validationLog) {
        int selectedBundles = 0;
        for (SupportZipBundle applicationFileBundle : this.applicationInfo.getSupportZipBundles()) {
            if (!Boolean.parseBoolean(req.getParameter(applicationFileBundle.getKey()))) continue;
            ++selectedBundles;
            break;
        }
        if (selectedBundles == 0) {
            validationLog.addError("stp.create.support.zip.no.options.error", new Serializable[0]);
        }
    }
}

