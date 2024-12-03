/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching.blossom.v5;

public class BlossomVOptions {
    public static final BlossomVOptions[] ALL_OPTIONS = new BlossomVOptions[]{new BlossomVOptions(InitializationType.NONE, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, true, true), new BlossomVOptions(InitializationType.NONE, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, true, false), new BlossomVOptions(InitializationType.NONE, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, false, true), new BlossomVOptions(InitializationType.NONE, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, false, false), new BlossomVOptions(InitializationType.NONE, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, true, true), new BlossomVOptions(InitializationType.NONE, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, true, false), new BlossomVOptions(InitializationType.NONE, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, false, true), new BlossomVOptions(InitializationType.NONE, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, false, false), new BlossomVOptions(InitializationType.GREEDY, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, true, true), new BlossomVOptions(InitializationType.GREEDY, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, true, false), new BlossomVOptions(InitializationType.GREEDY, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, false, true), new BlossomVOptions(InitializationType.GREEDY, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, false, false), new BlossomVOptions(InitializationType.GREEDY, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, true, true), new BlossomVOptions(InitializationType.GREEDY, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, true, false), new BlossomVOptions(InitializationType.GREEDY, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, false, true), new BlossomVOptions(InitializationType.GREEDY, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, false, true), new BlossomVOptions(InitializationType.FRACTIONAL, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, true, true), new BlossomVOptions(InitializationType.FRACTIONAL, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, true, false), new BlossomVOptions(InitializationType.FRACTIONAL, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, false, true), new BlossomVOptions(InitializationType.FRACTIONAL, DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS, false, false), new BlossomVOptions(InitializationType.FRACTIONAL, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, true, true), new BlossomVOptions(InitializationType.FRACTIONAL, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, true, false), new BlossomVOptions(InitializationType.FRACTIONAL, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, false, true), new BlossomVOptions(InitializationType.FRACTIONAL, DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA, false, true)};
    private static final InitializationType DEFAULT_INITIALIZATION_TYPE = InitializationType.FRACTIONAL;
    private static final DualUpdateStrategy DEFAULT_DUAL_UPDATE_TYPE = DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA;
    private static final boolean DEFAULT_UPDATE_DUALS_BEFORE = true;
    private static final boolean DEFAULT_UPDATE_DUALS_AFTER = false;
    DualUpdateStrategy dualUpdateStrategy;
    InitializationType initializationType;
    boolean updateDualsBefore;
    boolean updateDualsAfter;

    public BlossomVOptions(InitializationType initializationType, DualUpdateStrategy dualUpdateStrategy, boolean updateDualsBefore, boolean updateDualsAfter) {
        this.dualUpdateStrategy = dualUpdateStrategy;
        this.initializationType = initializationType;
        this.updateDualsBefore = updateDualsBefore;
        this.updateDualsAfter = updateDualsAfter;
    }

    public BlossomVOptions(InitializationType initializationType) {
        this(initializationType, DEFAULT_DUAL_UPDATE_TYPE, true, false);
    }

    public BlossomVOptions() {
        this(DEFAULT_INITIALIZATION_TYPE, DEFAULT_DUAL_UPDATE_TYPE, true, false);
    }

    public String toString() {
        return "BlossomVOptions{initializationType=" + this.initializationType + ", dualUpdateStrategy=" + this.dualUpdateStrategy + ", updateDualsBefore=" + this.updateDualsBefore + ", updateDualsAfter=" + this.updateDualsAfter + "}";
    }

    public boolean isUpdateDualsBefore() {
        return this.updateDualsBefore;
    }

    public boolean isUpdateDualsAfter() {
        return this.updateDualsAfter;
    }

    public DualUpdateStrategy getDualUpdateStrategy() {
        return this.dualUpdateStrategy;
    }

    public InitializationType getInitializationType() {
        return this.initializationType;
    }

    public static enum DualUpdateStrategy {
        MULTIPLE_TREE_FIXED_DELTA{

            @Override
            public String toString() {
                return "Multiple tree fixed delta";
            }
        }
        ,
        MULTIPLE_TREE_CONNECTED_COMPONENTS{

            @Override
            public String toString() {
                return "Multiple tree connected components";
            }
        };


        public abstract String toString();
    }

    public static enum InitializationType {
        GREEDY{

            @Override
            public String toString() {
                return "Greedy initialization";
            }
        }
        ,
        NONE{

            @Override
            public String toString() {
                return "None";
            }
        }
        ,
        FRACTIONAL{

            @Override
            public String toString() {
                return "Fractional matching initializations";
            }
        };


        public abstract String toString();
    }
}

