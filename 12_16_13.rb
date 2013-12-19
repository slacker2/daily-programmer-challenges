# http://www.reddit.com/r/dailyprogrammer/comments/1t0r09/121613_challenge_145_easy_tree_generation/

# I'm mostly going for brevity in this one.

n, b, t = gets.split
n = n.to_i

# for i in 0..n do puts ' '*((n-i)/2) + t*i unless i.even? end
# puts ' '*((n-3)/2) + b*3 + ' '*((n-3)/2)

# Done better
(1..n).step(2) { |i| puts ' ' * ((n - i) / 2) + t * i }
puts ' ' * ((n - 3) / 2) + b * 3
