/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.shindig.common.crypto.BlobCrypter
 *  org.apache.shindig.gadgets.servlet.OAuthCallbackServlet
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.gadgets.renderer.internal.servlet;

import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.gadgets.servlet.OAuthCallbackServlet;
import org.springframework.beans.factory.annotation.Qualifier;

public class AtlassianOAuthCallbackServlet
extends OAuthCallbackServlet {
    public AtlassianOAuthCallbackServlet(@Qualifier(value="blobCrypter") BlobCrypter stateCrypter) {
        super.setStateCrypter(stateCrypter);
    }
}

