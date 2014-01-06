# http://www.reddit.com/r/dailyprogrammer/comments/1r2mcz/112013_challenge_136_intermediate_ranked_voting/

input = ARGF.read.split("\n")
n, m = input[0].split.map { |i| i.to_i }
candidates = input[1].split
votes = input[2..m+3].map { |i| i.split.map { |k| k.to_i } }
votes = votes.transpose

candidate_votes = Hash[candidates.size.times.map { |c| [c, 0] }]
m.times do |i|
  votes[i].each { |k| candidate_votes[k] += 1 }
  puts candidate_votes.to_s
end
