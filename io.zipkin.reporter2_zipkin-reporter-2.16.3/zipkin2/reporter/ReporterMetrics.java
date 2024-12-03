/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.reporter;

public interface ReporterMetrics {
    public static final ReporterMetrics NOOP_METRICS = new ReporterMetrics(){

        @Override
        public void incrementMessages() {
        }

        @Override
        public void incrementMessagesDropped(Throwable cause) {
        }

        @Override
        public void incrementSpans(int quantity) {
        }

        @Override
        public void incrementSpanBytes(int quantity) {
        }

        @Override
        public void incrementMessageBytes(int quantity) {
        }

        @Override
        public void incrementSpansDropped(int quantity) {
        }

        @Override
        public void updateQueuedSpans(int update) {
        }

        @Override
        public void updateQueuedBytes(int update) {
        }

        public String toString() {
            return "NoOpReporterMetrics";
        }
    };

    public void incrementMessages();

    public void incrementMessagesDropped(Throwable var1);

    public void incrementSpans(int var1);

    public void incrementSpanBytes(int var1);

    public void incrementMessageBytes(int var1);

    public void incrementSpansDropped(int var1);

    public void updateQueuedSpans(int var1);

    public void updateQueuedBytes(int var1);
}

