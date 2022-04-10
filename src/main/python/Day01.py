from itertools import pairwise
input_file = open('input.txt', 'r')
input_data = [int(line.strip()) for line in input_file.readlines()]

def triplewise(iterable):
    """Return overlapping triplets from an iterable. From itertools recipes."""
    for (a, _), (b, c) in pairwise(pairwise(iterable)):
        yield a, b, c

def count_increases(iterable):
    """Count the number of times an adjacent subsequent element increases in the input."""
    return sum(pair[0] < pair[1] for pair in pairwise(iterable))

count_increases(input_data) # part 1
count_increases(map(sum, triplewise(input_data))) # part 2