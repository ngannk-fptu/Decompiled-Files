/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.internal.interceptor;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.SystemSetting;

@SdkInternalApi
public enum TracingSystemSetting implements SystemSetting
{
    _X_AMZN_TRACE_ID("com.amazonaws.xray.traceHeader", null);

    private final String systemProperty;
    private final String defaultValue;

    private TracingSystemSetting(String systemProperty, String defaultValue) {
        this.systemProperty = systemProperty;
        this.defaultValue = defaultValue;
    }

    @Override
    public String property() {
        return this.systemProperty;
    }

    @Override
    public String environmentVariable() {
        return this.name();
    }

    @Override
    public String defaultValue() {
        return this.defaultValue;
    }
}

