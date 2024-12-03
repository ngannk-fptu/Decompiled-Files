/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jdk.utilities.runtimeinformation;

import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformation;
import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformationFactory;

@Deprecated
public class RuntimeInformationBeanFactory {
    public static RuntimeInformation getRuntimeInformationBean() {
        return RuntimeInformationFactory.getRuntimeInformation();
    }
}

