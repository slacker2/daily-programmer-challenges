# http://www.reddit.com/r/dailyprogrammer/comments/1s6484/120513_challenge_138_intermediate_overlapping/

# Don't do math late at night.

x0, y0, x1, y1 = gets.split.map { |i| i.to_f }

od = Math.hypot((x0 - x1), (y0 - y1))

output = od >= 2 ? 2 * Math::PI : 2 * Math::PI - 2 * Math.acos(od / 2) + od / 2 * Math.sqrt(4 - od**2)

puts output.round(4)
