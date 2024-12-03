/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.http.auth.aws.internal.signer.RollingSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4CanonicalRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.Resettable;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.TrailerProvider;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerUtils;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class SigV4TrailerProvider
implements TrailerProvider {
    private final List<TrailerProvider> trailerProviders = new ArrayList<TrailerProvider>();
    private final RollingSigner signer;
    private final CredentialScope credentialScope;

    public SigV4TrailerProvider(List<TrailerProvider> trailerProviders, RollingSigner signer, CredentialScope credentialScope) {
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
        String trailerSig = this.signer.sign(this::getTrailersStringToSign);
        return Pair.of((Object)"x-amz-trailer-signature", Collections.singletonList(trailerSig));
    }

    private String getTrailersStringToSign(String previousSignature) {
        Map<String, List<String>> headers = this.trailerProviders.stream().map(TrailerProvider::get).collect(Collectors.toMap(Pair::left, Pair::right));
        String canonicalHeadersString = V4CanonicalRequest.getCanonicalHeadersString(V4CanonicalRequest.getCanonicalHeaders(headers));
        String canonicalHashHex = BinaryUtils.toHex((byte[])SignerUtils.hash(canonicalHeadersString));
        return String.join((CharSequence)"\n", "AWS4-HMAC-SHA256-TRAILER", this.credentialScope.getDatetime(), this.credentialScope.scope(), previousSignature, canonicalHashHex);
    }
}

