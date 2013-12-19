# http://www.reddit.com/r/dailyprogrammer/comments/1rnrs2/112813_challenge_137_intermediate_hard_banquet/

# I tried to focus on readability in this one. I broke the style guide a number of times, but
# I don't think it's too bad.


require 'set'

def main()
  args = get_input
  food_relationship_graph = create_food_relationship_graph( args.fetch(:food_relationships), 
                                                           args.fetch(:food_items))

  fail "Impossible order list!" if cycle_in_graph?(food_relationship_graph)
  order = get_order_from_graph(food_relationship_graph)
  display_order(order)
  display_warnings(args.fetch(:food_items), food_relationship_graph) 
end


def get_input
  input = ARGF.read().split("\n")
  n, m = input[0].split.map { |i| i.to_i }
  args = { food_items: input[1..n], food_relationships: input[n+1..n+m] } 
end


def create_food_relationship_graph(relationships,food_items)
  graph = {}
  relationships.each { |relationship| graph = add_relationship_to_graph(relationship, food_items, graph) }
  return graph
end


def add_relationship_to_graph(relationship, food_items, graph)
  before_regex, after_regex = relationship.split.map { |r| Regexp.new(r.sub('*', '.*')) }

  food_items.combination(2).each do |item1, item2| 
    graph[item1] ||= Set.new if item1.match(before_regex) || item1.match(after_regex)
    graph[item2] ||= Set.new if item2.match(before_regex) || item2.match(after_regex)

    graph[item1] << item2 if item1.match(before_regex) && item2.match(after_regex)
    graph[item2] << item1 if item2.match(before_regex) && item1.match(after_regex)
  end

  return graph
end


def cycle_in_graph?(graph)
  graph.to_a.combination(2).each.first do |item1, item2| 
    return true if direct_to_each_other?(item1, item2, graph)
  end
  return false
end


def direct_to_each_other?(item1, item2, graph)
  graph[item1].include?(item2) && graph[item2].include?(item1)
end

 
def order_insert(order, graph, index, key) 
  ambiguous?(order[index]) ? insert_on_ambiguous_index(order,graph,index,key) : insert_on_unambiguous_index(order,graph,index,key)
end


def insert_on_ambiguous_index(order, graph, index, key)
  key_before_a_subitem?(key, order[index], graph) ? order.insert(index,key) : order[index] << key
  return order
end


def insert_on_unambiguous_index(order, graph, index, key)
  if keys_are_ambiguous?(order[index], key, graph) 
    order[index] = [order[index],key] 
  else 
    order.insert(index,key) 
  end
  return order
end


def keys_are_ambiguous?(key1, key2, graph)
  return !key1.nil? && !key2.nil? && !graph[key1].include?(key2) && !graph[key2].include?(key1)
end


def ambiguous?(item) item.is_a?(Array) end


def comes_before?(item1, item2, graph)
  graph[item1].include?(item2)
end


def find_index_and_insert(key, order, graph)
  insert_index = 0
  order.each do |item|
    if ambiguous?(item)
       key_before = key_before_a_subitem?(key, item, graph)
       key_after = key_after_a_subitem?(key, item, graph)
     
       return reinsert_items(key, item, order, graph) if key_before && key_after
       insert_index = order.index(item) + 1 if key_after
    else 
      insert_index = order.index(item) + 1 if graph[item].include?(key)
    end
  end

  order_insert(order, graph, insert_index, key)
end

def reinsert_items(key, subitems, order, graph)
  find_index_and_insert(key, order, graph)
  subitems.each { |subitem| find_index_and_insert(subitem) } 
end


def key_before_a_subitem?(key, subitems, graph)
  subitems.each { |subitem| return true if graph[key].include?(subitem) }
  return false
end


def key_after_a_subitem?(key, subitems, graph)
  subitems.each { |subitem| return true if graph[subitem].include?(key) }
  return false
end


def get_order_from_graph(graph)
  order = []
  graph.each { |key, _| order = find_index_and_insert(key, order, graph) }
  return order
end


def display_order(order)
  order.each do |item| 
    if ambiguous?(item) 
      puts "#{order.index(item) + 1}. #{item.sort.join(", ")}" 
    else
      puts "#{order.index(item) + 1}. #{item}"
    end
  end 
end


def display_warnings(food, graph)
  food.each { |f| puts "Warning: #{f} does not have any ordering" unless graph.key?(f) }
end


main()
