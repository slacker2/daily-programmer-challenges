# http://www.reddit.com/r/dailyprogrammer/comments/1tixzk/122313_challenge_146_easy_polygon_perimeter/

n, r = ARGF.read.split.map(&:to_f)
puts (n*r*2*Math.sin(Math::PI / n)).round(3)
