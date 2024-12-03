/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.IoUtils
 */
package software.amazon.awssdk.services.sts.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest;
import software.amazon.awssdk.utils.IoUtils;

@SdkInternalApi
public class AssumeRoleWithWebIdentityRequestSupplier
implements Supplier<AssumeRoleWithWebIdentityRequest> {
    private final AssumeRoleWithWebIdentityRequest request;
    private final Path webIdentityTokenFile;

    public AssumeRoleWithWebIdentityRequestSupplier(Builder builder) {
        this.request = builder.request;
        this.webIdentityTokenFile = builder.webIdentityTokenFile;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public AssumeRoleWithWebIdentityRequest get() {
        return (AssumeRoleWithWebIdentityRequest)((Object)this.request.toBuilder().webIdentityToken(this.getToken(this.webIdentityTokenFile)).build());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private String getToken(Path file) {
        try (InputStream webIdentityTokenStream = Files.newInputStream(file, new OpenOption[0]);){
            String string = IoUtils.toUtf8String((InputStream)webIdentityTokenStream);
            return string;
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static class Builder {
        private AssumeRoleWithWebIdentityRequest request;
        private Path webIdentityTokenFile;

        public Builder assumeRoleWithWebIdentityRequest(AssumeRoleWithWebIdentityRequest request) {
            this.request = request;
            return this;
        }

        public Builder webIdentityTokenFile(Path webIdentityTokenFile) {
            this.webIdentityTokenFile = webIdentityTokenFile;
            return this;
        }

        public AssumeRoleWithWebIdentityRequestSupplier build() {
            return new AssumeRoleWithWebIdentityRequestSupplier(this);
        }
    }
}

