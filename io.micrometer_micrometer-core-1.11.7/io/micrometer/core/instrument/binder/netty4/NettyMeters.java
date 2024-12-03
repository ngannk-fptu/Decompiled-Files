/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.docs.KeyName
 */
package io.micrometer.core.instrument.binder.netty4;

import io.micrometer.common.docs.KeyName;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.docs.MeterDocumentation;

public enum NettyMeters implements MeterDocumentation
{
    ALLOCATOR_MEMORY_USED{

        @Override
        public String getName() {
            return "netty.allocator.memory.used";
        }

        @Override
        public Meter.Type getType() {
            return Meter.Type.GAUGE;
        }

        @Override
        public String getBaseUnit() {
            return "bytes";
        }

        @Override
        public KeyName[] getKeyNames() {
            return KeyName.merge((KeyName[][])new KeyName[][]{AllocatorKeyNames.values(), AllocatorMemoryKeyNames.values()});
        }
    }
    ,
    ALLOCATOR_MEMORY_PINNED{

        @Override
        public String getName() {
            return "netty.allocator.memory.pinned";
        }

        @Override
        public Meter.Type getType() {
            return Meter.Type.GAUGE;
        }

        @Override
        public String getBaseUnit() {
            return "bytes";
        }

        @Override
        public KeyName[] getKeyNames() {
            return KeyName.merge((KeyName[][])new KeyName[][]{AllocatorKeyNames.values(), AllocatorMemoryKeyNames.values()});
        }
    }
    ,
    ALLOCATOR_POOLED_ARENAS{

        @Override
        public String getName() {
            return "netty.allocator.pooled.arenas";
        }

        @Override
        public Meter.Type getType() {
            return Meter.Type.GAUGE;
        }

        @Override
        public KeyName[] getKeyNames() {
            return KeyName.merge((KeyName[][])new KeyName[][]{AllocatorKeyNames.values(), AllocatorMemoryKeyNames.values()});
        }
    }
    ,
    ALLOCATOR_POOLED_CACHE_SIZE{

        @Override
        public String getName() {
            return "netty.allocator.pooled.cache.size";
        }

        @Override
        public Meter.Type getType() {
            return Meter.Type.GAUGE;
        }

        @Override
        public String getBaseUnit() {
            return "bytes";
        }

        @Override
        public KeyName[] getKeyNames() {
            return KeyName.merge((KeyName[][])new KeyName[][]{AllocatorKeyNames.values(), AllocatorPooledCacheKeyNames.values()});
        }
    }
    ,
    ALLOCATOR_POOLED_THREADLOCAL_CACHES{

        @Override
        public String getName() {
            return "netty.allocator.pooled.threadlocal.caches";
        }

        @Override
        public Meter.Type getType() {
            return Meter.Type.GAUGE;
        }

        @Override
        public KeyName[] getKeyNames() {
            return AllocatorKeyNames.values();
        }
    }
    ,
    ALLOCATOR_POOLED_CHUNK_SIZE{

        @Override
        public String getName() {
            return "netty.allocator.pooled.chunk.size";
        }

        @Override
        public Meter.Type getType() {
            return Meter.Type.GAUGE;
        }

        @Override
        public String getBaseUnit() {
            return "bytes";
        }

        @Override
        public KeyName[] getKeyNames() {
            return AllocatorKeyNames.values();
        }
    }
    ,
    EVENT_EXECUTOR_TASKS_PENDING{

        @Override
        public String getName() {
            return "netty.eventexecutor.tasks.pending";
        }

        @Override
        public Meter.Type getType() {
            return Meter.Type.GAUGE;
        }

        @Override
        public KeyName[] getKeyNames() {
            return EventExecutorTasksPendingKeyNames.values();
        }
    };


    static enum EventExecutorTasksPendingKeyNames implements KeyName
    {
        NAME{

            public String asString() {
                return "name";
            }
        };

    }

    static enum AllocatorPooledCacheKeyNames implements KeyName
    {
        CACHE_TYPE{

            public String asString() {
                return "cache.type";
            }
        };

    }

    static enum AllocatorMemoryKeyNames implements KeyName
    {
        MEMORY_TYPE{

            public String asString() {
                return "memory.type";
            }
        };

    }

    static enum AllocatorKeyNames implements KeyName
    {
        ID{

            public String asString() {
                return "id";
            }
        }
        ,
        ALLOCATOR_TYPE{

            public String asString() {
                return "allocator.type";
            }
        };

    }
}

