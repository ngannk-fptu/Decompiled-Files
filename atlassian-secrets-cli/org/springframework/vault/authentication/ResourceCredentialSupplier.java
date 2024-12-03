/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.CredentialSupplier;

public class ResourceCredentialSupplier
implements CredentialSupplier {
    static final Charset CHARSET = StandardCharsets.US_ASCII;
    private final Resource resource;

    public ResourceCredentialSupplier(String path) {
        this(new FileSystemResource(path));
    }

    public ResourceCredentialSupplier(File file) {
        this(new FileSystemResource(file));
    }

    public ResourceCredentialSupplier(Resource resource) {
        Assert.isTrue(resource.exists(), () -> String.format("Resource %s does not exist", resource));
        this.resource = resource;
    }

    @Override
    public String get() {
        try {
            return new String(ResourceCredentialSupplier.readToken(this.resource), CHARSET);
        }
        catch (IOException e) {
            throw new VaultException(String.format("Credential retrieval from %s failed", this.resource), e);
        }
    }

    Resource getResource() {
        return this.resource;
    }

    private static byte[] readToken(Resource resource) throws IOException {
        Assert.notNull((Object)resource, "Resource must not be null");
        try (InputStream is = resource.getInputStream();){
            byte[] byArray = StreamUtils.copyToByteArray(is);
            return byArray;
        }
    }
}

