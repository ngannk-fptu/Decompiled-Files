/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.util.List;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.detect.CompositeDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.ProbabilisticMimeDetectionSelector;
import org.apache.tika.utils.ServiceLoaderUtils;

public class DefaultProbDetector
extends CompositeDetector {
    private static final long serialVersionUID = -8836240060532323352L;
    private final transient ServiceLoader loader;

    public DefaultProbDetector(ProbabilisticMimeDetectionSelector sel, ServiceLoader loader) {
        super(sel.getMediaTypeRegistry(), DefaultProbDetector.getDefaultDetectors(sel, loader));
        this.loader = loader;
    }

    public DefaultProbDetector(ProbabilisticMimeDetectionSelector sel, ClassLoader loader) {
        this(sel, new ServiceLoader(loader));
    }

    public DefaultProbDetector(ClassLoader loader) {
        this(new ProbabilisticMimeDetectionSelector(), loader);
    }

    public DefaultProbDetector(MimeTypes types) {
        this(new ProbabilisticMimeDetectionSelector(types), new ServiceLoader());
    }

    public DefaultProbDetector() {
        this(MimeTypes.getDefaultMimeTypes());
    }

    private static List<Detector> getDefaultDetectors(ProbabilisticMimeDetectionSelector sel, ServiceLoader loader) {
        List<Detector> detectors = loader.loadStaticServiceProviders(Detector.class);
        ServiceLoaderUtils.sortLoadedClasses(detectors);
        detectors.add(sel);
        return detectors;
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

