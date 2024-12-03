/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.metadata.Metadata;

public class CompositeEncodingDetector
implements EncodingDetector,
Serializable {
    private static final long serialVersionUID = 5980683158436430252L;
    private final List<EncodingDetector> detectors = new LinkedList<EncodingDetector>();

    public CompositeEncodingDetector(List<EncodingDetector> detectors, Collection<Class<? extends EncodingDetector>> excludeEncodingDetectors) {
        for (EncodingDetector encodingDetector : detectors) {
            if (this.isExcluded(excludeEncodingDetectors, encodingDetector.getClass())) continue;
            this.detectors.add(encodingDetector);
        }
    }

    public CompositeEncodingDetector(List<EncodingDetector> detectors) {
        for (EncodingDetector encodingDetector : detectors) {
            this.detectors.add(encodingDetector);
        }
    }

    @Override
    public Charset detect(InputStream input, Metadata metadata) throws IOException {
        for (EncodingDetector detector : this.getDetectors()) {
            Charset detected = detector.detect(input, metadata);
            if (detected == null) continue;
            return detected;
        }
        return null;
    }

    public List<EncodingDetector> getDetectors() {
        return Collections.unmodifiableList(this.detectors);
    }

    private boolean isExcluded(Collection<Class<? extends EncodingDetector>> excludeEncodingDetectors, Class<? extends EncodingDetector> encodingDetector) {
        return excludeEncodingDetectors.contains(encodingDetector) || this.assignableFrom(excludeEncodingDetectors, encodingDetector);
    }

    private boolean assignableFrom(Collection<Class<? extends EncodingDetector>> excludeEncodingDetectors, Class<? extends EncodingDetector> encodingDetector) {
        for (Class<? extends EncodingDetector> e : excludeEncodingDetectors) {
            if (!e.isAssignableFrom(encodingDetector)) continue;
            return true;
        }
        return false;
    }
}

