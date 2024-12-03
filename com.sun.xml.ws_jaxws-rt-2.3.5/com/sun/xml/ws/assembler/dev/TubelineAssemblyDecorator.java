/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.assembler.dev;

import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import java.util.ArrayList;
import java.util.Collection;

public class TubelineAssemblyDecorator {
    public static TubelineAssemblyDecorator composite(Iterable<TubelineAssemblyDecorator> decorators) {
        return new CompositeTubelineAssemblyDecorator(decorators);
    }

    public Tube decorateClient(Tube tube, ClientTubelineAssemblyContext context) {
        return tube;
    }

    public Tube decorateClientHead(Tube tube, ClientTubelineAssemblyContext context) {
        return tube;
    }

    public Tube decorateClientTail(Tube tube, ClientTubelineAssemblyContext context) {
        return tube;
    }

    public Tube decorateServer(Tube tube, ServerTubelineAssemblyContext context) {
        return tube;
    }

    public Tube decorateServerTail(Tube tube, ServerTubelineAssemblyContext context) {
        return tube;
    }

    public Tube decorateServerHead(Tube tube, ServerTubelineAssemblyContext context) {
        return tube;
    }

    private static class CompositeTubelineAssemblyDecorator
    extends TubelineAssemblyDecorator {
        private Collection<TubelineAssemblyDecorator> decorators = new ArrayList<TubelineAssemblyDecorator>();

        public CompositeTubelineAssemblyDecorator(Iterable<TubelineAssemblyDecorator> decorators) {
            for (TubelineAssemblyDecorator decorator : decorators) {
                this.decorators.add(decorator);
            }
        }

        @Override
        public Tube decorateClient(Tube tube, ClientTubelineAssemblyContext context) {
            for (TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateClient(tube, context);
            }
            return tube;
        }

        @Override
        public Tube decorateClientHead(Tube tube, ClientTubelineAssemblyContext context) {
            for (TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateClientHead(tube, context);
            }
            return tube;
        }

        @Override
        public Tube decorateClientTail(Tube tube, ClientTubelineAssemblyContext context) {
            for (TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateClientTail(tube, context);
            }
            return tube;
        }

        @Override
        public Tube decorateServer(Tube tube, ServerTubelineAssemblyContext context) {
            for (TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateServer(tube, context);
            }
            return tube;
        }

        @Override
        public Tube decorateServerTail(Tube tube, ServerTubelineAssemblyContext context) {
            for (TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateServerTail(tube, context);
            }
            return tube;
        }

        @Override
        public Tube decorateServerHead(Tube tube, ServerTubelineAssemblyContext context) {
            for (TubelineAssemblyDecorator decorator : this.decorators) {
                tube = decorator.decorateServerHead(tube, context);
            }
            return tube;
        }
    }
}

