/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.graph;

public final class GraphSemantic
extends Enum<GraphSemantic> {
    public static final /* enum */ GraphSemantic FETCH = new GraphSemantic("javax.persistence.fetchgraph", "jakarta.persistence.fetchgraph");
    public static final /* enum */ GraphSemantic LOAD = new GraphSemantic("javax.persistence.loadgraph", "jakarta.persistence.loadgraph");
    private final String jpaHintName;
    private final String jakartaJpaHintName;
    private static final /* synthetic */ GraphSemantic[] $VALUES;

    public static GraphSemantic[] values() {
        return (GraphSemantic[])$VALUES.clone();
    }

    public static GraphSemantic valueOf(String name) {
        return Enum.valueOf(GraphSemantic.class, name);
    }

    private GraphSemantic(String jpaHintName, String jakartaJpaHintName) {
        this.jpaHintName = jpaHintName;
        this.jakartaJpaHintName = jakartaJpaHintName;
    }

    public String getJpaHintName() {
        return this.jpaHintName;
    }

    public String getJakartaJpaHintName() {
        return this.jakartaJpaHintName;
    }

    public static GraphSemantic fromJpaHintName(String hintName) {
        assert (hintName != null);
        if (FETCH.getJpaHintName().equals(hintName) || FETCH.getJakartaJpaHintName().equals(hintName)) {
            return FETCH;
        }
        if (LOAD.getJpaHintName().equalsIgnoreCase(hintName) || LOAD.getJakartaJpaHintName().equalsIgnoreCase(hintName)) {
            return LOAD;
        }
        throw new IllegalArgumentException("Unknown EntityGraph hint name [" + hintName + "]; expecting `" + GraphSemantic.FETCH.jpaHintName + "` or `" + GraphSemantic.LOAD.jpaHintName + "`.");
    }

    static {
        $VALUES = new GraphSemantic[]{FETCH, LOAD};
    }
}

