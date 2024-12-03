/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings.beans;

import java.io.Serializable;
import java.util.List;

public class ReferrerSettings
implements Serializable {
    private static final long serialVersionUID = 4402580617712600152L;
    private boolean collectReferrerData;
    private List<String> excludedReferrers;
    private boolean hideExternalReferrers;
}

