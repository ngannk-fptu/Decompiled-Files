/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.health;

import com.atlassian.confluence.internal.health.HealthCheck;
import com.atlassian.confluence.internal.health.HealthCheckRegistry;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultHealthCheckRegistry
implements HealthCheckRegistry {
    private final Node root = new Node(null);
    private boolean allChecksRegistered;

    @Override
    public void register(HealthCheck healthCheck) {
        if (healthCheck.getPrerequisites().isEmpty()) {
            this.root.addChild(healthCheck);
        } else {
            this.addAsChildOfPrerequisites(healthCheck);
        }
    }

    private void addAsChildOfPrerequisites(HealthCheck healthCheck) {
        healthCheck.getPrerequisites().forEach(prereqCheck -> {
            Node prereqNode = this.findNode((HealthCheck)prereqCheck, this.root).orElseThrow(this.noSuchPrerequisite(healthCheck, (HealthCheck)prereqCheck));
            prereqNode.addChild(healthCheck);
        });
    }

    private Supplier<IllegalArgumentException> noSuchPrerequisite(HealthCheck healthCheck, HealthCheck prerequisite) {
        return () -> new IllegalArgumentException(String.format("HealthCheck %s has unknown prerequisite %s", healthCheck, prerequisite));
    }

    private Optional<Node> findNode(HealthCheck healthCheck, Node startNode) {
        if (healthCheck.equals(startNode.check)) {
            return Optional.of(startNode);
        }
        return startNode.children.stream().map(childNode -> this.findNode(healthCheck, (Node)childNode)).filter(Optional::isPresent).map(Optional::get).findFirst();
    }

    @Override
    public void registrationComplete() {
        this.allChecksRegistered = true;
    }

    @Override
    public Collection<HealthCheck> getAll() {
        if (this.allChecksRegistered) {
            return this.getAll(this.root, Sets.newLinkedHashSet());
        }
        throw new IllegalStateException("Cannot obtain checks until registration is complete");
    }

    private Collection<HealthCheck> getAll(Node parentNode, Collection<HealthCheck> accumulator) {
        parentNode.children.forEach(child -> accumulator.add(child.check));
        parentNode.children.forEach(child -> this.getAll((Node)child, accumulator));
        return accumulator;
    }

    private static class Node {
        private final HealthCheck check;
        private final Collection<Node> children = new ArrayList<Node>();

        private Node(@Nullable HealthCheck check) {
            this.check = check;
        }

        void addChild(HealthCheck healthCheck) {
            this.children.add(new Node(healthCheck));
        }
    }
}

