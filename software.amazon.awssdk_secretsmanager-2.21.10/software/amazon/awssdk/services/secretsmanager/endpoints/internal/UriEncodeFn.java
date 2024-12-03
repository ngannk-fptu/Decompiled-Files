/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.SingleArgFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;

@SdkInternalApi
public class UriEncodeFn
extends SingleArgFn {
    public static final String ID = "uriEncode";
    private static final String[] ENCODED_CHARACTERS = new String[]{"+", "*", "%7E"};
    private static final String[] ENCODED_CHARACTERS_REPLACEMENTS = new String[]{"%20", "%2A", "~"};

    public UriEncodeFn(FnNode fnNode) {
        super(fnNode);
    }

    @Override
    protected Value evalArg(Value arg) {
        String url = arg.expectString();
        try {
            String encoded = URLEncoder.encode(url, "UTF-8");
            for (int i = 0; i < ENCODED_CHARACTERS.length; ++i) {
                encoded = encoded.replace(ENCODED_CHARACTERS[i], ENCODED_CHARACTERS_REPLACEMENTS[i]);
            }
            return Value.fromStr(encoded);
        }
        catch (UnsupportedEncodingException e) {
            throw SdkClientException.create((String)("Unable to URI encode value: " + url), (Throwable)e);
        }
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitUriEncode(this);
    }
}

