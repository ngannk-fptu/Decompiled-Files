/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.TrainedModel;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public abstract class TrainedModelDetector
implements Detector {
    private static final long serialVersionUID = 1L;
    private final Map<MediaType, TrainedModel> MODEL_MAP = new HashMap<MediaType, TrainedModel>();

    public TrainedModelDetector() {
        this.loadDefaultModels(this.getClass().getClassLoader());
    }

    public int getMinLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public MediaType detect(InputStream input, Metadata metadata) throws IOException {
        if (input != null) {
            input.mark(this.getMinLength());
            float[] histogram = this.readByteFrequencies(input);
            float maxprob = 0.5f;
            MediaType maxType = MediaType.OCTET_STREAM;
            for (Map.Entry<MediaType, TrainedModel> entry : this.MODEL_MAP.entrySet()) {
                MediaType key = entry.getKey();
                TrainedModel model = entry.getValue();
                float prob = model.predict(histogram);
                if (!(maxprob < prob)) continue;
                maxprob = prob;
                maxType = key;
            }
            input.reset();
            return maxType;
        }
        return null;
    }

    protected float[] readByteFrequencies(InputStream input) throws IOException {
        ReadableByteChannel inputChannel = Channels.newChannel(input);
        float[] histogram = new float[257];
        histogram[0] = 1.0f;
        ByteBuffer buf = ByteBuffer.allocate(5120);
        int bytesRead = inputChannel.read(buf);
        float max = -1.0f;
        while (bytesRead != -1) {
            ((Buffer)buf).flip();
            while (buf.hasRemaining()) {
                int byt;
                int idx = byt = buf.get();
                ++idx;
                if (byt < 0) {
                    int n = idx = 256 + idx;
                    histogram[n] = histogram[n] + 1.0f;
                } else {
                    int n = idx;
                    histogram[n] = histogram[n] + 1.0f;
                }
                max = Math.max(max, histogram[idx]);
            }
            buf.clear();
            bytesRead = inputChannel.read(buf);
        }
        for (int i = 1; i < histogram.length; ++i) {
            int n = i;
            histogram[n] = histogram[n] / max;
            histogram[i] = (float)Math.sqrt(histogram[i]);
        }
        return histogram;
    }

    private void writeHisto(float[] histogram) throws IOException {
        Path histPath = new TemporaryResources().createTempFile();
        try (BufferedWriter writer = Files.newBufferedWriter(histPath, StandardCharsets.UTF_8, new OpenOption[0]);){
            for (float bin : histogram) {
                writer.write(bin + "\t");
            }
            writer.write("\r\n");
        }
    }

    public void loadDefaultModels(Path modelFile) {
        try (InputStream in = Files.newInputStream(modelFile, new OpenOption[0]);){
            this.loadDefaultModels(in);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to read the default media type registry", e);
        }
    }

    public void loadDefaultModels(File modelFile) {
        this.loadDefaultModels(modelFile.toPath());
    }

    public abstract void loadDefaultModels(InputStream var1);

    public abstract void loadDefaultModels(ClassLoader var1);

    protected void registerModels(MediaType type, TrainedModel model) {
        this.MODEL_MAP.put(type, model);
    }
}

