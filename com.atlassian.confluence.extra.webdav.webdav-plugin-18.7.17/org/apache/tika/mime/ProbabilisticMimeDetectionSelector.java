/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

public class ProbabilisticMimeDetectionSelector
implements Detector {
    private static final long serialVersionUID = 224589862960269260L;
    private static final float DEFAULT_MAGIC_TRUST = 0.9f;
    private static final float DEFAULT_META_TRUST = 0.8f;
    private static final float DEFAULT_EXTENSION_TRUST = 0.8f;
    private final MimeTypes mimeTypes;
    private final MediaType rootMediaType;
    private final float changeRate;
    private float priorMagicFileType;
    private float priorExtensionFileType;
    private float priorMetaFileType;
    private float magic_trust;
    private float extension_trust;
    private float meta_trust;
    private float magic_neg;
    private float extension_neg;
    private float meta_neg;
    private float threshold;

    public ProbabilisticMimeDetectionSelector() {
        this(MimeTypes.getDefaultMimeTypes(), null);
    }

    public ProbabilisticMimeDetectionSelector(Builder builder) {
        this(MimeTypes.getDefaultMimeTypes(), builder);
    }

    public ProbabilisticMimeDetectionSelector(MimeTypes mimeTypes) {
        this(mimeTypes, null);
    }

    public ProbabilisticMimeDetectionSelector(MimeTypes mimeTypes, Builder builder) {
        this.mimeTypes = mimeTypes;
        this.rootMediaType = MediaType.OCTET_STREAM;
        this.initializeDefaultProbabilityParameters();
        this.changeRate = 0.1f;
        if (builder != null) {
            this.priorMagicFileType = builder.priorMagicFileType == 0.0f ? this.priorMagicFileType : builder.priorMagicFileType;
            this.priorExtensionFileType = builder.priorExtensionFileType == 0.0f ? this.priorExtensionFileType : builder.priorExtensionFileType;
            this.priorMetaFileType = builder.priorMetaFileType == 0.0f ? this.priorMetaFileType : builder.priorMetaFileType;
            this.magic_trust = builder.magic_trust == 0.0f ? this.magic_trust : builder.extension_neg;
            this.extension_trust = builder.extension_trust == 0.0f ? this.extension_trust : builder.extension_trust;
            this.meta_trust = builder.meta_trust == 0.0f ? this.meta_trust : builder.meta_trust;
            this.magic_neg = builder.magic_neg == 0.0f ? this.magic_neg : builder.magic_neg;
            this.extension_neg = builder.extension_neg == 0.0f ? this.extension_neg : builder.extension_neg;
            this.meta_neg = builder.meta_neg == 0.0f ? this.meta_neg : builder.meta_neg;
            this.threshold = builder.threshold == 0.0f ? this.threshold : builder.threshold;
        }
    }

    private void initializeDefaultProbabilityParameters() {
        this.priorMagicFileType = 0.5f;
        this.priorExtensionFileType = 0.5f;
        this.priorMetaFileType = 0.5f;
        this.magic_trust = 0.9f;
        this.extension_trust = 0.8f;
        this.meta_trust = 0.8f;
        this.magic_neg = 0.100000024f;
        this.extension_neg = 0.19999999f;
        this.meta_neg = 0.19999999f;
        this.threshold = 0.5001f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MediaType detect(InputStream input, Metadata metadata) throws IOException {
        ArrayList<MimeType> possibleTypes = new ArrayList<MimeType>();
        if (input != null) {
            input.mark(this.mimeTypes.getMinLength());
            try {
                byte[] prefix = this.mimeTypes.readMagicHeader(input);
                possibleTypes.addAll(this.mimeTypes.getMimeType(prefix));
            }
            finally {
                input.reset();
            }
        }
        MimeType extHint = null;
        String resourceName = metadata.get("resourceName");
        if (resourceName != null) {
            String name = null;
            try {
                int slash;
                URI uri = new URI(resourceName);
                String path = uri.getPath();
                if (path != null && (slash = path.lastIndexOf(47)) + 1 < path.length()) {
                    name = path.substring(slash + 1);
                }
            }
            catch (URISyntaxException e) {
                name = resourceName;
            }
            if (name != null) {
                extHint = this.mimeTypes.getMimeType(name);
            }
        }
        MimeType metaHint = null;
        String typeName = metadata.get("Content-Type");
        if (typeName != null) {
            try {
                metaHint = this.mimeTypes.forName(typeName);
            }
            catch (MimeTypeException mimeTypeException) {
                // empty catch block
            }
        }
        return this.applyProbilities(possibleTypes, extHint, metaHint);
    }

    private MediaType applyProbilities(List<MimeType> possibleTypes, MimeType extMimeType, MimeType metadataMimeType) {
        MediaType extensionMediaType_ = extMimeType == null ? null : extMimeType.getType();
        MediaType metaMediaType_ = metadataMimeType == null ? null : metadataMimeType.getType();
        int n = possibleTypes.size();
        float mag_trust = this.magic_trust;
        float mag_neg = this.magic_neg;
        float ext_trust = this.extension_trust;
        float ext_neg = this.extension_neg;
        float met_trust = this.meta_trust;
        float met_neg = this.meta_neg;
        if (extensionMediaType_ == null || extensionMediaType_.compareTo(this.rootMediaType) == 0) {
            ext_trust = 1.0f;
            ext_neg = 1.0f;
        }
        if (metaMediaType_ == null || metaMediaType_.compareTo(this.rootMediaType) == 0) {
            met_trust = 1.0f;
            met_neg = 1.0f;
        }
        float maxProb = -1.0f;
        MediaType bestEstimate = this.rootMediaType;
        if (!possibleTypes.isEmpty()) {
            for (int i = 0; i < n; ++i) {
                int j;
                MediaType magictype = possibleTypes.get(i).getType();
                MediaTypeRegistry registry = this.mimeTypes.getMediaTypeRegistry();
                if (magictype != null && magictype.equals(this.rootMediaType)) {
                    mag_trust = 1.0f;
                    mag_neg = 1.0f;
                } else {
                    if (extensionMediaType_ != null) {
                        if (extensionMediaType_.equals(magictype) || registry.isSpecializationOf(extensionMediaType_, magictype)) {
                            possibleTypes.set(i, extMimeType);
                        } else if (registry.isSpecializationOf(magictype, extensionMediaType_)) {
                            extensionMediaType_ = magictype;
                        }
                    }
                    if (metaMediaType_ != null) {
                        if (metaMediaType_.equals(magictype) || registry.isSpecializationOf(metaMediaType_, magictype)) {
                            possibleTypes.set(i, metadataMimeType);
                        } else if (registry.isSpecializationOf(magictype, metaMediaType_)) {
                            metaMediaType_ = magictype;
                        }
                    }
                }
                float[] results = new float[3];
                float[] trust1 = new float[3];
                float[] negtrust1 = new float[3];
                magictype = possibleTypes.get(i).getType();
                if (i > 0) {
                    mag_trust *= 1.0f - this.changeRate;
                    mag_neg *= 1.0f + this.changeRate;
                }
                if (magictype != null && mag_trust != 1.0f) {
                    trust1[0] = mag_trust;
                    negtrust1[0] = mag_neg;
                    if (metaMediaType_ != null && met_trust != 1.0f) {
                        if (magictype.equals(metaMediaType_)) {
                            trust1[1] = met_trust;
                            negtrust1[1] = met_neg;
                        } else {
                            trust1[1] = 1.0f - met_trust;
                            negtrust1[1] = 1.0f - met_neg;
                        }
                    } else {
                        trust1[1] = 1.0f;
                        negtrust1[1] = 1.0f;
                    }
                    if (extensionMediaType_ != null && ext_trust != 1.0f) {
                        if (magictype.equals(extensionMediaType_)) {
                            trust1[2] = ext_trust;
                            negtrust1[2] = ext_neg;
                        } else {
                            trust1[2] = 1.0f - ext_trust;
                            negtrust1[2] = 1.0f - ext_neg;
                        }
                    } else {
                        trust1[2] = 1.0f;
                        negtrust1[2] = 1.0f;
                    }
                } else {
                    results[0] = 0.1f;
                }
                float[] trust2 = new float[3];
                float[] negtrust2 = new float[3];
                if (metadataMimeType != null && met_trust != 1.0f) {
                    trust2[1] = met_trust;
                    negtrust2[1] = met_neg;
                    if (magictype != null && mag_trust != 1.0f) {
                        if (metaMediaType_.equals(magictype)) {
                            trust2[0] = mag_trust;
                            negtrust2[0] = mag_neg;
                        } else {
                            trust2[0] = 1.0f - mag_trust;
                            negtrust2[0] = 1.0f - mag_neg;
                        }
                    } else {
                        trust2[0] = 1.0f;
                        negtrust2[0] = 1.0f;
                    }
                    if (extensionMediaType_ != null && ext_trust != 1.0f) {
                        if (metaMediaType_.equals(extensionMediaType_)) {
                            trust2[2] = ext_trust;
                            negtrust2[2] = ext_neg;
                        } else {
                            trust2[2] = 1.0f - ext_trust;
                            negtrust2[2] = 1.0f - ext_neg;
                        }
                    } else {
                        trust2[2] = 1.0f;
                        negtrust2[2] = 1.0f;
                    }
                } else {
                    results[1] = 0.1f;
                }
                float[] trust3 = new float[3];
                float[] negtrust3 = new float[3];
                if (extensionMediaType_ != null && ext_trust != 1.0f) {
                    trust3[2] = ext_trust;
                    negtrust3[2] = ext_neg;
                    if (magictype != null && mag_trust != 1.0f) {
                        if (magictype.equals(extensionMediaType_)) {
                            trust3[0] = mag_trust;
                            negtrust3[0] = mag_neg;
                        } else {
                            trust3[0] = 1.0f - mag_trust;
                            negtrust3[0] = 1.0f - mag_neg;
                        }
                    } else {
                        trust3[0] = 1.0f;
                        negtrust3[0] = 1.0f;
                    }
                    if (metaMediaType_ != null && met_trust != 1.0f) {
                        if (metaMediaType_.equals(extensionMediaType_)) {
                            trust3[1] = met_trust;
                            negtrust3[1] = met_neg;
                        } else {
                            trust3[1] = 1.0f - met_trust;
                            negtrust3[1] = 1.0f - met_neg;
                        }
                    } else {
                        trust3[1] = 1.0f;
                        negtrust3[1] = 1.0f;
                    }
                } else {
                    results[2] = 0.1f;
                }
                float pPrime = this.priorMagicFileType;
                float deno = 1.0f - this.priorMagicFileType;
                if (results[0] == 0.0f) {
                    for (j = 0; j < trust1.length; ++j) {
                        pPrime *= trust1[j];
                        if (trust1[j] == 1.0f) continue;
                        deno *= negtrust1[j];
                    }
                    pPrime /= pPrime + deno;
                    results[0] = pPrime;
                }
                if (maxProb < results[0]) {
                    maxProb = results[0];
                    bestEstimate = magictype;
                }
                pPrime = this.priorMetaFileType;
                deno = 1.0f - this.priorMetaFileType;
                if (results[1] == 0.0f) {
                    for (j = 0; j < trust2.length; ++j) {
                        pPrime *= trust2[j];
                        if (trust2[j] == 1.0f) continue;
                        deno *= negtrust2[j];
                    }
                    pPrime /= pPrime + deno;
                    results[1] = pPrime;
                }
                if (maxProb < results[1]) {
                    maxProb = results[1];
                    bestEstimate = metaMediaType_;
                }
                pPrime = this.priorExtensionFileType;
                deno = 1.0f - this.priorExtensionFileType;
                if (results[2] == 0.0f) {
                    for (j = 0; j < trust3.length; ++j) {
                        pPrime *= trust3[j];
                        if (trust3[j] == 1.0f) continue;
                        deno *= negtrust3[j];
                    }
                    pPrime /= pPrime + deno;
                    results[2] = pPrime;
                }
                if (!(maxProb < results[2])) continue;
                maxProb = results[2];
                bestEstimate = extensionMediaType_;
            }
        }
        return maxProb < this.threshold ? this.rootMediaType : bestEstimate;
    }

    public MediaTypeRegistry getMediaTypeRegistry() {
        return this.mimeTypes.getMediaTypeRegistry();
    }

    public static class Builder {
        private float priorMagicFileType;
        private float priorExtensionFileType;
        private float priorMetaFileType;
        private float magic_trust;
        private float extension_trust;
        private float meta_trust;
        private float magic_neg;
        private float extension_neg;
        private float meta_neg;
        private float threshold;

        public synchronized Builder priorMagicFileType(float prior) {
            this.priorMagicFileType = prior;
            return this;
        }

        public synchronized Builder priorExtensionFileType(float prior) {
            this.priorExtensionFileType = prior;
            return this;
        }

        public synchronized Builder priorMetaFileType(float prior) {
            this.priorMetaFileType = prior;
            return this;
        }

        public synchronized Builder magic_trust(float trust) {
            this.magic_trust = trust;
            return this;
        }

        public synchronized Builder extension_trust(float trust) {
            this.extension_trust = trust;
            return this;
        }

        public synchronized Builder meta_trust(float trust) {
            this.meta_trust = trust;
            return this;
        }

        public synchronized Builder magic_neg(float trust) {
            this.magic_neg = trust;
            return this;
        }

        public synchronized Builder extension_neg(float trust) {
            this.extension_neg = trust;
            return this;
        }

        public synchronized Builder meta_neg(float trust) {
            this.meta_neg = trust;
            return this;
        }

        public synchronized Builder threshold(float threshold) {
            this.threshold = threshold;
            return this;
        }

        public ProbabilisticMimeDetectionSelector build2() {
            return new ProbabilisticMimeDetectionSelector(this);
        }
    }
}

