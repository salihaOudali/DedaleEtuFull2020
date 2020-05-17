package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import dataStructures.tuple.Couple;

/**
 * This simple topology representation only deals with the graph, not its content.</br>
 * The knowledge representation is not well written (at all), it is just given as a minimal example.</br>
 * The viewer methods are not independent of the data structure, and the dijkstra is recomputed every-time.
 * 
 * @author hc
 */
public class MapRepresentation implements Serializable {

	public enum MapAttribute {
		agent,open
	}


	private static final long serialVersionUID = -1333959882640838272L;
	private List<String> openNodes = new ArrayList<String>();
	private List<String> closedNodes = new ArrayList<String>();
	private List<Couple<String,String>> edges = new ArrayList<Couple<String, String>>();
	private Graph g; //data structure
	private Viewer viewer; //ref to the display
	private Integer nbEdges;//used to generate the edges ids

	/*********************************
	 * Parameters for graph rendering
	 ********************************/

	private String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
	private String nodeStyle_open = "node.agent {"+"fill-color: forestgreen;"+"}";
	private String nodeStyle_agent = "node.open {"+"fill-color: blue;"+"}";
	private String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_open;

	public MapRepresentation() {
		System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		this.g= new SingleGraph("My world vision");
		this.g.setAttribute("ui.stylesheet",nodeStyle);
		this.viewer = this.g.display();
		this.nbEdges=0;
	}

	/**
	 * Associate to a node an attribute in order to identify them by type. 
	 * @param id
	 * @param mapAttribute
	 */
	public void addNode(String id,MapAttribute mapAttribute){
		Node n;
		if (this.g.getNode(id)==null){
			n=this.g.addNode(id);
			if (mapAttribute.equals(MapAttribute.open)) {
				openNodes.add(id);
			}
			else {
				closedNodes.add(id);
			}
		}else{
			n=this.g.getNode(id);
		}
		n.clearAttributes();
		n.addAttribute("ui.class", mapAttribute.toString());
		n.addAttribute("ui.label",id);
	}

	/**
	 * Add the node id if not already existing
	 * @param id
	 */
	public void addNode(String id){
		Node n=this.g.getNode(id);
		if(n==null){
			n=this.g.addNode(id);
			openNodes.add(id);
		}else{
			n.clearAttributes();
		}
		n.addAttribute("ui.label",id);
	}



	/**
	 * Add the edge if not already existing.
	 * @param idNode1
	 * @param idNode2
	 */
	public void addEdge(String idNode1,String idNode2) throws EdgeRejectedException{
		try {
			this.nbEdges++;
			this.g.addEdge(this.nbEdges.toString(), idNode1, idNode2);
			edges.add(new Couple(idNode1, idNode2));

		}catch (EdgeRejectedException e){
			//Do not add an already existing one
			this.nbEdges--;
			throw e;
		}

	}

	/**
	 * Compute the shortest Path from idFrom to IdTo. The computation is currently not very efficient
	 * 
	 * @param idFrom id of the origin node
	 * @param idTo id of the destination node
	 * @return the list of nodes to follow
	 */
	public List<String> getShortestPath(String idFrom,String idTo){
		List<String> shortestPath=new ArrayList<String>();

		Dijkstra dijkstra = new Dijkstra();//number of edge
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(idFrom));
		dijkstra.compute();//compute the distance to all nodes from idFrom
		List<Node> path=dijkstra.getPath(g.getNode(idTo)).getNodePath(); //the shortest path from idFrom to idTo
		Iterator<Node> iter=path.iterator();
		while (iter.hasNext()){
			shortestPath.add(iter.next().getId());
		}
		dijkstra.clear();
		shortestPath.remove(0);//remove the current position
		return shortestPath;
	}

	public <T extends Edge> Iterable<? extends Edge> getEdgesIterator() {
		return g.getEachEdge();
	}


	//	public MapRepresentation mergeMaps(MapRepresentation map2) {
	//		for (Node i: map2.getNodes()) {
	//			this.addNode(i.getId());
	//		}
	//		for (Edge e: map2.getEdges()) {
	//			this.addEdge(e.getNode0(), e.getNode1());
	//		}
	//		
	//		return null;
	//	}


	public void prepareMigration(){
		if (this.viewer!=null){
			//this.viewer.close();

		}
		//closeGui();
		this.viewer=null;
		this.g=null;
	}


	/**
	 * After migration we load the serialized data and recreate the non-serializable
		components (Gui,..)
	 */
	public void loadSavedData(){
		System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		this.g= new SingleGraph("My world vision");
		this.g.setAttribute("ui.stylesheet",nodeStyle);

		//openGui();
		this.viewer = this.g.display();
		this.nbEdges =0;
		//Parcours de sg et remplissage de g.
		for (String n : openNodes) {
			this.g.addNode(n);
		}
		for (String n : closedNodes) {
			this.g.addNode(n);
		}
		for (Couple<String, String> c : edges) {
			String idNode1 = c.getLeft();
			String idNode2 = c.getRight();
			try {
				this.nbEdges++;
				this.g.addEdge(this.nbEdges.toString(), idNode1, idNode2);

			}catch (EdgeRejectedException e){
				//Do not add an already existing one
				this.nbEdges--;
				throw e;
			}
		}
		System.out.println("Loading done");
	}
	
	public String getTankerPosition() {
		BetweennessCentrality center = new BetweennessCentrality();
		center.init(g);
		center.compute();
		double max = 0;
		Node c = g.getNode(0);
		for (Node n: g.getNodeSet()) {
			double betweenness = n.getAttribute("Cb");
			if (max < betweenness ) {
				max = betweenness;
				c=n;
			}
		}
		return c.toString();
	}


	/**
	 * Method called before migration to kill all non serializable graphStream components
	 */
	private void closeGui() {
		//once the graph is saved,
		//Parcours de sg et remplissage de g.
		//clear non serializable components
		if (this.viewer!=null){
			try{
				this.viewer.close();
			}catch(NullPointerException e){
				System.err.println("Bug graphstream viewer.close() work-around - https://github.com/graphstream/gs-core/issues/150");
			}
			this.viewer=null;
		}
	}

	/**
	 * Method called after a migration to reopen GUI components
	 */
	private void openGui() {
		this.viewer =new Viewer(this.g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
		viewer.addDefaultView(true);
	}
}