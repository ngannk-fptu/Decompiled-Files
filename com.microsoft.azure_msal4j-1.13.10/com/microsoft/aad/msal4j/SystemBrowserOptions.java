/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.OpenBrowserAction;
import java.net.URI;

public class SystemBrowserOptions {
    private String htmlMessageSuccess;
    private String htmlMessageError;
    private URI browserRedirectSuccess;
    private URI browserRedirectError;
    private OpenBrowserAction openBrowserAction;

    public static SystemBrowserOptionsBuilder builder() {
        return new SystemBrowserOptionsBuilder();
    }

    public String htmlMessageSuccess() {
        return this.htmlMessageSuccess;
    }

    public String htmlMessageError() {
        return this.htmlMessageError;
    }

    public URI browserRedirectSuccess() {
        return this.browserRedirectSuccess;
    }

    public URI browserRedirectError() {
        return this.browserRedirectError;
    }

    public OpenBrowserAction openBrowserAction() {
        return this.openBrowserAction;
    }

    private SystemBrowserOptions(String htmlMessageSuccess, String htmlMessageError, URI browserRedirectSuccess, URI browserRedirectError, OpenBrowserAction openBrowserAction) {
        this.htmlMessageSuccess = htmlMessageSuccess;
        this.htmlMessageError = htmlMessageError;
        this.browserRedirectSuccess = browserRedirectSuccess;
        this.browserRedirectError = browserRedirectError;
        this.openBrowserAction = openBrowserAction;
    }

    public static class SystemBrowserOptionsBuilder {
        private String htmlMessageSuccess;
        private String htmlMessageError;
        private URI browserRedirectSuccess;
        private URI browserRedirectError;
        private OpenBrowserAction openBrowserAction;

        SystemBrowserOptionsBuilder() {
        }

        public SystemBrowserOptionsBuilder htmlMessageSuccess(String htmlMessageSuccess) {
            this.htmlMessageSuccess = htmlMessageSuccess;
            return this;
        }

        public SystemBrowserOptionsBuilder htmlMessageError(String htmlMessageError) {
            this.htmlMessageError = htmlMessageError;
            return this;
        }

        public SystemBrowserOptionsBuilder browserRedirectSuccess(URI browserRedirectSuccess) {
            this.browserRedirectSuccess = browserRedirectSuccess;
            return this;
        }

        public SystemBrowserOptionsBuilder browserRedirectError(URI browserRedirectError) {
            this.browserRedirectError = browserRedirectError;
            return this;
        }

        public SystemBrowserOptionsBuilder openBrowserAction(OpenBrowserAction openBrowserAction) {
            this.openBrowserAction = openBrowserAction;
            return this;
        }

        public SystemBrowserOptions build() {
            return new SystemBrowserOptions(this.htmlMessageSuccess, this.htmlMessageError, this.browserRedirectSuccess, this.browserRedirectError, this.openBrowserAction);
        }

        public String toString() {
            return "SystemBrowserOptions.SystemBrowserOptionsBuilder(htmlMessageSuccess=" + this.htmlMessageSuccess + ", htmlMessageError=" + this.htmlMessageError + ", browserRedirectSuccess=" + this.browserRedirectSuccess + ", browserRedirectError=" + this.browserRedirectError + ", openBrowserAction=" + this.openBrowserAction + ")";
        }
    }
}

