size = 8  # The length and width of the board


def solution(src, dest):
    solve = Solution(src, dest)
    return solve.solve()


def generate_board():
    """Generate the board of values

    :return: a 2D array representing the board of values
    """
    board = []
    for x in range(size):
        row = []
        for y in range(size):
            row.append(Position(x, y))
        board.append(row)
    return board


class Solution:

    def __init__(self, src, dest):
        self.src = src
        self.dest = dest
        self.board = generate_board()
        self.visited = set()

    def solve(self):
        """Solves the board from src to dest

        :return: the lowest number of hops from src to dest
        """
        start_position = self.find_position(self.src)
        return self.find_least_path({start_position}, 0)

    def find_position(self, value):
        """Finds the Position with the given value

        :param value: The value of the Position to find
        :return: the Position on the board with the given value
        """
        for row in self.board:
            for position in row:
                if position.value == value:
                    return position

    def find_least_path(self, positions, current_hops):
        """Finds the lowest number of hops to dest

        :param positions: the possible Positions reachable given the current number of hops
        :param current_hops: the current number of hops performed
        :return: the lowest number of hops to dest from any of the Positions
        """
        # Check if any of the current Positions' value equal dest
        if self.dest in map(lambda pos: pos.value, positions):
            return current_hops
        current_hops += 1
        self.visited |= positions
        # Generate the next possible Positions from any of the current Positions
        next_positions = {pos for pos_list in map(self.get_next_positions, positions) for pos in pos_list}
        # Recursively find the lowest number of hops
        return self.find_least_path(next_positions, current_hops)

    def get_next_positions(self, position):
        """Generate all reachable positions from the given Position

        :param position: the current position
        :return: list of Positions reachable from the current Position
        """
        return [self.board[position.x + x][position.y + y] for x in range(-2, 3) for y in range(-2, 3)
                if self.validate_position(position, x, y)]

    def validate_position(self, position, x, y):
        """Check whether the Position translated with the given x-increment and y-increment is valid

        :param position: the Position to validate
        :param x: value to translate the Position in the x direction
        :param y: value to translate the Position in the y direction
        :return: True or False if the Position is valid
        """
        return (0 not in [x, y, x + y, x - y] and position.x + x in range(size) and position.y + y in range(size) and
                self.board[position.x + x][position.y + y] not in self.visited)


class Position:
    """A class to represent a spot on the board with the given value"""

    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.value = x * size + y
