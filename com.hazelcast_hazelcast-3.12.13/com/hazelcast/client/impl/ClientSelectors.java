/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.ClientSelector;
import com.hazelcast.core.Client;
import com.hazelcast.util.AddressUtil;
import java.util.Set;

public final class ClientSelectors {
    private ClientSelectors() {
    }

    public static ClientSelector any() {
        return new ClientSelector(){

            @Override
            public boolean select(Client client) {
                return true;
            }

            public String toString() {
                return "ClientSelector{any}";
            }
        };
    }

    public static ClientSelector none() {
        return new ClientSelector(){

            @Override
            public boolean select(Client client) {
                return false;
            }

            public String toString() {
                return "ClientSelector{none}";
            }
        };
    }

    public static ClientSelector nameSelector(final String nameMask) {
        return new ClientSelector(){

            @Override
            public boolean select(Client client) {
                String name = client.getName();
                if (name == null) {
                    return false;
                }
                return name.matches(nameMask);
            }

            public String toString() {
                return "ClientSelector{nameMask:" + nameMask + " }";
            }
        };
    }

    public static ClientSelector ipSelector(final String ipMask) {
        return new ClientSelector(){

            @Override
            public boolean select(Client client) {
                return AddressUtil.matchInterface(client.getSocketAddress().getAddress().getHostAddress(), ipMask);
            }

            public String toString() {
                return "ClientSelector{ipMask:" + ipMask + " }";
            }
        };
    }

    public static ClientSelector or(final ClientSelector ... selectors) {
        return new ClientSelector(){

            @Override
            public boolean select(Client client) {
                for (ClientSelector selector : selectors) {
                    if (!selector.select(client)) continue;
                    return true;
                }
                return false;
            }

            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append("ClientSelector{or:");
                for (ClientSelector selector : selectors) {
                    builder.append(selector).append(", ");
                }
                builder.append("}");
                return builder.toString();
            }
        };
    }

    public static ClientSelector labelSelector(final String labelMask) {
        return new ClientSelector(){

            @Override
            public boolean select(Client client) {
                Set<String> labels = client.getLabels();
                for (String label : labels) {
                    if (!label.matches(labelMask)) continue;
                    return true;
                }
                return false;
            }

            public String toString() {
                return "ClientSelector{labelMask:" + labelMask + " }";
            }
        };
    }

    public static ClientSelector inverse(final ClientSelector clientSelector) {
        return new ClientSelector(){

            @Override
            public boolean select(Client client) {
                return !clientSelector.select(client);
            }

            public String toString() {
                return "ClientSelector{inverse:" + clientSelector + " }";
            }
        };
    }
}

