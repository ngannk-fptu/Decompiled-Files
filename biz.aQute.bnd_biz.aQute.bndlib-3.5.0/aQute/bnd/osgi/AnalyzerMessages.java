/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Descriptors;
import aQute.service.reporter.Messages;
import java.util.Collection;

public interface AnalyzerMessages
extends Messages {
    public Messages.WARNING Export_Has_PrivateReferences_(Descriptors.PackageRef var1, int var2, Collection<Descriptors.PackageRef> var3);
}

