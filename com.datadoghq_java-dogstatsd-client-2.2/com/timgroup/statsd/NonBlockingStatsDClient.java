/*
 * Decompiled with CFR 0.152.
 */
package com.timgroup.statsd;

import com.timgroup.statsd.Event;
import com.timgroup.statsd.ServiceCheck;
import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.StatsDClientErrorHandler;
import com.timgroup.statsd.StatsDClientException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class NonBlockingStatsDClient
implements StatsDClient {
    private static final int PACKET_SIZE_BYTES = 1400;
    private static final StatsDClientErrorHandler NO_OP_HANDLER = new StatsDClientErrorHandler(){

        @Override
        public void handle(Exception e) {
        }
    };
    private static final ThreadLocal<NumberFormat> NUMBER_FORMATTERS = new ThreadLocal<NumberFormat>(){

        @Override
        protected NumberFormat initialValue() {
            NumberFormat numberFormatter = NumberFormat.getInstance(Locale.US);
            numberFormatter.setGroupingUsed(false);
            numberFormatter.setMaximumFractionDigits(6);
            if (numberFormatter instanceof DecimalFormat) {
                DecimalFormat decimalFormat = (DecimalFormat)numberFormatter;
                DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
                symbols.setNaN("NaN");
                decimalFormat.setDecimalFormatSymbols(symbols);
            }
            return numberFormatter;
        }
    };
    private final String prefix;
    private final DatagramChannel clientChannel;
    private final StatsDClientErrorHandler handler;
    private final String constantTagsRendered;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory(){
        final ThreadFactory delegate = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            Thread result = this.delegate.newThread(r);
            result.setName("StatsD-" + result.getName());
            result.setDaemon(true);
            return result;
        }
    });
    private final BlockingQueue<String> queue;
    public static final Charset MESSAGE_CHARSET = Charset.forName("UTF-8");

    public NonBlockingStatsDClient(String prefix, String hostname, int port) throws StatsDClientException {
        this(prefix, hostname, port, Integer.MAX_VALUE);
    }

    public NonBlockingStatsDClient(String prefix, String hostname, int port, int queueSize) throws StatsDClientException {
        this(prefix, hostname, port, queueSize, (String[])null, (StatsDClientErrorHandler)null);
    }

    public NonBlockingStatsDClient(String prefix, String hostname, int port, String ... constantTags) throws StatsDClientException {
        this(prefix, hostname, port, Integer.MAX_VALUE, constantTags, (StatsDClientErrorHandler)null);
    }

    public NonBlockingStatsDClient(String prefix, String hostname, int port, int queueSize, String ... constantTags) throws StatsDClientException {
        this(prefix, hostname, port, queueSize, constantTags, (StatsDClientErrorHandler)null);
    }

    public NonBlockingStatsDClient(String prefix, String hostname, int port, String[] constantTags, StatsDClientErrorHandler errorHandler) throws StatsDClientException {
        this(prefix, Integer.MAX_VALUE, constantTags, errorHandler, NonBlockingStatsDClient.staticStatsDAddressResolution(hostname, port));
    }

    public NonBlockingStatsDClient(String prefix, String hostname, int port, int queueSize, String[] constantTags, StatsDClientErrorHandler errorHandler) throws StatsDClientException {
        this(prefix, queueSize, constantTags, errorHandler, NonBlockingStatsDClient.staticStatsDAddressResolution(hostname, port));
    }

    public NonBlockingStatsDClient(String prefix, int queueSize, String[] constantTags, StatsDClientErrorHandler errorHandler, Callable<InetSocketAddress> addressLookup) throws StatsDClientException {
        this.prefix = prefix != null && !prefix.isEmpty() ? String.format("%s.", prefix) : "";
        this.handler = errorHandler == null ? NO_OP_HANDLER : errorHandler;
        if (constantTags != null && constantTags.length == 0) {
            constantTags = null;
        }
        this.constantTagsRendered = constantTags != null ? NonBlockingStatsDClient.tagString(constantTags, null) : null;
        try {
            this.clientChannel = DatagramChannel.open();
        }
        catch (Exception e) {
            throw new StatsDClientException("Failed to start StatsD client", e);
        }
        this.queue = new LinkedBlockingQueue<String>(queueSize);
        this.executor.submit(new QueueConsumer(addressLookup));
    }

    @Override
    public void stop() {
        try {
            this.executor.shutdown();
            this.executor.awaitTermination(30L, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            this.handler.handle(e);
        }
        finally {
            if (this.clientChannel != null) {
                try {
                    this.clientChannel.close();
                }
                catch (IOException e) {
                    this.handler.handle(e);
                }
            }
        }
    }

    static String tagString(String[] tags, String tagPrefix) {
        StringBuilder sb;
        if (tagPrefix != null) {
            if (tags == null || tags.length == 0) {
                return tagPrefix;
            }
            sb = new StringBuilder(tagPrefix);
            sb.append(",");
        } else {
            if (tags == null || tags.length == 0) {
                return "";
            }
            sb = new StringBuilder("|#");
        }
        for (int n = tags.length - 1; n >= 0; --n) {
            sb.append(tags[n]);
            if (n <= 0) continue;
            sb.append(",");
        }
        return sb.toString();
    }

    String tagString(String[] tags) {
        return NonBlockingStatsDClient.tagString(tags, this.constantTagsRendered);
    }

    @Override
    public void count(String aspect, long delta, String ... tags) {
        this.send(String.format("%s%s:%d|c%s", this.prefix, aspect, delta, this.tagString(tags)));
    }

    @Override
    public void count(String aspect, long delta, double sampleRate, String ... tags) {
        if (this.isInvalidSample(sampleRate)) {
            return;
        }
        this.send(String.format("%s%s:%d|c|@%f%s", this.prefix, aspect, delta, sampleRate, this.tagString(tags)));
    }

    @Override
    public void incrementCounter(String aspect, String ... tags) {
        this.count(aspect, 1L, tags);
    }

    @Override
    public void incrementCounter(String aspect, double sampleRate, String ... tags) {
        this.count(aspect, 1L, sampleRate, tags);
    }

    @Override
    public void increment(String aspect, String ... tags) {
        this.incrementCounter(aspect, tags);
    }

    @Override
    public void increment(String aspect, double sampleRate, String ... tags) {
        this.incrementCounter(aspect, sampleRate, tags);
    }

    @Override
    public void decrementCounter(String aspect, String ... tags) {
        this.count(aspect, -1L, tags);
    }

    @Override
    public void decrementCounter(String aspect, double sampleRate, String ... tags) {
        this.count(aspect, -1L, sampleRate, tags);
    }

    @Override
    public void decrement(String aspect, String ... tags) {
        this.decrementCounter(aspect, tags);
    }

    @Override
    public void decrement(String aspect, double sampleRate, String ... tags) {
        this.decrementCounter(aspect, sampleRate, tags);
    }

    @Override
    public void recordGaugeValue(String aspect, double value, String ... tags) {
        this.send(String.format("%s%s:%s|g%s", this.prefix, aspect, NUMBER_FORMATTERS.get().format(value), this.tagString(tags)));
    }

    @Override
    public void recordGaugeValue(String aspect, double value, double sampleRate, String ... tags) {
        if (this.isInvalidSample(sampleRate)) {
            return;
        }
        this.send(String.format("%s%s:%s|g|@%f%s", this.prefix, aspect, NUMBER_FORMATTERS.get().format(value), sampleRate, this.tagString(tags)));
    }

    @Override
    public void gauge(String aspect, double value, String ... tags) {
        this.recordGaugeValue(aspect, value, tags);
    }

    @Override
    public void gauge(String aspect, double value, double sampleRate, String ... tags) {
        this.recordGaugeValue(aspect, value, sampleRate, tags);
    }

    @Override
    public void recordGaugeValue(String aspect, long value, String ... tags) {
        this.send(String.format("%s%s:%d|g%s", this.prefix, aspect, value, this.tagString(tags)));
    }

    @Override
    public void recordGaugeValue(String aspect, long value, double sampleRate, String ... tags) {
        if (this.isInvalidSample(sampleRate)) {
            return;
        }
        this.send(String.format("%s%s:%d|g|@%f%s", this.prefix, aspect, value, sampleRate, this.tagString(tags)));
    }

    @Override
    public void gauge(String aspect, long value, String ... tags) {
        this.recordGaugeValue(aspect, value, tags);
    }

    @Override
    public void gauge(String aspect, long value, double sampleRate, String ... tags) {
        this.recordGaugeValue(aspect, value, sampleRate, tags);
    }

    @Override
    public void recordExecutionTime(String aspect, long timeInMs, String ... tags) {
        this.send(String.format("%s%s:%d|ms%s", this.prefix, aspect, timeInMs, this.tagString(tags)));
    }

    @Override
    public void recordExecutionTime(String aspect, long timeInMs, double sampleRate, String ... tags) {
        if (this.isInvalidSample(sampleRate)) {
            return;
        }
        this.send(String.format("%s%s:%d|ms|@%f%s", this.prefix, aspect, timeInMs, sampleRate, this.tagString(tags)));
    }

    @Override
    public void time(String aspect, long value, String ... tags) {
        this.recordExecutionTime(aspect, value, tags);
    }

    @Override
    public void time(String aspect, long value, double sampleRate, String ... tags) {
        this.recordExecutionTime(aspect, value, sampleRate, tags);
    }

    @Override
    public void recordHistogramValue(String aspect, double value, String ... tags) {
        this.send(String.format("%s%s:%s|h%s", this.prefix, aspect, NUMBER_FORMATTERS.get().format(value), this.tagString(tags)));
    }

    @Override
    public void recordHistogramValue(String aspect, double value, double sampleRate, String ... tags) {
        if (this.isInvalidSample(sampleRate)) {
            return;
        }
        this.send(String.format("%s%s:%s|h|@%f%s", this.prefix, aspect, NUMBER_FORMATTERS.get().format(value), sampleRate, this.tagString(tags)));
    }

    @Override
    public void histogram(String aspect, double value, String ... tags) {
        this.recordHistogramValue(aspect, value, tags);
    }

    @Override
    public void histogram(String aspect, double value, double sampleRate, String ... tags) {
        this.recordHistogramValue(aspect, value, sampleRate, tags);
    }

    @Override
    public void recordHistogramValue(String aspect, long value, String ... tags) {
        this.send(String.format("%s%s:%d|h%s", this.prefix, aspect, value, this.tagString(tags)));
    }

    @Override
    public void recordHistogramValue(String aspect, long value, double sampleRate, String ... tags) {
        if (this.isInvalidSample(sampleRate)) {
            return;
        }
        this.send(String.format("%s%s:%d|h|@%f%s", this.prefix, aspect, value, sampleRate, this.tagString(tags)));
    }

    @Override
    public void histogram(String aspect, long value, String ... tags) {
        this.recordHistogramValue(aspect, value, tags);
    }

    @Override
    public void histogram(String aspect, long value, double sampleRate, String ... tags) {
        this.recordHistogramValue(aspect, value, sampleRate, tags);
    }

    private String eventMap(Event event) {
        String alertType;
        String priority;
        String aggregationKey;
        String hostname;
        StringBuilder res = new StringBuilder("");
        long millisSinceEpoch = event.getMillisSinceEpoch();
        if (millisSinceEpoch != -1L) {
            res.append("|d:").append(millisSinceEpoch / 1000L);
        }
        if ((hostname = event.getHostname()) != null) {
            res.append("|h:").append(hostname);
        }
        if ((aggregationKey = event.getAggregationKey()) != null) {
            res.append("|k:").append(aggregationKey);
        }
        if ((priority = event.getPriority()) != null) {
            res.append("|p:").append(priority);
        }
        if ((alertType = event.getAlertType()) != null) {
            res.append("|t:").append(alertType);
        }
        return res.toString();
    }

    @Override
    public void recordEvent(Event event, String ... tags) {
        String title = this.escapeEventString(this.prefix + event.getTitle());
        String text = this.escapeEventString(event.getText());
        this.send(String.format("_e{%d,%d}:%s|%s%s%s", title.length(), text.length(), title, text, this.eventMap(event), this.tagString(tags)));
    }

    private String escapeEventString(String title) {
        return title.replace("\n", "\\n");
    }

    @Override
    public void recordServiceCheckRun(ServiceCheck sc) {
        this.send(this.toStatsDString(sc));
    }

    private String toStatsDString(ServiceCheck sc) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("_sc|%s|%d", sc.getName(), sc.getStatus()));
        if (sc.getTimestamp() > 0) {
            sb.append(String.format("|d:%d", sc.getTimestamp()));
        }
        if (sc.getHostname() != null) {
            sb.append(String.format("|h:%s", sc.getHostname()));
        }
        sb.append(this.tagString(sc.getTags()));
        if (sc.getMessage() != null) {
            sb.append(String.format("|m:%s", sc.getEscapedMessage()));
        }
        return sb.toString();
    }

    @Override
    public void serviceCheck(ServiceCheck sc) {
        this.recordServiceCheckRun(sc);
    }

    @Override
    public void recordSetValue(String aspect, String value, String ... tags) {
        this.send(String.format("%s%s:%s|s%s", this.prefix, aspect, value, this.tagString(tags)));
    }

    private void send(String message) {
        this.queue.offer(message);
    }

    private boolean isInvalidSample(double sampleRate) {
        return sampleRate != 1.0 && Math.random() > sampleRate;
    }

    public static Callable<InetSocketAddress> volatileAddressResolution(final String hostname, final int port) {
        return new Callable<InetSocketAddress>(){

            @Override
            public InetSocketAddress call() throws UnknownHostException {
                return new InetSocketAddress(InetAddress.getByName(hostname), port);
            }
        };
    }

    public static Callable<InetSocketAddress> staticAddressResolution(String hostname, int port) throws Exception {
        final InetSocketAddress address = NonBlockingStatsDClient.volatileAddressResolution(hostname, port).call();
        return new Callable<InetSocketAddress>(){

            @Override
            public InetSocketAddress call() {
                return address;
            }
        };
    }

    private static Callable<InetSocketAddress> staticStatsDAddressResolution(String hostname, int port) throws StatsDClientException {
        try {
            return NonBlockingStatsDClient.staticAddressResolution(hostname, port);
        }
        catch (Exception e) {
            throw new StatsDClientException("Failed to lookup StatsD host", e);
        }
    }

    private class QueueConsumer
    implements Runnable {
        private final ByteBuffer sendBuffer = ByteBuffer.allocate(1400);
        private final Callable<InetSocketAddress> addressLookup;

        QueueConsumer(Callable<InetSocketAddress> addressLookup) {
            this.addressLookup = addressLookup;
        }

        @Override
        public void run() {
            while (!NonBlockingStatsDClient.this.executor.isShutdown()) {
                try {
                    String message = (String)NonBlockingStatsDClient.this.queue.poll(1L, TimeUnit.SECONDS);
                    if (null == message) continue;
                    InetSocketAddress address = this.addressLookup.call();
                    byte[] data = message.getBytes(MESSAGE_CHARSET);
                    if (this.sendBuffer.remaining() < data.length + 1) {
                        this.blockingSend(address);
                    }
                    if (this.sendBuffer.position() > 0) {
                        this.sendBuffer.put((byte)10);
                    }
                    this.sendBuffer.put(data);
                    if (null != NonBlockingStatsDClient.this.queue.peek()) continue;
                    this.blockingSend(address);
                }
                catch (Exception e) {
                    NonBlockingStatsDClient.this.handler.handle(e);
                }
            }
        }

        private void blockingSend(InetSocketAddress address) throws IOException {
            int sizeOfBuffer = this.sendBuffer.position();
            this.sendBuffer.flip();
            int sentBytes = NonBlockingStatsDClient.this.clientChannel.send(this.sendBuffer, address);
            this.sendBuffer.limit(this.sendBuffer.capacity());
            this.sendBuffer.rewind();
            if (sizeOfBuffer != sentBytes) {
                NonBlockingStatsDClient.this.handler.handle(new IOException(String.format("Could not send entirely stat %s to host %s:%d. Only sent %d bytes out of %d bytes", this.sendBuffer.toString(), address.getHostName(), address.getPort(), sentBytes, sizeOfBuffer)));
            }
        }
    }
}

