def generate_primes():
    prime_list = [2]
    prime_str_length = 1

    num_to_check = 3

    while prime_str_length < 10005:
        # Add primes to the prime list.
        for prime in prime_list:
            # Check if the number is not prime by checking if it is a multiple of any previous prime.
            if num_to_check % prime == 0:
                # Number is divisible by a prime, do not include.
                break
        else:
            prime_list.append(num_to_check)
            prime_str_length += len(str(num_to_check))

        # Increment number to check by 2 to exclude even numbers
        num_to_check += 2

    return ''.join(map(str, prime_list))


prime_str = generate_primes()


def solution(index):
    return ''.join([prime_str[i] for i in range(index, index + 5)])
