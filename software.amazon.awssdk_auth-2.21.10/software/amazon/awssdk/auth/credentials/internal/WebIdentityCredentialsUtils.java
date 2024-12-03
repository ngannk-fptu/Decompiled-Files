/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.internal.util.ClassLoaderHelper
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.auth.credentials.internal;

import java.lang.reflect.InvocationTargetException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenCredentialsProviderFactory;
import software.amazon.awssdk.core.internal.util.ClassLoaderHelper;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class WebIdentityCredentialsUtils {
    private static final Logger log = Logger.loggerFor(WebIdentityCredentialsUtils.class);
    private static final String STS_WEB_IDENTITY_CREDENTIALS_PROVIDER_FACTORY = "software.amazon.awssdk.services.sts.internal.StsWebIdentityCredentialsProviderFactory";

    private WebIdentityCredentialsUtils() {
    }

    public static WebIdentityTokenCredentialsProviderFactory factory() {
        try {
            Class stsCredentialsProviderFactory = ClassLoaderHelper.loadClass((String)STS_WEB_IDENTITY_CREDENTIALS_PROVIDER_FACTORY, (Class[])new Class[]{WebIdentityCredentialsUtils.class});
            return (WebIdentityTokenCredentialsProviderFactory)stsCredentialsProviderFactory.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException e) {
            String message = "To use web identity tokens, the 'sts' service module must be on the class path.";
            log.warn(() -> message);
            throw new IllegalStateException(message, e);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create a web identity token credentials provider.", e);
        }
    }
}

