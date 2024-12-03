/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.signer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.RollingSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.Resettable;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.TrailerProvider;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class SigV4aTrailerProvider
implements TrailerProvider {
    private final List<TrailerProvider> trailerProviders = new ArrayList<TrailerProvider>();
    private final RollingSigner signer;
    private final CredentialScope credentialScope;

    public SigV4aTrailerProvider(List<TrailerProvider> trailerProviders, RollingSigner signer, CredentialScope credentialScope) {
        this.trailerProviders.addAll(trailerProviders);
        this.signer = signer;
        this.credentialScope = credentialScope;
    }

    @Override
    public void reset() {
        this.trailerProviders.forEach(Resettable::reset);
        this.signer.reset();
    }

    @Override
    public Pair<String, List<String>> get() {
        byte[] trailerSig = this.signer.sign(this.getTrailers());
        return Pair.of("x-amz-trailer-signature", Collections.singletonList(new String(trailerSig, StandardCharsets.UTF_8)));
    }

    private Map<String, List<String>> getTrailers() {
        return this.trailerProviders.stream().map(TrailerProvider::get).collect(Collectors.toMap(Pair::left, Pair::right));
    }
}

