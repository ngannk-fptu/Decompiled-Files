/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.util;

import java.time.Clock;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.DefaultAwsCrtV4aHttpSigner;
import software.amazon.awssdk.http.auth.aws.eventstream.internal.signer.EventStreamV4PayloadSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.utils.ClassLoaderHelper;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class OptionalDependencyLoaderUtil {
    private static final Logger LOG = Logger.loggerFor(OptionalDependencyLoaderUtil.class);
    private static final String HTTP_AUTH_AWS_CRT_PATH = "software.amazon.awssdk.http.auth.aws.crt.HttpAuthAwsCrt";
    private static final String HTTP_AUTH_AWS_CRT_MODULE = "software.amazon.awssdk:http-auth-aws-crt";
    private static final String HTTP_AUTH_AWS_EVENT_STREAM_PATH = "software.amazon.awssdk.http.auth.aws.eventstream.HttpAuthAwsEventStream";
    private static final String HTTP_AUTH_AWS_EVENT_STREAM_MODULE = "software.amazon.awssdk:http-auth-aws-eventstream";

    private OptionalDependencyLoaderUtil() {
    }

    private static void requireClass(String classPath, String module, String feature) {
        try {
            ClassLoaderHelper.loadClass(classPath, false, new Class[0]);
        }
        catch (ClassNotFoundException e) {
            LOG.debug(() -> "Cannot find the " + classPath + " class: ", e);
            String msg = String.format("Could not load class. You must add a dependency on the '%s' module to enable the %s feature: ", module, feature);
            throw new RuntimeException(msg, e);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Could not load class (%s): ", classPath), e);
        }
    }

    public static DefaultAwsCrtV4aHttpSigner getDefaultAwsCrtV4aHttpSigner() {
        OptionalDependencyLoaderUtil.requireClass(HTTP_AUTH_AWS_CRT_PATH, HTTP_AUTH_AWS_CRT_MODULE, "CRT-V4a signing");
        return new DefaultAwsCrtV4aHttpSigner();
    }

    public static EventStreamV4PayloadSigner getEventStreamV4PayloadSigner(AwsCredentialsIdentity credentials, CredentialScope credentialScope, Clock signingClock) {
        OptionalDependencyLoaderUtil.requireClass(HTTP_AUTH_AWS_EVENT_STREAM_PATH, HTTP_AUTH_AWS_EVENT_STREAM_MODULE, "Event-stream signing");
        return EventStreamV4PayloadSigner.builder().credentials(credentials).credentialScope(credentialScope).signingClock(signingClock).build();
    }
}

