/*
 * Copyright (C) 2020 Grakn Labs
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
 *
 */

package grakn.core.kb.concept.structure;

import grakn.core.core.Schema;
import org.apache.tinkerpop.gremlin.structure.Edge;

public interface EdgeElement extends AbstractElement<Edge> {
    VertexElement source();

    VertexElement target();

    void property(Schema.EdgeProperty key, Object value);

    <X> X property(Schema.EdgeProperty key);

    Boolean propertyBoolean(Schema.EdgeProperty key);
}
