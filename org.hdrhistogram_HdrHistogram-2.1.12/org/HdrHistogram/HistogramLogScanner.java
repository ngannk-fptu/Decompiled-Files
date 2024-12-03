/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import org.HdrHistogram.Base64Helper;
import org.HdrHistogram.EncodableHistogram;

public class HistogramLogScanner
implements Closeable {
    private final LazyHistogramReader lazyReader;
    protected final Scanner scanner;

    public HistogramLogScanner(String inputFileName) throws FileNotFoundException {
        this(new Scanner(new File(inputFileName)));
    }

    public HistogramLogScanner(InputStream inputStream) {
        this(new Scanner(inputStream));
    }

    public HistogramLogScanner(File inputFile) throws FileNotFoundException {
        this(new Scanner(inputFile));
    }

    private HistogramLogScanner(Scanner scanner) {
        this.scanner = scanner;
        this.lazyReader = new LazyHistogramReader(scanner);
        this.initScanner();
    }

    private void initScanner() {
        this.scanner.useLocale(Locale.US);
        this.scanner.useDelimiter("[ ,\\r\\n]");
    }

    @Override
    public void close() {
        this.scanner.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void process(EventHandler handler) {
        while (this.scanner.hasNextLine()) {
            try {
                if (this.scanner.hasNext("\\#.*")) {
                    if (this.scanner.hasNext("#\\[StartTime:")) {
                        this.scanner.next("#\\[StartTime:");
                        if (!this.scanner.hasNextDouble()) continue;
                        double startTimeSec = this.scanner.nextDouble();
                        if (!handler.onStartTime(startTimeSec)) continue;
                        return;
                    }
                    if (this.scanner.hasNext("#\\[BaseTime:")) {
                        this.scanner.next("#\\[BaseTime:");
                        if (!this.scanner.hasNextDouble()) continue;
                        double baseTimeSec = this.scanner.nextDouble();
                        if (!handler.onBaseTime(baseTimeSec)) continue;
                        return;
                    }
                    if (!handler.onComment(this.scanner.next("\\#.*"))) continue;
                    return;
                }
                if (this.scanner.hasNext("\"StartTimestamp\".*")) continue;
                String tagString = null;
                if (this.scanner.hasNext("Tag\\=.*")) {
                    tagString = this.scanner.next("Tag\\=.*").substring(4);
                }
                double logTimeStampInSec = this.scanner.nextDouble();
                double intervalLengthSec = this.scanner.nextDouble();
                this.scanner.nextDouble();
                this.lazyReader.allowGet();
                if (!handler.onHistogram(tagString, logTimeStampInSec, intervalLengthSec, this.lazyReader)) continue;
                return;
            }
            catch (Throwable ex) {
                if (!handler.onException(ex)) continue;
                return;
            }
            finally {
                this.scanner.nextLine();
            }
        }
    }

    public boolean hasNextLine() {
        return this.scanner.hasNextLine();
    }

    private static class LazyHistogramReader
    implements EncodableHistogramSupplier {
        private final Scanner scanner;
        private boolean gotIt = true;

        private LazyHistogramReader(Scanner scanner) {
            this.scanner = scanner;
        }

        private void allowGet() {
            this.gotIt = false;
        }

        @Override
        public EncodableHistogram read() throws DataFormatException {
            if (this.gotIt) {
                throw new IllegalStateException();
            }
            this.gotIt = true;
            String compressedPayloadString = this.scanner.next();
            ByteBuffer buffer = ByteBuffer.wrap(Base64Helper.parseBase64Binary(compressedPayloadString));
            EncodableHistogram histogram = EncodableHistogram.decodeFromCompressedByteBuffer(buffer, 0L);
            return histogram;
        }
    }

    public static interface EventHandler {
        public boolean onComment(String var1);

        public boolean onBaseTime(double var1);

        public boolean onStartTime(double var1);

        public boolean onHistogram(String var1, double var2, double var4, EncodableHistogramSupplier var6);

        public boolean onException(Throwable var1);
    }

    public static interface EncodableHistogramSupplier {
        public EncodableHistogram read() throws DataFormatException;
    }
}

