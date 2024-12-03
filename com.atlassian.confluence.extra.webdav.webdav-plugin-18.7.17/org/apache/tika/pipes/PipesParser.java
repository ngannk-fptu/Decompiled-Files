/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.PipesClient;
import org.apache.tika.pipes.PipesConfig;
import org.apache.tika.pipes.PipesException;
import org.apache.tika.pipes.PipesResult;

public class PipesParser
implements Closeable {
    private final PipesConfig pipesConfig;
    private final List<PipesClient> clients = new ArrayList<PipesClient>();
    private final ArrayBlockingQueue<PipesClient> clientQueue;

    public PipesParser(PipesConfig pipesConfig) {
        this.pipesConfig = pipesConfig;
        this.clientQueue = new ArrayBlockingQueue(pipesConfig.getNumClients());
        for (int i = 0; i < pipesConfig.getNumClients(); ++i) {
            PipesClient client = new PipesClient(pipesConfig);
            this.clientQueue.offer(client);
            this.clients.add(client);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PipesResult parse(FetchEmitTuple t) throws InterruptedException, PipesException, IOException {
        PipesResult pipesResult;
        block6: {
            PipesClient client;
            block4: {
                PipesResult pipesResult2;
                block5: {
                    client = null;
                    try {
                        client = this.clientQueue.poll(this.pipesConfig.getMaxWaitForClientMillis(), TimeUnit.MILLISECONDS);
                        if (client != null) break block4;
                        pipesResult2 = PipesResult.CLIENT_UNAVAILABLE_WITHIN_MS;
                        if (client == null) break block5;
                        this.clientQueue.offer(client);
                    }
                    catch (Throwable throwable) {
                        if (client != null) {
                            this.clientQueue.offer(client);
                        }
                        throw throwable;
                    }
                }
                return pipesResult2;
            }
            pipesResult = client.process(t);
            if (client == null) break block6;
            this.clientQueue.offer(client);
        }
        return pipesResult;
    }

    @Override
    public void close() throws IOException {
        ArrayList<IOException> exceptions = new ArrayList<IOException>();
        for (PipesClient pipesClient : this.clients) {
            try {
                pipesClient.close();
            }
            catch (IOException e) {
                exceptions.add(e);
            }
        }
        if (exceptions.size() > 0) {
            throw (IOException)exceptions.get(0);
        }
    }
}

