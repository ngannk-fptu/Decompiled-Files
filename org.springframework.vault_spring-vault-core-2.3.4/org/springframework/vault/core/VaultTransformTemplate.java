/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.Base64Utils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.vault.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTransformOperations;
import org.springframework.vault.support.TransformCiphertext;
import org.springframework.vault.support.TransformPlaintext;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultTransformContext;
import org.springframework.vault.support.VaultTransformDecodeResult;
import org.springframework.vault.support.VaultTransformEncodeResult;

public class VaultTransformTemplate
implements VaultTransformOperations {
    private final VaultOperations vaultOperations;
    private final String path;

    public VaultTransformTemplate(VaultOperations vaultOperations, String path) {
        Assert.notNull((Object)vaultOperations, (String)"VaultOperations must not be null");
        Assert.hasText((String)path, (String)"Path must not be empty");
        this.vaultOperations = vaultOperations;
        this.path = path;
    }

    @Override
    public String encode(String roleName, String plaintext) {
        Assert.hasText((String)roleName, (String)"Role name must not be empty");
        Assert.notNull((Object)plaintext, (String)"Plaintext must not be null");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("value", plaintext);
        return (String)((Map)this.vaultOperations.write(String.format("%s/encode/%s", this.path, roleName), request).getRequiredData()).get("encoded_value");
    }

    @Override
    public TransformCiphertext encode(String roleName, TransformPlaintext plaintext) {
        Assert.hasText((String)roleName, (String)"Role name must not be empty");
        Assert.notNull((Object)plaintext, (String)"Plaintext must not be null");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("value", plaintext.asString());
        VaultTransformTemplate.applyTransformOptions(plaintext.getContext(), request);
        Map data = (Map)this.vaultOperations.write(String.format("%s/encode/%s", this.path, roleName), request).getRequiredData();
        return VaultTransformTemplate.toCiphertext(data, plaintext.getContext());
    }

    @Override
    public List<VaultTransformEncodeResult> encode(String roleName, List<TransformPlaintext> batchRequest) {
        Assert.hasText((String)roleName, (String)"Role name must not be empty");
        Assert.notEmpty(batchRequest, (String)"BatchRequest must not be null and must have at least one entry");
        ArrayList<LinkedHashMap<String, String>> batch = new ArrayList<LinkedHashMap<String, String>>(batchRequest.size());
        for (TransformPlaintext request : batchRequest) {
            LinkedHashMap<String, String> vaultRequest = new LinkedHashMap<String, String>(2);
            vaultRequest.put("value", request.asString());
            VaultTransformTemplate.applyTransformOptions(request.getContext(), vaultRequest);
            batch.add(vaultRequest);
        }
        VaultResponse vaultResponse = this.vaultOperations.write(String.format("%s/encode/%s", this.path, roleName), Collections.singletonMap("batch_input", batch));
        return VaultTransformTemplate.toEncodedResults(vaultResponse, batchRequest);
    }

    @Override
    public TransformPlaintext decode(String roleName, TransformCiphertext ciphertext) {
        Assert.hasText((String)roleName, (String)"Role name must not be null");
        Assert.notNull((Object)ciphertext, (String)"Ciphertext must not be null");
        String plaintext = this.decode(roleName, ciphertext.getCiphertext(), ciphertext.getContext());
        return TransformPlaintext.of(plaintext).with(ciphertext.getContext());
    }

    @Override
    public String decode(String roleName, String ciphertext, VaultTransformContext transformContext) {
        Assert.hasText((String)roleName, (String)"Role name must not be empty");
        Assert.hasText((String)ciphertext, (String)"Ciphertext must not be empty");
        Assert.notNull((Object)transformContext, (String)"VaultTransformContext must not be null");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("value", ciphertext);
        VaultTransformTemplate.applyTransformOptions(transformContext, request);
        return (String)((Map)this.vaultOperations.write(String.format("%s/decode/%s", this.path, roleName), request).getRequiredData()).get("decoded_value");
    }

    @Override
    public List<VaultTransformDecodeResult> decode(String roleName, List<TransformCiphertext> batchRequest) {
        Assert.hasText((String)roleName, (String)"Role name must not be empty");
        Assert.notEmpty(batchRequest, (String)"BatchRequest must not be null and must have at least one entry");
        ArrayList<LinkedHashMap<String, String>> batch = new ArrayList<LinkedHashMap<String, String>>(batchRequest.size());
        for (TransformCiphertext request : batchRequest) {
            LinkedHashMap<String, String> vaultRequest = new LinkedHashMap<String, String>(2);
            vaultRequest.put("value", request.getCiphertext());
            VaultTransformTemplate.applyTransformOptions(request.getContext(), vaultRequest);
            batch.add(vaultRequest);
        }
        VaultResponse vaultResponse = this.vaultOperations.write(String.format("%s/decode/%s", this.path, roleName), Collections.singletonMap("batch_input", batch));
        return VaultTransformTemplate.toDecryptionResults(vaultResponse, batchRequest);
    }

    private static void applyTransformOptions(VaultTransformContext context, Map<String, String> request) {
        if (!ObjectUtils.isEmpty((Object)context.getTransformation())) {
            request.put("transformation", context.getTransformation());
        }
        if (!ObjectUtils.isEmpty((Object)context.getTweak())) {
            request.put("tweak", Base64Utils.encodeToString((byte[])context.getTweak()));
        }
    }

    private static List<VaultTransformEncodeResult> toEncodedResults(VaultResponse vaultResponse, List<TransformPlaintext> batchRequest) {
        ArrayList<VaultTransformEncodeResult> result = new ArrayList<VaultTransformEncodeResult>(batchRequest.size());
        List<Map<String, String>> batchData = VaultTransformTemplate.getBatchData(vaultResponse);
        for (int i = 0; i < batchRequest.size(); ++i) {
            Map<String, String> data;
            TransformPlaintext plaintext = batchRequest.get(i);
            VaultTransformEncodeResult encoded = batchData.size() > i ? (StringUtils.hasText((String)(data = batchData.get(i)).get("error")) ? new VaultTransformEncodeResult(new VaultException(data.get("error"))) : new VaultTransformEncodeResult(VaultTransformTemplate.toCiphertext(data, plaintext.getContext()))) : new VaultTransformEncodeResult(new VaultException("No result for plaintext #" + i));
            result.add(encoded);
        }
        return result;
    }

    private static List<VaultTransformDecodeResult> toDecryptionResults(VaultResponse vaultResponse, List<TransformCiphertext> batchRequest) {
        ArrayList<VaultTransformDecodeResult> result = new ArrayList<VaultTransformDecodeResult>(batchRequest.size());
        List<Map<String, String>> batchData = VaultTransformTemplate.getBatchData(vaultResponse);
        for (int i = 0; i < batchRequest.size(); ++i) {
            TransformCiphertext ciphertext = batchRequest.get(i);
            VaultTransformDecodeResult encrypted = batchData.size() > i ? VaultTransformTemplate.getDecryptionResult(batchData.get(i), ciphertext) : new VaultTransformDecodeResult(new VaultException("No result for ciphertext #" + i));
            result.add(encrypted);
        }
        return result;
    }

    private static VaultTransformDecodeResult getDecryptionResult(Map<String, String> data, TransformCiphertext ciphertext) {
        if (StringUtils.hasText((String)data.get("error"))) {
            return new VaultTransformDecodeResult(new VaultException(data.get("error")));
        }
        if (StringUtils.hasText((String)data.get("decoded_value"))) {
            return new VaultTransformDecodeResult(TransformPlaintext.of(data.get("decoded_value")).with(ciphertext.getContext()));
        }
        return new VaultTransformDecodeResult(TransformPlaintext.empty().with(ciphertext.getContext()));
    }

    private static TransformCiphertext toCiphertext(Map<String, ?> data, VaultTransformContext context) {
        String ciphertext = (String)data.get("encoded_value");
        VaultTransformContext contextToUse = context;
        if (data.containsKey("tweak")) {
            byte[] tweak = Base64Utils.decodeFromString((String)((String)data.get("tweak")));
            contextToUse = VaultTransformContext.builder().transformation(context.getTransformation()).tweak(tweak).build();
        }
        return contextToUse.isEmpty() ? TransformCiphertext.of(ciphertext) : TransformCiphertext.of(ciphertext).with(contextToUse);
    }

    private static List<Map<String, String>> getBatchData(VaultResponse vaultResponse) {
        return (List)((Map)vaultResponse.getRequiredData()).get("batch_results");
    }
}

