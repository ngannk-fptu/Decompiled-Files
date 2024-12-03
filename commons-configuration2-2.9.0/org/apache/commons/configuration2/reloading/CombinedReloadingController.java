/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.reloading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingDetector;

public class CombinedReloadingController
extends ReloadingController {
    private static final ReloadingDetector DUMMY = new MultiReloadingControllerDetector(null);
    private final Collection<ReloadingController> controllers;
    private final ReloadingDetector detector;

    public CombinedReloadingController(Collection<? extends ReloadingController> subCtrls) {
        super(DUMMY);
        this.controllers = CombinedReloadingController.checkManagedControllers(subCtrls);
        this.detector = new MultiReloadingControllerDetector(this);
    }

    public Collection<ReloadingController> getSubControllers() {
        return this.controllers;
    }

    @Override
    public ReloadingDetector getDetector() {
        return this.detector;
    }

    public void resetInitialReloadingState() {
        this.getDetector().reloadingPerformed();
    }

    private static Collection<ReloadingController> checkManagedControllers(Collection<? extends ReloadingController> subCtrls) {
        if (subCtrls == null) {
            throw new IllegalArgumentException("Collection with sub controllers must not be null!");
        }
        ArrayList<? extends ReloadingController> ctrls = new ArrayList<ReloadingController>(subCtrls);
        if (ctrls.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Collection with sub controllers contains a null entry!");
        }
        return Collections.unmodifiableCollection(ctrls);
    }

    private static class MultiReloadingControllerDetector
    implements ReloadingDetector {
        private final CombinedReloadingController owner;

        public MultiReloadingControllerDetector(CombinedReloadingController owner) {
            this.owner = owner;
        }

        @Override
        public boolean isReloadingRequired() {
            return this.owner.getSubControllers().stream().reduce(false, (b, rc) -> b | rc.checkForReloading(null), (t, u) -> t | u);
        }

        @Override
        public void reloadingPerformed() {
            this.owner.getSubControllers().forEach(ReloadingController::resetReloadingState);
        }
    }
}

