# http://www.reddit.com/r/dailyprogrammer/comments/1t6dlf/121813_challenge_140_intermediate_adjacency_matrix/

# Shooting for readability and style convention

def main()
  args = get_input
  directed_graph = create_directed_graph(args.fetch(:raw_edge_list))
  display_adjacency_matrix(args.fetch(:num_nodes), directed_graph)
end

def get_input
  input = ARGF.read().split("\n")
  n, m = input[0].split.map { |i| i.to_i }
  args = { raw_edge_list: input[1..m],
           num_nodes: n }
end

def create_directed_graph(raw_edge_list)
  graph = {}
  graph = add_raw_edges_to_graph(raw_edge_list, graph)
  return graph
end

def add_edges_to_graph(sources, dests, graph)
  sources.split.each do |source|
    graph[source.to_i] ||= []
    dests.split.each { |dest| graph[source.to_i] << dest.to_i }
  end
  return graph
end

def add_raw_edges_to_graph(raw_edge_list, graph)
  raw_edge_list.each do |raw_edge| 
    sources, dests = raw_edge.chomp.split('->') 
    graph = add_edges_to_graph(sources, dests, graph)
  end
  return graph
end

def display_adjacency_matrix(num_nodes, graph)
  matrix = create_adjacency_matrix(num_nodes, graph)
  num_nodes.times { |i| puts matrix[i].join }
end

def create_adjacency_matrix(num_nodes, graph)
  matrix = create_zero_initialized_matrix(num_nodes)
  graph.each do |source, dests|
    dests.each { |dest| matrix[source][dest] = 1 }
  end
  return matrix
end

def create_zero_initialized_matrix(num_nodes)
  matrix = []
  num_nodes.times { matrix << Array.new(num_nodes, 0) }
  return matrix
end

main()
