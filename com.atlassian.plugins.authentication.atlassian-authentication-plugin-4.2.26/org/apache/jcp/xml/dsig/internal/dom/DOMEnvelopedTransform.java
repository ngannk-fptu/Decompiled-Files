/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.apache.jcp.xml.dsig.internal.dom.ApacheTransform;

public final class DOMEnvelopedTransform
extends ApacheTransform {
    @Override
    public void init(TransformParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("params must be null");
        }
    }
}

