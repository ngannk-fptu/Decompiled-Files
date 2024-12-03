/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import javax.activation.DataHandler;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;

public interface OptimizationPolicy {
    public static final OptimizationPolicy DEFAULT = new OptimizationPolicy(){

        public boolean isOptimized(DataHandler dataHandler, boolean optimize) {
            return optimize;
        }

        public boolean isOptimized(DataHandlerProvider dataHandlerProvider, boolean optimize) {
            return optimize;
        }
    };
    public static final OptimizationPolicy ALL = new OptimizationPolicy(){

        public boolean isOptimized(DataHandler dataHandler, boolean optimize) {
            return true;
        }

        public boolean isOptimized(DataHandlerProvider dataHandlerProvider, boolean optimize) {
            return true;
        }
    };

    public boolean isOptimized(DataHandler var1, boolean var2) throws IOException;

    public boolean isOptimized(DataHandlerProvider var1, boolean var2) throws IOException;
}

