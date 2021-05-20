def solution(num_bunnies, num_required):
    """Returns a scheme describing which bunnies get which keys

    :param num_bunnies: the total number of bunnies available
    :param num_required: the number of bunnies required to open a door
    :return: the scheme of keys to give to each bunny
    """
    allocator = KeyAllocator(num_bunnies, num_required)
    return allocator.assign_keys()


class KeyAllocator:
    """A class to kelp assign keys to the bunnies"""

    def __init__(self, num_bunnies, num_required):
        self.num_bunnies = num_bunnies
        self.num_required = num_required
        #  Create an empty list for each bunny to hold the keys
        self.allocations = [[] for _ in range(num_bunnies)]
        self.allocator = Allocator(num_bunnies, num_required)

    def assign_keys(self, key=0):
        """Assigns the keys to each bunny and returns the scheme

        :param key: the current key to assign
        :return: the scheme of keys
        """
        #  Loop through the current allocator values
        for index, should_receive in self.allocator.get_values():
            if not should_receive:
                continue
            #  Assign the current key to the bunny at index in the allocations
            self.allocations[index].append(key)
        if not self.allocator.increment_allocator():
            #  If the allocator is unable to increment, then we have assigned all of the keys
            return self.allocations
        #  Recursively assign the remaining keys
        return self.assign_keys(key=key + 1)


class Allocator:
    """A class to help decide which bunnies to assign a key"""

    def __init__(self, num_bunnies, num_required):
        self.num_bunnies = num_bunnies
        self.num_required = num_required
        #  Define an array with each index representing a different bunny
        self.model = self.init_model()

    def init_model(self):
        """Returns a list representing which bunny to assign a specific key.
        If the value at an index is 1, then we should assign a key to that bunny, else do not assign it

        :return: a list of values
        """
        #  The limit is the number of bunnies that will receive a specific key
        limit = 0 if self.num_required == 0 else self.num_bunnies - self.num_required + 1
        return [1 if i < limit else 0 for i in range(self.num_bunnies)]

    def get_values(self):
        """Generates tuples containing an index representing a bunny,
        and a value representing if they should receive a key or not

        :return:
        """
        for index in range(len(self.model)):
            yield index, self.model[index] == 1

    def increment_allocator(self):
        """Attempts to increment the allocator and returns if the operation was successful.
        The allocator is incremented by moving the right-most 1 to the right. Once that 1 gets to the end,
        the second-right-most 1 moves to the right, and every 1 to the right of the second-right-most gets pushed
        to the left. The incrementer is fully incremented if all 1s are pushed all the way to the right

        :return: True if the allocator was successfully incremented, else False if the allocator is full
        """
        #  Find the index of the right-most 1 that is allowed to increment
        index_to_increment = self.find_index()
        if index_to_increment == -1:
            #  No 1s are able to be incremented, allocator is full
            return False
        #  Swap the 1 to be incremented with the 0 to the right of it
        self.model[index_to_increment] = 0
        self.model[index_to_increment + 1] = 1
        #  Check if there is a zero to the right of the 1, meaning we may have to pull other 1s to the left
        if index_to_increment + 2 != len(self.model) and self.model[index_to_increment + 2] != 1:
            #  Push all 1s that are to the right of the 1 that was incremented to the left
            self.pull_forward_from(index_to_increment + 2)
        return True

    def find_index(self):
        """Finds the index of the 1 to increment

        :return: the index of the 1 to increment if it exists, else -1
        """
        #  A 1 can only increment if there is a 0 to the right of it. Define a flag
        passed_zero = False
        #  Loop through the allocator from the right to left
        for i in range(len(self.model) - 1, -1, -1):
            if self.model[i] == 0:
                #  We have passed a zero, set the flag
                passed_zero = True
            elif passed_zero:
                #  We have found a 1 after passing a 0, return the index
                return i
        #  No 1s can be incremented, return -1
        return -1

    def pull_forward_from(self, index):
        """Pulls all 1s to the right of the index to the left

        :param index: the starting index to check for 1s
        """
        for i in range(index + 1, len(self.model)):
            if self.model[i] == 0:
                continue
            #  We have found a 1. Swap it with the 0 at the defined index
            self.model[index] = 1
            self.model[i] = 0
            #  Increment the index
            index += 1
