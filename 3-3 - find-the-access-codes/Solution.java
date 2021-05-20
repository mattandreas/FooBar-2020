public class Solution {

    /**
     * Returns the number of triples in the array of numbers.
     *
     * @param numbers the array of numbers to check
     * @return the number of triples
     */
    public static int solution(int[] numbers) {
        int result = 0;
        for (int i = 0; i < numbers.length - 2; i++) {
            for (int j = i + 1; j < numbers.length - 1; j++) {
                for (int k = j + 1; k < numbers.length; k++) {
                    // Check if j is divisible by i and k is divisible by j.
                    if (numbers[j] % numbers[i] != 0 || numbers[k] % numbers[j] != 0) continue;
                    result++;
                }
            }
        }
        return result;
    }

}
