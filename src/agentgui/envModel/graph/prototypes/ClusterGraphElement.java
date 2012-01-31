/**
 * ***************************************************************
 * Agent.GUI is a framework to develop Multi-agent based simulation 
 * applications based on the JADE - Framework in compliance with the 
 * FIPA specifications. 
 * Copyright (C) 2010 Christian Derksen and DAWIS
 * http://www.dawis.wiwi.uni-due.de
 * http://sourceforge.net/projects/agentgui/
 * http://www.agentgui.org 
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */

package agentgui.envModel.graph.prototypes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import agentgui.envModel.graph.networkModel.GraphEdge;
import agentgui.envModel.graph.networkModel.GraphElement;
import agentgui.envModel.graph.networkModel.GraphNode;
import edu.uci.ics.jung.graph.Graph;

/**
 * A graph / network element with a star arrangement. Specialy designed for a Cluster
 * 
 * @author David Pachula
 */
public class ClusterGraphElement extends GraphElementPrototype {

    public static final String CLUSTER_PREFIX = "C_";

    /**
     * The vector of outernodes which forms the corners of the element.
     */
    Vector<GraphNode> outerNodes;

    /**
     * The central node of the element, to which all outernodes are connected.
     */
    GraphNode centralNode;

    /**
     * Default constructor with 3 corners
     */
    public ClusterGraphElement() {
	super();
	outerNodes = new Vector<GraphNode>();
    }

    /**
     * Constructor for creating the Star prototype with 'n' connection points
     * 
     * @param n the number of connection points
     */
    public ClusterGraphElement(Integer n) {
	super();
	if (n >= 3) {

	    outerNodes = new Vector<GraphNode>();
	} else {
	    throw new GraphElementPrototypeException("Number of connection points should be greater than 3");
	}
    }

    @Override
    public HashSet<GraphElement> addToGraph(Graph<GraphNode, GraphEdge> graph) {
	// check if n is set
	{
	    this.graph = graph;
	    // Create a HashSet for the nodes and edges
	    HashSet<GraphElement> elements = new HashSet<GraphElement>();

	    // Create central node and add to the graph
	    centralNode = new GraphNode();
	    centralNode.setId(GraphNode.GRAPH_NODE_PREFIX + (nodeCounter++));
	    graph.addVertex(centralNode);
	    elements.add(centralNode);

	    return elements;
	}
    }

    @Override
    public HashSet<GraphElement> addAfter(Graph<GraphNode, GraphEdge> graph, GraphElementPrototype predecessor) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public HashSet<GraphElement> addBefore(Graph<GraphNode, GraphEdge> graph, GraphElementPrototype successor) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public HashSet<GraphElement> addBetween(Graph<GraphNode, GraphEdge> graph, GraphElementPrototype predecessor, GraphElementPrototype successor) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public GraphNode getFreeEntry() {
	Iterator<GraphNode> iter = outerNodes.iterator();
	while (iter.hasNext()) {
	    GraphNode node = iter.next();
	    if (graph.getNeighborCount(node) < 2) {
		return node;
	    }
	}
	return null;
    }

    @Override
    public GraphNode getFreeExit() {
	return getFreeEntry();
    }

    @Override
    public boolean isDirected() {
	return false;
    }
}