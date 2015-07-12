# http://www.reddit.com/r/dailyprogrammer/comments/3840rp/20150601_challenge_217_easy_lumberjack_pile/

input = """ 4
200
15 12 13 11 
19 14  8 18 
13 14 17 15 
 7 14 20  7""".split("\n")
#size = input[0].strip.to_i
num_logs = input[1].strip.to_i
piles = input[2..-1].map { |pile| pile.split(' ').map { |i| i.to_i } }

smallest = piles[0].min
piles.each { |row|
  smallest = row.min if row.min < smallest
}

while (num_logs > 0)
  piles.map! { |row|
    row.map! { |col|
      if (col == smallest && num_logs > 0)
        num_logs -= 1
        col += 1
      else
        col
      end
    }
  }
  smallest += 1
end

puts piles.map { |row| row.join(" ") }.join("\n")

