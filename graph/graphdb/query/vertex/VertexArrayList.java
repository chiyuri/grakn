/*
 * GRAKN.AI - THE KNOWLEDGE GRAPH
 * Copyright (C) 2019 Grakn Labs Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package grakn.core.graph.graphdb.query.vertex;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import grakn.core.graph.core.JanusGraphElement;
import grakn.core.graph.core.JanusGraphVertex;
import grakn.core.graph.core.VertexList;
import grakn.core.graph.graphdb.transaction.StandardJanusGraphTx;
import grakn.core.graph.util.datastructures.IterablesUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of {@link VertexListInternal} that stores the actual vertex references
 * and simply wraps an {@link ArrayList} and keeps a boolean flag to remember whether this list is in sort order.
 */
public class VertexArrayList implements VertexListInternal {

    public static final Comparator<JanusGraphVertex> VERTEX_ID_COMPARATOR = Comparator.comparingLong(JanusGraphElement::longId);

    private final StandardJanusGraphTx tx;
    private List<JanusGraphVertex> vertices;
    private boolean sorted;

    private VertexArrayList(StandardJanusGraphTx tx, List<JanusGraphVertex> vertices, boolean sorted) {
        Preconditions.checkArgument(tx != null && vertices != null);
        this.tx = tx;
        this.vertices = vertices;
        this.sorted = sorted;
    }

    public VertexArrayList(StandardJanusGraphTx tx) {
        Preconditions.checkNotNull(tx);
        this.tx = tx;
        vertices = new ArrayList<>();
        sorted = true;
    }


    @Override
    public void add(JanusGraphVertex n) {
        if (!vertices.isEmpty()) sorted = sorted && (vertices.get(vertices.size() - 1).longId() <= n.longId());
        vertices.add(n);
    }

    @Override
    public long getID(int pos) {
        return vertices.get(pos).longId();
    }

    @Override
    public List<Long> getIDs() {
        return vertices.stream().map(JanusGraphElement::longId).collect(Collectors.toList());
    }

    @Override
    public JanusGraphVertex get(int pos) {
        return vertices.get(pos);
    }

    @Override
    public boolean isSorted() {
        return sorted;
    }

    @Override
    public VertexList subList(int fromPosition, int length) {
        return new VertexArrayList(tx, vertices.subList(fromPosition, fromPosition + length), sorted);
    }

    @Override
    public int size() {
        return vertices.size();
    }

    @Override
    public void addAll(VertexList vertexlist) {
        Preconditions.checkArgument(vertexlist instanceof VertexArrayList, "Only supporting union of identical lists.");
        VertexArrayList other = ((VertexArrayList) vertexlist);
        if (sorted && other.isSorted()) {
            //Merge sort
            vertices = IterablesUtil.mergeSort(vertices, other.vertices, VERTEX_ID_COMPARATOR);
        } else {
            sorted = false;
            vertices.addAll(other.vertices);
        }
    }

    @Override
    public Iterator<JanusGraphVertex> iterator() {
        return Iterators.unmodifiableIterator(vertices.iterator());
    }

}