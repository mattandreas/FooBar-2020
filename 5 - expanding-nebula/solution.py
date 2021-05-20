from collections import defaultdict


def solution(cell_matrix):
    """Finds the count of all possible previous grids that would generate the current grid after one time step.

    :param cell_matrix: the current grid
    :return: the count of valid previous states
    """
    generator = StateGenerator(cell_matrix)
    return generator.count_previous_states()


class StateGenerator:

    def __init__(self, current_matrix):
        """ Initialize the StateGenerator. If the width is greater than the height, switch the rows and columns.
        This will allow us to iterate over arrays of shorter length which will result in a smaller number of
        possible combinations per iteration. Also convert the inner arrays to tuples to allow entire rows
        to be compared in a single comparison.

        :param current_matrix: the current grid of gas states
        """
        self.current_matrix = (zip(*current_matrix) if len(current_matrix[0]) > len(current_matrix) else
                               map(tuple, current_matrix))
        self.known_previous_rows = {}

    def count_previous_states(self):
        """Returns the total number of possible previous grids that would result in the current grid
        after one time step.

        :return: the total number of previous grids
        """
        lower_row_counts = defaultdict(lambda: 1)
        #  Iterate through all of the possible width x 2 grids that would result in each row in the current matrix
        for previous_rows in (self.get_previous_rows(row) for row in self.current_matrix):
            upper_row_counts = defaultdict(lambda: 0)
            for row in previous_rows:
                #  If the upper row matches a lower row in the grid above, then that grid is a valid continuation
                upper_row_counts[row[1]] += lower_row_counts[row[0]]
            lower_row_counts = upper_row_counts
        return sum(lower_row_counts.values())

    def get_previous_rows(self, current_row):
        """Returns all width x 2 grids that would result in a single row in the current grid.

        :param current_row: the current row to check
        :return: an array of width x 2 grids
        """
        if current_row in self.known_previous_rows:
            #  If we have already computed all previous grids for this row, return the saved result
            return self.known_previous_rows[current_row]
        #  Initialize a list of all combinations of True and False to add on the end of the grid
        row_extensions = [(first, second) for first in (True, False) for second in (True, False)]
        #  Initialize all possible previous grids with just the row extensions
        previous_rows = [(extension,) for extension in row_extensions]
        for state in current_row:
            #  Match every previous grid with every row extension
            previous_rows = [row + (column,) for row in previous_rows for column in row_extensions if
                             #  If it results in a valid previous 2 x 2 grid for the given state, add the new grid
                             (row[-1].count(True) + column.count(True) == 1) == state]
        #  Switch the rows and columns to allow entire rows to be compared in a single comparison
        previous_rows = self.known_previous_rows[current_row] = [zip(*row) for row in previous_rows]
        return previous_rows
