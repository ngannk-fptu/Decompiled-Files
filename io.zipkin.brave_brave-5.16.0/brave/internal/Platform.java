/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.animal_sniffer.IgnoreJRERequirement
 */
package brave.internal;

import brave.Clock;
import brave.Tracer;
import brave.internal.Nullable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.time.Instant;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.jvnet.animal_sniffer.IgnoreJRERequirement;

public abstract class Platform {
    private static final Platform PLATFORM = Platform.findPlatform();
    volatile String linkLocalIp;

    @Nullable
    public abstract String getHostString(InetSocketAddress var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public String linkLocalIp() {
        if (this.linkLocalIp != null) {
            return this.linkLocalIp;
        }
        Platform platform = this;
        synchronized (platform) {
            if (this.linkLocalIp == null) {
                this.linkLocalIp = this.produceLinkLocalIp();
            }
        }
        return this.linkLocalIp;
    }

    String produceLinkLocalIp() {
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                Enumeration<InetAddress> addresses = nic.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isSiteLocalAddress()) continue;
                    return address.getHostAddress();
                }
            }
        }
        catch (Exception e) {
            this.log("error reading nics", e);
        }
        return null;
    }

    public AssertionError assertionError(String message, Throwable cause) {
        AssertionError error = new AssertionError((Object)message);
        ((Throwable)((Object)error)).initCause(cause);
        throw error;
    }

    public static Platform get() {
        return PLATFORM;
    }

    public void log(String msg, @Nullable Throwable thrown) {
        Logger logger = LoggerHolder.LOG;
        if (!logger.isLoggable(Level.FINE)) {
            return;
        }
        logger.log(Level.FINE, msg, thrown);
    }

    public void log(String msg, Object param1, @Nullable Throwable thrown) {
        Logger logger = LoggerHolder.LOG;
        if (!logger.isLoggable(Level.FINE)) {
            return;
        }
        LogRecord lr = new LogRecord(Level.FINE, msg);
        Object[] params = new Object[]{param1};
        lr.setParameters(params);
        if (thrown != null) {
            lr.setThrown(thrown);
        }
        logger.log(lr);
    }

    static Platform findPlatform() {
        try {
            Class<?> zoneId = Class.forName("java.time.ZoneId");
            Class.forName("java.time.Clock").getMethod("tickMillis", zoneId);
            return new Jre9();
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        try {
            Class.forName("java.util.concurrent.ThreadLocalRandom");
            return new Jre7();
        }
        catch (ClassNotFoundException classNotFoundException) {
            return new Jre6();
        }
    }

    public abstract long randomLong();

    public abstract long nextTraceIdHigh();

    public Clock clock() {
        return new Clock(){

            @Override
            public long currentTimeMicroseconds() {
                return System.currentTimeMillis() * 1000L;
            }

            public String toString() {
                return "System.currentTimeMillis()";
            }
        };
    }

    static long nextTraceIdHigh(int random) {
        long epochSeconds = System.currentTimeMillis() / 1000L;
        return (epochSeconds & 0xFFFFFFFFL) << 32 | (long)random & 0xFFFFFFFFL;
    }

    static class Jre6
    extends Platform {
        final Random prng = new Random(System.nanoTime());

        @Override
        public String getHostString(InetSocketAddress socket) {
            return socket.getAddress().getHostAddress();
        }

        @Override
        public long randomLong() {
            return this.prng.nextLong();
        }

        @Override
        public long nextTraceIdHigh() {
            return Jre6.nextTraceIdHigh(this.prng.nextInt());
        }

        Jre6() {
        }

        public String toString() {
            return "Jre6{}";
        }
    }

    static class Jre7
    extends Platform {
        Jre7() {
        }

        @Override
        @IgnoreJRERequirement
        public String getHostString(InetSocketAddress socket) {
            return socket.getHostString();
        }

        @Override
        @IgnoreJRERequirement
        public long randomLong() {
            return ThreadLocalRandom.current().nextLong();
        }

        @Override
        @IgnoreJRERequirement
        public long nextTraceIdHigh() {
            return Jre7.nextTraceIdHigh(ThreadLocalRandom.current().nextInt());
        }

        @Override
        @IgnoreJRERequirement
        public AssertionError assertionError(String message, Throwable cause) {
            return new AssertionError(message, cause);
        }

        public String toString() {
            return "Jre7{}";
        }
    }

    static class Jre9
    extends Jre7 {
        Jre9() {
        }

        @Override
        @IgnoreJRERequirement
        public Clock clock() {
            return new Clock(){

                @Override
                public long currentTimeMicroseconds() {
                    Instant instant = java.time.Clock.systemUTC().instant();
                    return instant.getEpochSecond() * 1000000L + (long)(instant.getNano() / 1000);
                }

                public String toString() {
                    return "Clock.systemUTC().instant()";
                }
            };
        }

        @Override
        public String toString() {
            return "Jre9{}";
        }
    }

    private static final class LoggerHolder {
        static final Logger LOG = Logger.getLogger(Tracer.class.getName());

        private LoggerHolder() {
        }
    }
}

