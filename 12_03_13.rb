# http://www.reddit.com/r/dailyprogrammer/comments/1s061q/120313_challenge_143_easy_braille/

# The hardest part about this was creating the alphabet

input = ARGF.read.split
n = input.length / 3

alphabet = {
            'O.....' => 'a',
            'O.O...' => 'b',
            'OO....' => 'c',
            'OO.O..' => 'd',
            'O..O..' => 'e',
            'OOO...' => 'f',
            'OOOO..' => 'g',
            'O.OO..' => 'h',
            '.OO...' => 'i',
            '.OOO..' => 'j',
            'O...O.' => 'k',
            'O.O.O.' => 'l',
            'OO..O.' => 'm',
            'OO.OO.' => 'n',
            'O..OO.' => 'o',
            'OOO.O.' => 'p',
            'OOOOO.' => 'q',
            'O.OOO.' => 'r',
            '.OO.O.' => 's',
            '.OOOO.' => 't',
            'O...OO' => 'u',
            'O.O.OO' => 'v',
            '.OOO.O' => 'w',
            'OO..OO' => 'x',
            'OO.OOO' => 'y',
            'O..OOO' => 'z'
           }

output = ''
n.times do |i|
  letter = input[i] + input[i + n] + input[i + 2 * n]
  output = output + alphabet[letter]
end

puts output
