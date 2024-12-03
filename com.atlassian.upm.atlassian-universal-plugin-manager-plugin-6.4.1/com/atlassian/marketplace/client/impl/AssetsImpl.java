/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.ArtifactId;
import com.atlassian.marketplace.client.api.Assets;
import com.atlassian.marketplace.client.api.ImageId;
import com.atlassian.marketplace.client.api.ImagePurpose;
import com.atlassian.marketplace.client.api.UriTemplate;
import com.atlassian.marketplace.client.impl.ApiHelper;
import com.atlassian.marketplace.client.impl.ApiImplBase;
import com.atlassian.marketplace.client.impl.InternalModel;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.util.UriBuilder;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

final class AssetsImpl
extends ApiImplBase
implements Assets {
    AssetsImpl(ApiHelper apiHelper, InternalModel.MinimalLinks root) throws MpacException {
        super(apiHelper, root, "assets");
    }

    @Override
    public ArtifactId uploadAddonArtifact(File artifactFile) throws MpacException {
        URI uri = this.apiHelper.requireLinkUri(this.getLinksOnly(this.getApiRoot()), "artifact", Assets.class);
        return ArtifactId.fromUri(this.uploadAssetFromFile(uri, artifactFile, Optional.empty()));
    }

    @Override
    public ArtifactId uploadAddonArtifact(File artifactFile, String pluginKey) throws MpacException {
        URI uri = this.apiHelper.requireLinkUri(this.getLinksOnly(this.getApiRoot()), "artifact", Assets.class);
        return ArtifactId.fromUri(this.uploadAssetFromFile(uri, artifactFile, Optional.of(pluginKey)));
    }

    @Override
    public ImageId uploadImage(File imageFile, ImagePurpose imageType) throws MpacException {
        UriTemplate ut = ApiHelper.requireLinkUriTemplate(this.getLinksOnly(this.getApiRoot()), "imageByType", Assets.class);
        URI uri = this.apiHelper.resolveLink(ut.resolve((Map<String, String>)ImmutableMap.of((Object)"imageType", (Object)imageType.getKey())));
        return ImageId.fromUri(this.uploadAssetFromFile(uri, imageFile, Optional.empty()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private URI uploadAssetFromFile(URI collectionUri, File file, Optional<String> pluginKey) throws MpacException {
        URI uRI;
        FileInputStream fis = new FileInputStream(file);
        try {
            uRI = this.uploadAssetFromStream(collectionUri, fis, file.length(), file.getName(), pluginKey);
        }
        catch (Throwable throwable) {
            try {
                fis.close();
                throw throwable;
            }
            catch (IOException e) {
                throw new MpacException(e);
            }
        }
        fis.close();
        return uRI;
    }

    private URI uploadAssetFromStream(URI collectionUri, InputStream stream, long length, String logicalFileName, Optional<String> pluginKey) throws MpacException {
        UriBuilder uriBuilder = UriBuilder.fromUri(collectionUri).queryParam("file", logicalFileName);
        pluginKey.map(key -> uriBuilder.queryParam("pluginKey", key));
        URI uri = uriBuilder.build();
        InternalModel.MinimalLinks result = this.apiHelper.postContent(uri, stream, length, "application/binary", InternalModel.MinimalLinks.class);
        return ApiHelper.requireLink(result.getLinks(), "self", Links.class).getUri();
    }
}

