# http://www.reddit.com/r/dailyprogrammer/comments/1sob1e/121113_challenge_144_easy_nuts_bolts/

# This was more readable to me than getting fancy with blocks

input = ARGF.read.split("\n")

n = input.shift.to_i
track = {}
n.times do
  k, v = input.shift.split
  track[k] = v.to_i
end

n.times do
  k, v = input.shift.split
  diff = v.to_i - track[k]
  puts "#{k}#{diff > 0 ? ' +' : ' '}#{diff}" unless diff == 0
end
