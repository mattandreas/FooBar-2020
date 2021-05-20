import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Deque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// implementation based on https://franklinvp.github.io/2020-06-05-PolyaFooBar/

public class Solution {

    /**
     * Returns the count of unique states able to be produced from a grid
     *
     * @param width  the width of the grid
     * @param height  the height of the grid
     * @param size  the max value of any single integer in the grid
     * @return  the count of unique states
     */
    public static String solution(int width, int height, int size) {
        StateGenerator stateGenerator = new StateGenerator(width, height, size);
        return stateGenerator.getUniqueStates();
    }

    private static final class StateGenerator {

        private final int width;

        private final int height;

        private final int size;

        private final int[][] gcdTable;

        private final int[] factorialTable;

        public StateGenerator(int width, int height, int size) {
            this.width = width;
            this.height = height;
            this.size = size;
            int max = Math.max(width, height);
            gcdTable = buildGcdTable(max);
            factorialTable = buildFactorialTable(max);
        }

        /**
         * Returns the count of unique states
         * @return  The count in String format
         */
        public String getUniqueStates() {
            // Produce the cartesian product of every row and column permutation and cycle count
            return getPartitionAndCycleCount(width).entrySet().stream()
                    .map(widthEntry -> getPartitionAndCycleCount(height).entrySet().stream()
                            .map(heightEntry -> getResult(widthEntry, heightEntry))
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    // Divide by the width factorial * height factorial
                    .divide(BigDecimal.valueOf((long) factorialTable[width - 1] * factorialTable[height - 1]),
                            0, RoundingMode.FLOOR).toString();
        }

        /**
         * Returns the result of combining two permutation and cycle counts
         * @param widthEntry  the first permutation and cycle count
         * @param heightEntry  the second permutation and cycle count
         * @return  the result of combining the two permutation and cycle counts
         */
        private BigDecimal getResult(Map.Entry<int[], Integer> widthEntry,
                                     Map.Entry<int[], Integer> heightEntry) {
            return BigDecimal.valueOf((long) widthEntry.getValue() * heightEntry.getValue())
                    .multiply(BigDecimal.valueOf(size).pow(Arrays.stream(widthEntry.getKey())
                            .map(w -> Arrays.stream(heightEntry.getKey())
                                    .map(h -> gcdTable[w - 1][h - 1]).sum()).sum()));
        }

        /**
         * Returns the cycle counts of all permutation groups via Polya's enumeration theorem
         * @param num  the size of the array
         * @return  Map of all permutation groups and their cycle count
         */
        private Map<int[], Integer> getPartitionAndCycleCount(int num) {
            Map<int[], Integer> result = new HashMap<>();
            int[] a = IntStream.range(0, num + 1).toArray();
            int k = 1;
            int y = num - 1;
            while (k != 0) {
                int x = a[k - 1] + 1;
                k -= 1;
                while (x << 1 <= y) {
                    a[k] = x;
                    y -= x;
                    k += 1;
                }
                int l = k + 1;
                while (x <= y) {
                    a[k] = x;
                    a[l] = y;
                    int[] partition = Arrays.copyOfRange(a, 0, k + 2);
                    result.put(partition, getCoefficientFactor(partition, num));
                    x += 1;
                    y -= 1;
                }
                a[k] = x + y;
                y = x + y - 1;
                int[] partition = Arrays.copyOfRange(a, 0, k + 1);
                result.put(partition, getCoefficientFactor(partition, num));
            }
            return result;
        }

        /**
         * Returns the cycle count of the specified array
         * @param partitions  the partition array
         * @param num  the size of the array the partition is from
         * @return  the cycle count
         */
        private int getCoefficientFactor(int[] partitions, int num) {
            return Arrays.stream(partitions).boxed().collect(Collectors.toMap(i -> i, i -> 1, Integer::sum))
                    .entrySet().stream().reduce(factorialTable[num - 1], this::accumulator, Integer::sum);
        }

        /**
         * Returns the new subtotal of the coefficient factor with the next value added on
         * @param subtotal  the current total of the coefficient factor
         * @param next  the next value to add on
         * @return  the new subtotal
         */
        private int accumulator(int subtotal, Map.Entry<Integer, Integer> next) {
            return Math.floorDiv(subtotal, (int) Math.pow(next.getKey(), next.getValue()) *
                    factorialTable[next.getValue() - 1]);
        }

        /**
         * Returns a table of values such that table[a][b] is the gcd of a and b via Euclid's algorithm
         * @param size  the maximum number the table can check for
         * @return  the gdc table
         */
        private int[][] buildGcdTable(int size) {
            int[][] table = IntStream.range(0, size).mapToObj(x ->
                    IntStream.range(0, size).map(y -> 0).toArray()).toArray(int[][]::new);

            IntStream.range(0, size).forEach(a -> IntStream.range(a, size).forEach(b -> {
                if (a == 0 || b == 0) {
                    table[a][b] = table[b][a] = 1;
                } else if (a == b) {
                    table[a][a] = a + 1;
                } else {
                    table[a][b] = table[b][a] = table[a][b - a - 1];
                }
            }));

            return table;
        }

        /**
         * Returns an array of factorials such that array[i] is the factorial of i
         * @param size  the number of factorials to add
         * @return  the array of factorials
         */
        private int[] buildFactorialTable(int size) {
            Deque<Integer> factorials = new LinkedList<>(Collections.singleton(1));
            IntStream.range(0, size - 1).forEach(n -> factorials.add(factorials.getLast() * (n + 2)));
            return factorials.stream().mapToInt(i -> i).toArray();
        }


    }

}
