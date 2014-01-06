# http://www.reddit.com/r/dailyprogrammer/comments/1sody4/12113_challenge_139_intermediate_telephone_keypads/

# Brevity

alphabet = { '2' => %w(a b c),
             '3' => %w(d e f),
             '4' => %w(g h i),
             '5' => %w(j k l),
             '6' => %w(m n o),
             '7' => %w(p q r s),
             '8' => %w(t u v),
             '9' => %w(w x y z)
           }

re = '^'
ARGF.read.split.each { |l| word << alphabet[l[0]][l.size - 1] }
regex = Regexp.new(re)
open('brit-a-z.txt', 'r:ASCII-8BIT').readlines.each { |word| puts word if word.match(regex) }
