package P2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

class Node1 {
	int a;
	Node1(int a) { this.a = a; }
}

public class Betweeness {
	/*
	 * Node is shared between graph and tree, both graph node and tree node
	 * use the Node class here.
	 * 
	 * Each user corresponds to one Node object, reused in different data structures.
	 */
	class Node {
		String name;
		int num_shortest_path;
		double credit;
		int levelInTree;
		boolean inComputeCredit;
		boolean inCommunity;
		// TODO: check if we need set or ArrayList.
		//Set<GraphEdge> edges = new HashSet<GraphEdge>();
		ArrayList<Edge> edges;
		ArrayList<Node> parents;
		ArrayList<Node> children;
		
		Node(String n) {
			this.name = n;
			this.num_shortest_path = 0;
			this.credit = 0;
			this.levelInTree = Integer.MAX_VALUE;
			this.inComputeCredit = false;
			this.inCommunity = false;
			edges = new ArrayList<Edge>();
			parents = new ArrayList<Node>();
			children = new ArrayList<Node>();
		}
		
		@Override
		public int hashCode() {
			return name.hashCode() +
				   parents.hashCode() * 63 + children.hashCode() * 127;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj == null) {
				return false;
			} else if (getClass() != obj.getClass()) {
				return false;
			}
			return this.name.equals(((Node)obj).name); // num_shortest_path and credit not used here.
			/*
			Node new_node = (Node)obj;
			if (this.edges == null && new_node.edges != null || 
				this.edges != null && new_node.edges == null) {
				return false;
			} else if (this.edges != null && new_node.edges != null) {
				if (this.edges.size() != new_node.edges.size()) {
					return false;
				}
			}
			*/
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	/*
	 * Similarly, Edge is shared between graph and tree.
	 * 
	 * Each pair of user connection corresponds to one edge, shared by different data structure.
	 */
	class Edge {
		Node[] nodes;
		double credit = 0.0;
		double betweeness = 0.0;
		
		Edge(Node n1, Node n2) {
			nodes = new Node[2];
			nodes[0] = n1;
			nodes[1] = n2;
		}
	
		@Override
		public int hashCode() {
			return nodes[0].hashCode() * 31 + nodes[1].hashCode() * 63 + (int)(betweeness) * 127;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj == null) {
				return false;
			} else if (getClass() != obj.getClass()) {
				return false;
			}
			return ((Edge)obj).nodes[0] == this.nodes[0] // check object equality, not content of obj
				&& ((Edge)obj).nodes[1] == this.nodes[1] // check object equality, not content of obj
				&& ((Edge)obj).betweeness == this.betweeness;
		}
		
		@Override
		public String toString() {
			return nodes[0].toString() + "-" + nodes[1].toString() + "-" + Double.toString(betweeness);
		}
	}
	public ArrayList<ArrayList<String>> inputs;
	public Set<Node> all_users;
	public Set<Edge> all_edges;
	public HashMap<String, Node> all_user_map;
	public HashMap<String, Edge> all_edge_map;
	public ArrayList<ArrayList<Set<Node>>> communities;
	
	Betweeness() {
		inputs = new ArrayList<ArrayList<String>>();
		communities = new ArrayList<ArrayList<Set<Node>>>();
		all_users = new HashSet<Node>();
		all_edges = new HashSet<Edge>();
		all_user_map = new HashMap<String, Node>();
		all_edge_map = new HashMap<String, Edge>();
	}
	
	private void error_input(String line) {
		System.out.println("line is " + line);
		System.out.println("Wrong input");
		return;
	}
	
	public int read_input_construct_graph() {
		Scanner in = new Scanner(System.in);
		in.nextLine(); // skip first line
		while (in.hasNextLine()) {
			String line = in.nextLine();
			if (line.trim().length() == 0) break;
			String[] users = line.split(",");
			if (users.length != 2) {
				error_input(line);
				in.close();
				return -1;
			}
			users[0] = users[0].trim();
			users[1] = users[1].trim();
			inputs.add(new ArrayList<String>(Arrays.asList(users)));
			if (!all_user_map.containsKey(users[0])) {
				all_user_map.put(users[0], new Node(users[0]));
				all_users.add(all_user_map.get(users[0]));
			}
			if (!all_user_map.containsKey(users[1])) {
				all_user_map.put(users[1], new Node(users[1]));
				all_users.add(all_user_map.get(users[1]));
			}
			if (!all_edge_map.keySet().contains(users[0]+users[1]) &&
				!all_edge_map.keySet().contains(users[1]+users[0])) {
				all_edge_map.put(users[0]+users[1],
								 new Edge(all_user_map.get(users[0]), all_user_map.get(users[1])));
				Edge new_edge = all_edge_map.get(users[0]+users[1]);
				all_edge_map.put(users[1]+users[0], new_edge); // reuse edge
				all_edges.add(new_edge);
				all_user_map.get(users[0]).edges.add(new_edge);
				all_user_map.get(users[1]).edges.add(new_edge);
			}
		}
		in.close();
		return all_users.size();
	}
	/*
	 * Create a tree using user i as root
	 */
	private int create_tree(Node root) {
		Queue<Node> q = new LinkedList<Node>();
		q.offer(root);
		root.levelInTree = 0;
		root.num_shortest_path = 1;
		
		
		int curLevel = 0;
		Node cur = null;
		while (!q.isEmpty()) {
			cur = q.poll();
			curLevel = cur.levelInTree;
			int num_neigh = cur.edges.size();
			Node neighbor = null;
			for (int i = 0; i < num_neigh; i++) {
				neighbor = cur.edges.get(i).nodes[1];
				if (cur.equals(neighbor)) {
					neighbor = cur.edges.get(i).nodes[0];
				}
				if (neighbor.levelInTree < curLevel + 1) {
					continue;
				}
				if (neighbor.levelInTree == Integer.MAX_VALUE) {
					q.offer(neighbor);
					neighbor.levelInTree = curLevel + 1;
				}
				cur.children.add(neighbor);
				neighbor.parents.add(cur);
				neighbor.num_shortest_path++;
			}
		}
		return curLevel;
	}
	
	/*
	 * Update the betweeness score of edge and node credit bottom up.
	 */
	// TODO: remember to divided by 2 during the computation
	private void update_edge_betweeness(Node t) {
		Queue<Node> q = new LinkedList<Node>();
		q.offer(t);
		Stack<Node> s = new Stack<Node>();
		s.push(t);
		t.inComputeCredit = true;
		
		while (!q.isEmpty()) {
			Node n = q.poll();
			int num_children = n.children.size();
			for (int i = 0; i < num_children; i++) {
				Node child = n.children.get(i);
				if (!child.inComputeCredit) {
					q.offer(child);
					s.push(child);
					child.inComputeCredit = true;
				}
			}
		}
		
		Node cur = null;
		while (!s.isEmpty()) {
			cur = s.pop();
			// update non leaf node credit
			int num_children = cur.children.size();
			if (num_children > 0) {
				double out_edge_credit_sum = 0;
				for (int i = 0; i < num_children; i++) {
					Edge e = all_edge_map.get(cur.name + cur.children.get(i).name);
					if (e == null) {
						e = all_edge_map.get(cur.children.get(i).name + cur.name);
					}
					out_edge_credit_sum += e.credit;
				}
				cur.credit = out_edge_credit_sum + 1.0;
			} else {
				cur.credit = 1.0;
			}
			// update edge betweeness
			int num_parents = cur.parents.size(), sum = 0;
			for (int i = 0; i < num_parents; i++) {
				sum += cur.parents.get(i).num_shortest_path;
			}
			for (int i = 0; i < num_parents; i++) {
				Node parent = cur.parents.get(i);
				double p = ((double) parent.num_shortest_path) / (double)sum;
				Edge e = all_edge_map.get(cur.name + parent.name);
				if (e == null) {
					e = all_edge_map.get(parent.name + cur.name);
				}
				e.credit = cur.credit * p;
				e.betweeness += e.credit / 2;
			}
		}
	}
	
	/*
	 * Clear num_shortest_path, credit from nodes, children and parents.
	 * Keep the betweeness score of edges.
	 */
	private void clear_tree() {
		Iterator<Node> it = all_users.iterator();
		while (it.hasNext()) {
			Node n = it.next();
			n.num_shortest_path = 0;
			n.credit = 0;
			n.levelInTree = Integer.MAX_VALUE;
			n.inComputeCredit = false;
			n.children.clear();
			n.parents.clear();
		}
	}
	
	public void compute_betweeness(int num_users) {
		Iterator<Node> it = all_users.iterator();
		while (it.hasNext()) {
			Node root = it.next();
			create_tree(root);
			//if (root.name.equals("Gary")) levelPrintTree(root);
			update_edge_betweeness(root);
			clear_tree();
		}
	}
	
	private void traverse_graph() {
		ArrayList<Set<Node>> community = new ArrayList<Set<Node>>();
		Set<Node> group = null;
		Iterator<Node> it = all_users.iterator();
		while (it.hasNext()) {
			Node user = it.next();
			if (user.inCommunity) {
				continue;
			}
			group = new HashSet<Node>();
			group.add(user);
			Queue<Node> q = new LinkedList<Node>();
			q.offer(user);
			
			Node cur = null;
			while (!q.isEmpty()) {
				cur = q.poll();
				int num_neigh = cur.edges.size();
				for (int i = 0; i < num_neigh; i++) {
					if (cur.edges.get(i).betweeness == -1) {
						continue;
					}
					Node neighbor = cur.edges.get(i).nodes[1];
					if (neighbor.equals(cur)) {
						neighbor = cur.edges.get(i).nodes[0];
					}
					if (!group.contains(neighbor)) {
						group.add(neighbor);
						q.offer(neighbor);
						neighbor.inCommunity = true;
					}
				}
			}
			community.add(group);
		}
		int community_size = communities.size();
		if (community_size == 0 || community.size() > communities.get(community_size-1).size()) {
			communities.add(community);
		}
		it = all_users.iterator();
		while (it.hasNext()) {
			it.next().inCommunity = false;
		}
	}
	
	private void delete_max_betweeness_edges(double max_betweeness) {
		Iterator<Edge> it = all_edges.iterator();
		while (it.hasNext()) {
			Edge e = it.next();
			if (max_betweeness < e.betweeness) {
				max_betweeness = e.betweeness;
			}
		}
		it = all_edges.iterator();
		while (it.hasNext()) {
			Edge e = it.next();
			if (max_betweeness == e.betweeness) {
				e.betweeness = -1;
			}
		}
	}

	public void output_communities() {
		int num_communities = 0;
		double max_betweeness = 0;
		do {
			traverse_graph();
			num_communities = communities.size();
			if (communities.get(num_communities-1).size() == all_users.size()) {
				break;
			}
			delete_max_betweeness_edges(max_betweeness);
		} while (true);
		
		num_communities = communities.size();
		for (int i = 0; i < num_communities; i++) {
			ArrayList<Set<Node>> community = communities.get(i);
			int num_groups = community.size();
			System.out.print(Integer.toString(num_groups) + " cluster: ");
			for (int j = 0; j < num_groups; j++) {
				System.out.print("(");
				Set<Node> group = community.get(j);
				Iterator<Node> it = group.iterator();
				StringBuilder sb = new StringBuilder();
				while (it.hasNext()) {
					sb.append(it.next() + ", ");
				}
				sb.delete(sb.length()-2, sb.length());
				sb.append(")");
				System.out.print(sb.toString());
				if (j < num_groups - 1) {
					System.out.print(',');
				}
			}
			System.out.println("");
		}
		//System.out.println(communities);
	}
	
	@SuppressWarnings("unused")
	private void check_input() {
		int len = inputs.size();
		for (int i = 0; i < len; i++)
			System.out.println(inputs.get(i).get(0) + " - " + inputs.get(i).get(1));
	}
	@SuppressWarnings("unused")
	private void test_obj(ArrayList<Node1> l) {
		Node1 n = new Node1(-1);
		l.add(n);
		System.out.println(l.get(0).a);
		//Node1 n = l.get(0);
		n.a = 2;
		System.out.println(l.get(0).a);
	}
	@SuppressWarnings("unused")
	private void printGraph() {
		Iterator<Node> it = all_users.iterator();
		while (it.hasNext()) {
			Node n= it.next();
			System.out.print(n);
			System.out.println(": ");
			int num_neighbors = n.edges.size();
			for (int i = 0; i < num_neighbors; i++) {
				System.out.print(n.edges.get(i));
				System.out.println(" ");
			}
		}
	}
	@SuppressWarnings("unused")
	private void levelPrintTree(Node root) {
		Queue<Node> q = new LinkedList<Node>();
		Queue<Integer> level = new LinkedList<Integer>();
		q.offer(root);
		level.offer(0);
		
		int curLevel = 0;
		Node cur = null;
		while (!q.isEmpty()) {
			cur = q.poll();
			curLevel = level.poll();
			System.out.print(cur + Integer.toString(cur.num_shortest_path) + " ");
			int num_neigh = cur.children.size();
			Node children = null;
			for (int i = 0; i < num_neigh; i++) {
				children = cur.children.get(i);
				q.offer(children);
				level.offer(curLevel + 1);
			}
			if (!level.isEmpty() && curLevel < level.peek()) {
				System.out.println("");
			}
		}
		System.out.println("\n");
	}
	@SuppressWarnings("unused")
	private void printEdgeBetweeness() {
		Iterator<Edge> it = all_edges.iterator();
		while (it.hasNext()) {
			Edge e = it.next();
			System.out.println(e);
		}
	}
	public static void main(String[] args) {
		Betweeness b = new Betweeness();
		int num_users = b.read_input_construct_graph();
		//b.check_input();
		//b.printGraph();
		if (num_users == -1) {
			return;
		}
		b.compute_betweeness(num_users);
		//b.printEdgeBetweeness();
		b.output_communities();
	}

}
