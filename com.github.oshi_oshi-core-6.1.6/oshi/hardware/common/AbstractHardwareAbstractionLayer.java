/*
 * Decompiled with CFR 0.152.
 */
package oshi.hardware.common;

import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.Sensors;
import oshi.util.Memoizer;

@ThreadSafe
public abstract class AbstractHardwareAbstractionLayer
implements HardwareAbstractionLayer {
    private final Supplier<ComputerSystem> computerSystem = Memoizer.memoize(this::createComputerSystem);
    private final Supplier<CentralProcessor> processor = Memoizer.memoize(this::createProcessor);
    private final Supplier<GlobalMemory> memory = Memoizer.memoize(this::createMemory);
    private final Supplier<Sensors> sensors = Memoizer.memoize(this::createSensors);

    @Override
    public ComputerSystem getComputerSystem() {
        return this.computerSystem.get();
    }

    protected abstract ComputerSystem createComputerSystem();

    @Override
    public CentralProcessor getProcessor() {
        return this.processor.get();
    }

    protected abstract CentralProcessor createProcessor();

    @Override
    public GlobalMemory getMemory() {
        return this.memory.get();
    }

    protected abstract GlobalMemory createMemory();

    @Override
    public Sensors getSensors() {
        return this.sensors.get();
    }

    protected abstract Sensors createSensors();

    @Override
    public List<NetworkIF> getNetworkIFs() {
        return this.getNetworkIFs(false);
    }
}

