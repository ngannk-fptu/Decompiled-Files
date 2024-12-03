/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.tika.detect.NNTrainedModelBuilder;
import org.apache.tika.detect.TrainedModelDetector;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NNExampleModelDetector
extends TrainedModelDetector {
    private static final String EXAMPLE_NNMODEL_FILE = "tika-example.nnmodel";
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(NNExampleModelDetector.class);

    public NNExampleModelDetector() {
    }

    public NNExampleModelDetector(Path modelFile) {
        this.loadDefaultModels(modelFile);
    }

    public NNExampleModelDetector(File modelFile) {
        this.loadDefaultModels(modelFile);
    }

    @Override
    public void loadDefaultModels(InputStream modelStream) {
        BufferedReader bReader = new BufferedReader(new InputStreamReader(modelStream, StandardCharsets.UTF_8));
        NNTrainedModelBuilder nnBuilder = new NNTrainedModelBuilder();
        try {
            String line;
            while ((line = bReader.readLine()) != null) {
                if ((line = line.trim()).startsWith("#")) {
                    this.readDescription(nnBuilder, line);
                    continue;
                }
                this.readNNParams(nnBuilder, line);
                super.registerModels(nnBuilder.getType(), nnBuilder.build());
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to read the default media type registry", e);
        }
    }

    @Override
    public void loadDefaultModels(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = TrainedModelDetector.class.getClassLoader();
        }
        String classPrefix = TrainedModelDetector.class.getPackage().getName().replace('.', '/') + "/";
        URL modelURL = classLoader.getResource(classPrefix + EXAMPLE_NNMODEL_FILE);
        Objects.requireNonNull(modelURL, "required resource " + classPrefix + EXAMPLE_NNMODEL_FILE + " not found");
        try (InputStream stream = modelURL.openStream();){
            this.loadDefaultModels(stream);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to read the default media type registry", e);
        }
    }

    private void readDescription(NNTrainedModelBuilder builder, String line) {
        String[] sarr = line.split("\t");
        try {
            MediaType type = MediaType.parse(sarr[1]);
            int numInputs = Integer.parseInt(sarr[2]);
            int numHidden = Integer.parseInt(sarr[3]);
            int numOutputs = Integer.parseInt(sarr[4]);
            builder.setNumOfInputs(numInputs);
            builder.setNumOfHidden(numHidden);
            builder.setNumOfOutputs(numOutputs);
            builder.setType(type);
        }
        catch (Exception e) {
            LOG.warn("Unable to parse the model configuration", (Throwable)e);
            throw new RuntimeException("Unable to parse the model configuration", e);
        }
    }

    private void readNNParams(NNTrainedModelBuilder builder, String line) {
        String[] sarr = line.split("\t");
        int n = sarr.length;
        float[] params = new float[n];
        try {
            int i = 0;
            for (String fstr : sarr) {
                params[i] = Float.parseFloat(fstr);
                ++i;
            }
            builder.setParams(params);
        }
        catch (Exception e) {
            LOG.warn("Unable to parse the model configuration", (Throwable)e);
            throw new RuntimeException("Unable to parse the model configuration", e);
        }
    }
}

