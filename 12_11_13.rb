# http://www.reddit.com/r/dailyprogrammer/comments/1sob1e/121113_challenge_144_easy_nuts_bolts/

# This was more readable to me than getting fancy with blocks

input = ARGF.read.split("\n")

n = input.shift.to_i - 1

track = {}
for i in 0..n do
  k,v = input.shift.split
  track[k] = v.to_i
end

for i in 0..n do
  k,v = input.shift.split
  diff = v.to_i - track[k]
  puts "%s%s%s" % [k, diff > 0 ? " +" : " ", diff] unless diff == 0 
end
  
