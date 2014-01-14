# http://www.reddit.com/r/dailyprogrammer/comments/1undyd/010714_challenge_147_easy_sport_points/

score = ARGV[0].to_i
puts (score <= 1 || score == 2 || score == 4 || score == 5 ? 'Invalid Score' : 'Valid Score')
