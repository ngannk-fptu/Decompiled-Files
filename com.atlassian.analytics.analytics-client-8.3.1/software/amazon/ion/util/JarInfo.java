/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import software.amazon.ion.IonException;
import software.amazon.ion.Timestamp;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class JarInfo {
    private static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";
    private static final String BUILD_TIME_ATTRIBUTE = "Ion-Java-Build-Time";
    private static final String PROJECT_VERSION_ATTRIBUTE = "Ion-Java-Project-Version";
    private String ourProjectVersion;
    private Timestamp ourBuildTime;

    public JarInfo() throws IonException {
        Enumeration<URL> manifestUrls;
        try {
            manifestUrls = this.getClass().getClassLoader().getResources(MANIFEST_FILE);
        }
        catch (IOException e) {
            throw new IonException("Unable to load manifests.", e);
        }
        ArrayList<Manifest> manifests = new ArrayList<Manifest>();
        while (manifestUrls.hasMoreElements()) {
            try {
                manifests.add(new Manifest(manifestUrls.nextElement().openStream()));
            }
            catch (IOException e) {}
        }
        this.loadBuildProperties(manifests);
    }

    JarInfo(List<Manifest> manifests) {
        this.loadBuildProperties(manifests);
    }

    public String getProjectVersion() {
        return this.ourProjectVersion;
    }

    public Timestamp getBuildTime() {
        return this.ourBuildTime;
    }

    private void loadBuildProperties(List<Manifest> manifests) throws IonException {
        boolean propertiesLoaded = false;
        for (Manifest manifest : manifests) {
            boolean success = this.tryLoadBuildProperties(manifest);
            if (success && propertiesLoaded) {
                throw new IonException("Found multiple manifests with ion-java version info on the classpath.");
            }
            propertiesLoaded |= success;
        }
        if (!propertiesLoaded) {
            throw new IonException("Unable to locate manifest with ion-java version info on the classpath.");
        }
    }

    private boolean tryLoadBuildProperties(Manifest manifest) {
        Attributes mainAttributes = manifest.getMainAttributes();
        String projectVersion = mainAttributes.getValue(PROJECT_VERSION_ATTRIBUTE);
        String time = mainAttributes.getValue(BUILD_TIME_ATTRIBUTE);
        if (projectVersion == null || time == null) {
            return false;
        }
        this.ourProjectVersion = projectVersion;
        try {
            this.ourBuildTime = Timestamp.valueOf(time);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return true;
    }
}

