/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.detect.CompositeDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.OverrideDetector;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.utils.ServiceLoaderUtils;

public class DefaultDetector
extends CompositeDetector {
    private static final long serialVersionUID = -8170114575326908027L;
    private final transient ServiceLoader loader;

    public DefaultDetector(MimeTypes types, ServiceLoader loader, Collection<Class<? extends Detector>> excludeDetectors) {
        super(types.getMediaTypeRegistry(), DefaultDetector.getDefaultDetectors(types, loader, excludeDetectors));
        this.loader = loader;
    }

    public DefaultDetector(MimeTypes types, ServiceLoader loader) {
        this(types, loader, (Collection<Class<? extends Detector>>)Collections.EMPTY_SET);
    }

    public DefaultDetector(MimeTypes types, ClassLoader loader) {
        this(types, new ServiceLoader(loader));
    }

    public DefaultDetector(ClassLoader loader) {
        this(MimeTypes.getDefaultMimeTypes(), loader);
    }

    public DefaultDetector(MimeTypes types) {
        this(types, new ServiceLoader());
    }

    public DefaultDetector() {
        this(MimeTypes.getDefaultMimeTypes());
    }

    private static List<Detector> getDefaultDetectors(MimeTypes types, ServiceLoader loader, Collection<Class<? extends Detector>> excludeDetectors) {
        List detectors = loader.loadStaticServiceProviders(Detector.class, excludeDetectors);
        ServiceLoaderUtils.sortLoadedClasses(detectors);
        int overrideIndex = -1;
        int i = 0;
        for (Detector detector : detectors) {
            if (detector instanceof OverrideDetector) {
                overrideIndex = i;
                break;
            }
            ++i;
        }
        if (overrideIndex > -1) {
            Detector detector = (Detector)detectors.remove(overrideIndex);
            detectors.add(0, detector);
        }
        detectors.add(types);
        return detectors;
    }

    @Override
    public List<Detector> getDetectors() {
        if (this.loader != null && this.loader.isDynamic()) {
            List<Detector> detectors = this.loader.loadDynamicServiceProviders(Detector.class);
            if (detectors.size() > 0) {
                detectors.addAll(super.getDetectors());
                return detectors;
            }
            return super.getDetectors();
        }
        return super.getDetectors();
    }
}

