def solution(board):
    """Returns the shortest path from the top-left of the map to the bottom-right with removing one wall

    :param board: the map, with 0's representing open positions and 1's representing walls
    :return: an int representing the shortest path
    """
    path_finder = PathFinder(board)
    return path_finder.find_shortest_path()


class PathFinder:
    """A class to help navigate the map"""

    def __init__(self, board):
        #  Convert the board to Positions with their respective values
        self.board = [[Position(x, y, board[x][y]) for y in range(len(board[x]))] for x in range(len(board))]
        #  Define the destination as the bottom-right position
        self.dest = self.board[len(board) - 1][len(board[0]) - 1]

    def find_shortest_path(self):
        """Finds the shortest path to the destination with having one wall removed

        :return: an int representing the shortest path
        """
        walls = self.get_all_walls()
        #  Apply the shortest path algorithm to every map variation and return the minimum
        return min(map(lambda x: self.get_shortest_path({self.board[0][0]}, x, 1, set()), walls))

    def get_all_walls(self):
        """Returns a list of Positions representing every wall in the map

        :return: a list of Positions
        """
        return [self.board[x][y] for x in range(len(self.board))
                for y in range(len(self.board[x])) if self.board[x][y].value == 1]

    def get_shortest_path(self, positions, open_wall, hops, taken):
        """Returns the shortest path to the destination

        :param positions: the list of possible current Positions given the number of hops
        :param open_wall: the Position representing a wall to remove from the map
        :param hops: the current number of hops performed
        :param taken: the set of Positions already visited
        :return: the least number of hops to reach the destination
        """
        #  Check if we have reached the destination
        if self.dest in positions:
            return hops
        #  Generate all Positions reachable from any of the current Positions
        next_positions = {position for position_list in
                          map(lambda x: self.get_next_positions(x, open_wall, taken), positions)
                          for position in position_list}
        #  If the list of next Positions is empty, we have reached a dead end. Return the max number
        if len(next_positions) == 0:
            return float('inf')
        #  Add the current Positions to the set of Positions visited
        taken |= positions
        #  Recursively find the path to the destination
        return self.get_shortest_path(next_positions, open_wall, hops + 1, taken)

    def get_next_positions(self, position, open_wall, taken):
        """Returns a list of Positions reachable from the given Position

        :param position: the Position to check
        :param open_wall: the Position representing a wall to remove from the map
        :param taken: the set of Positions already visited
        :return: the list of Positions reachable from the given Position
        """
        return [self.board[position.x + x][position.y + y] for y in range(-1, 2) for x in range(-1, 2)
                if self.validate_position(position, x, y, open_wall, taken)]

    def validate_position(self, position, x, y, open_wall, taken):
        """Checks whether the Position is a valid Position with the x and y increments applied

        :param position: the starting Position
        :param x: the number to move the Position in the x direction
        :param y: the number to move the Position in the y direction
        :param open_wall: the Position representing a wall to remove from the map
        :param taken: the set of Positions already visited
        :return: True or False if the new Position is valid
        """
        if 0 in [x + y, x - y] or position.x + x not in range(len(self.board)) or \
                position.y + y not in range(len(self.board[0])):
            return False
        next_position = self.board[position.x + x][position.y + y]
        return next_position not in taken and (next_position.value == 0 or next_position == open_wall)


class Position:
    """A class to represent a spot on the board with the given value"""

    def __init__(self, x, y, value):
        self.x = x
        self.y = y
        self.value = value
