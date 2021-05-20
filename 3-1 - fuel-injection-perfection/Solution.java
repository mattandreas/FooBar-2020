import java.util.Arrays;

public class Solution {

    /**
     * Returns the least number of conversions to transform the number to 1
     *
     * @param number  the number to transform
     * @return  an int representing the least number of conversions
     */
    public static int solution(String number) {
        Transformer transformer = new Transformer();
        return transformer.transformNumber(number);
    }

    private static final class Transformer {

        private final StringOperator stringOperator = new StringOperator();

        /**
         * Returns the least number of conversions to transform the number to 1
         *
         * @param number  the number to transform
         * @return  an int representing the least number of conversions
         */
        public int transformNumber(String number) {
            return transformNumber(number, 0);
        }

        /**
         * Returns the least number of conversions to transform the number to 1
         *
         * @param number  the number to transform
         * @param operations  the number of operations already performed
         * @return  the number of operations to transform the number to 1
         */
        private int transformNumber(String number, int operations) {
            if (number.equals("1")) return operations;
            // Retrieve the right-most digit in the number.
            int leastSignificant = Integer.parseInt(String.valueOf(number.charAt(number.length() - 1)));
            String converted;
            // Determine if the number is odd.
            if ((leastSignificant & 1) == 1) {
                // If the number is odd, determine if we should increment or decrement, then perform the operation.
                converted = shouldIncrement(number) ? stringOperator.addOneToString(number) :
                        stringOperator.subtractOneFromString(number);
            } else {
                // if the number is even, divide the number by 2.
                converted = stringOperator.divideStringByTwo(number);
            }
            // Recursively transform the number.
            return transformNumber(converted, ++operations);
        }

        /**
         * Determines if we should increment the give number. Starting from 7, every other odd number
         * should be incremented, else decremented. Algorithm was determined by performing a
         * minimax version of transformNumber on the first 1000 positive integers.
         *
         * @param number  the number to check whether to increment or decrement
         * @return  a boolean value representing if we should increment
         */
        private boolean shouldIncrement(String number) {
            // 3 does not follow the rule, should be decremented.
            if (number.equals("3")) return false;
            // Get the right-most digit.
            char leastSignificant = number.charAt(number.length() - 1);
            // If number equals 3 or 7, we should increment.
            boolean shouldIncrement = Arrays.asList('3', '7').contains(leastSignificant);
            if (number.length() == 1) return shouldIncrement;
            // Get the second right-most digit.
            int secondLeastSignificant = Integer.parseInt(String.valueOf(number.charAt(number.length() - 2)));
            // If the second right-most digit is odd, we need to negate the result.
            boolean opposite = (secondLeastSignificant & 1) == 1;
            return opposite != shouldIncrement;
        }

    }

    /**
     * The StringOperator class performs basic math operations on integers represented in Strings.
     */
    private static final class StringOperator {

        /**
         * Returns a number divided by 2.
         *
         * @param number  a positive integer in String form
         * @return  the number divided by 2
         */
        public String divideStringByTwo(String number) {
            return divideStringByTwo(number, 0, 0, new StringBuilder());
        }

        /**
         * Returns a number incremented by 1.
         *
         * @param number  a positive integer in String form
         * @return  the number incremented by 1
         */
        public String addOneToString(String number) {
            return addOneToString(number, 1, 1, new StringBuilder());
        }

        /**
         * Returns a number decremented by 1.
         *
         * @param number  a positive integer in String form
         * @return  the number decremented by 1
         */
        public String subtractOneFromString(String number) {
            return subtractOneFromString(number, 1, 1, new StringBuilder());
        }

        /**
         * Represents performing division by 2 by hand.
         *
         * @param number  the number to divide by 2
         * @param carry  the number to carry from a previous operation
         * @param index  the current index in the number to perform the operation
         * @param accumulator  a StringBuilder to hold the result
         * @return  a String, which is the number divided by 2
         */
        private String divideStringByTwo(String number, int carry, int index, StringBuilder accumulator) {
            if (index == number.length()) return accumulator.toString();
            // Get the digit at the given index. Place the carry to the left of that number.
            int value = Integer.parseInt(String.format("%d%c", carry, number.charAt(index)));
            // Divide the value by 2 and floor the result.
            int result = value >> 1;
            /*
             * Append the result to the accumulator. Do not append if
             * the accumulator is empty and the result equals zero.
             */
            if (accumulator.length() > 0 || result != 0) accumulator.append(result);
            // Get the carry by multiplying the result by 2 and subtracting it from the initial value.
            carry = value - (result << 1);
            // Recursively divide the number by 2.
            return divideStringByTwo(number, carry, ++index, accumulator);
        }

        /**
         * Represents performing addition by 1 by hand.
         *
         * @param number  the number to increment
         * @param carry  the number to carry from a previous operation
         * @param index  the current index in the number to perform the operation, representing digits from the right
         * @param accumulator  a StringBuilder to hold the result
         * @return  a String, which is the number incremented by 1
         */
        private String addOneToString(String number, int carry, int index, StringBuilder accumulator) {
            // Check if we are at the end of the number.
            if (index == number.length() + 1) {
                if (carry != 0) accumulator.append(carry);
                // Reverse the result since the number was traversed from right to left.
                return accumulator.reverse().toString();
            }
            // Get the digit offset from the right-most digit by the index.
            char numberToCheck = number.charAt(number.length() - index);
            // Increment the digit by the carry.
            int value = Integer.parseInt(String.valueOf(numberToCheck)) + carry;
            if (value > 9) {
                // If the value is greater than 9, append zero and carry the 1.
                accumulator.append(0);
                carry = 1;
            } else {
                // Append the value to the result.
                accumulator.append(value);
                carry = 0;
            }
            // Recursively increment the number.
            return addOneToString(number, carry, ++index, accumulator);
        }

        /**
         * Represents performing subtraction by 1 by hand.
         *
         * @param number  the number to decrement
         * @param borrow  the number to borrow from a previous operation
         * @param index  the current index in the number to perform the operation, representing digits from the right
         * @param accumulator  a StringBuilder to hold the result
         * @return  a String, which is the number decremented by 1
         */
        private String subtractOneFromString(String number, int borrow, int index, StringBuilder accumulator) {
            // Check if we are at the end of the number.
            if (index == number.length() + 1) {
                // Reverse the result since the number was traversed from right to left.
                return accumulator.reverse().toString();
            }
            // Get the digit offset from the right-most digit by the index.
            char numberToCheck = number.charAt(number.length() - index);
            // Decrement the digit by the borrow.
            int value = Integer.parseInt(String.valueOf(numberToCheck)) - borrow;
            if (value < 0) {
                // If the value is less than zero, append 9 and borrow the 1.
                accumulator.append(9);
                borrow = 1;
            } else {
                // Append the value to the result.
                accumulator.append(value);
                borrow = 0;
            }
            // Recursively decrement the number.
            return subtractOneFromString(number, borrow, ++index, accumulator);
        }

    }

}
