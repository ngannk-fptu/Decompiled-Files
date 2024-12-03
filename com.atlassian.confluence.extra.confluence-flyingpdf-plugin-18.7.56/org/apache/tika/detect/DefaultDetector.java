/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.util.Collection;
import java.util.List;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.detect.CompositeDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.utils.ServiceLoaderUtils;

public class DefaultDetector
extends CompositeDetector {
    private static final long serialVersionUID = -8170114575326908027L;
    private final transient ServiceLoader loader;

    private static List<Detector> getDefaultDetectors(MimeTypes types, ServiceLoader loader) {
        List<Detector> detectors = loader.loadStaticServiceProviders(Detector.class);
        ServiceLoaderUtils.sortLoadedClasses(detectors);
        detectors.add(types);
        return detectors;
    }

    public DefaultDetector(MimeTypes types, ServiceLoader loader, Collection<Class<? extends Detector>> excludeDetectors) {
        super(types.getMediaTypeRegistry(), DefaultDetector.getDefaultDetectors(types, loader), excludeDetectors);
        this.loader = loader;
    }

    public DefaultDetector(MimeTypes types, ServiceLoader loader) {
        this(types, loader, null);
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

    @Override
    public List<Detector> getDetectors() {
        if (this.loader != null) {
            List<Detector> detectors = this.loader.loadDynamicServiceProviders(Detector.class);
            detectors.addAll(super.getDetectors());
            return detectors;
        }
        return super.getDetectors();
    }
}

