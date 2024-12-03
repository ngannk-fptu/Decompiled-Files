/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jdk.utilities.runtimeinformation;

import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformation;
import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformationBean;

public class RuntimeInformationFactory {
    private static final RuntimeInformation runtimeInformationBean = new RuntimeInformationBean();

    public static RuntimeInformation getRuntimeInformation() {
        return runtimeInformationBean;
    }
}

