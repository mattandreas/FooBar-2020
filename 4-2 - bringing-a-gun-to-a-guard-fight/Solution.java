import java.awt.Point;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class Solution {

    /**
     * Returns the exact number of distinct directions that a laser can be fired on the given grid and hit the endpoint
     *
     * @param dimensions  the dimensions of the grid
     * @param startPoint  the starting point of the laser to be fired
     * @param endPoint  the ending point to try and hit
     * @param distance  the maximum distance the laser can travel
     * @return  the number of distinct directions that the laser can be fired
     */
    public static int solution(int[] dimensions, int[] startPoint, int[] endPoint, int distance) {
        PathFinder pathFinder = new PathFinder(dimensions[0], dimensions[1], distance,
                new Point(startPoint[0], startPoint[1]), new Point(endPoint[0], endPoint[1]));
        return pathFinder.getValidDirections();
    }

    /**
     * The PathFinder class will simulate the laser being fired in different directions
     */
    private static final class PathFinder {

        private final int width;

        private final int height;

        private final int maxDistance;

        private final Point startPoint;

        private final Point endPoint;

        private final Set<Point> checked = new HashSet<>();

        public PathFinder(int width, int height, int maxDistance, Point startPoint, Point endPoint) {
            this.width = width;
            this.height = height;
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            this.maxDistance = maxDistance;
        }

        /**
         * Returns the maximum number of distinct directions that a laser can be fired
         *
         * @return  integer value of the maximum directions
         */
        public int getValidDirections() {
            /*
             * Starting with and index of zero, representing the base grid with a direct shot to the endpoint,
             * increment the index until an invalid index is found. The index represents the number of times
             * the laser bounces off of the x or y walls. This can be simulated by mirroring the grid on the given wall.
             */
            for (int count = 0, index = 0 ;; index++) {
                // Generate all mirrored positions for the given index and filter based on validity.
                long validForIndex = getPositionsForIndex(index).stream().filter(this::isValidPosition).count();
                // If none are found for the given index, then we have reached the maximum distance.
                if (validForIndex == 0) return count;
                count += validForIndex;
            }
        }

        /**
         * Returns the Greatest Common Divisor for the given integers
         *
         * @param a  an integer to check
         * @param b  an integer to check
         * @return  an integer value which of the Greatest Common Divisor of the two numbers
         */
        private int findDivisor(int a, int b) {
            if (a == 0) return Math.abs(b);
            return findDivisor(b % a, a);
        }

        /**
         * Returns the distance between the given points
         *
         * @param p1  a point to check
         * @param p2  a point to check
         * @return  the distance between the two points
         */
        private double getDistance(Point p1, Point p2) {
            return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
        }

        /**
         * Returns whether the given positions are valid positions and the laser can reach the end position
         *
         * @param positions  an array of positions, with the start position at index 0 and end position at index 1
         * @return  true or false if the positions are valid positions
         */
        private boolean isValidPosition(Point[] positions) {
            // Add the start position to the set of checked positions as we cannot hit it.
            Point start = positions[0];
            int[] startSlope = getSlopeFor(start);
            checked.add(new Point(startSlope[0], startSlope[1]));
            // Check whether the end position's slope is distinct and validate it's distance.
            Point end = positions[1];
            int[] endSlope = getSlopeFor(end);
            return checked.add(new Point(endSlope[0], endSlope[1])) &&
                    getDistance(startPoint, end) <= maxDistance;
        }

        /**
         * Generates the slope from the start position to the given position
         *
         * @param point  the end position to check
         * @return  an integer array representing the slope, with run at index 0 and rise at index 1
         */
        private int[] getSlopeFor(Point point) {
            int dx = point.x - startPoint.x;
            int dy = point.y - startPoint.y;
            if (dx == 0 && dy == 0) return new int[]{0,0};
            // Find the greatest common divisor, as all slopes must be reduced to their lowest form.
            int divisor = findDivisor(dy, dx);
            return new int[]{dx / divisor, dy / divisor};
        }

        /**
         * Returns a list of position arrays representing a mirrored start position and end position
         *
         * @param index  an integer representing how many times the laser can bounce off of the x or y wall
         * @return  a list of position arrays, with the arrays containing the mirrored start position at index 0
         *          and the mirrored end position at index 1
         */
        private List<Point[]> getPositionsForIndex(int index) {
            List<Point[]> positions = new ArrayList<>();
            // Loop through all of the possible index values.
            for (int x = -index; x <= index; x++) {
                for (int y = -index; y <= index; y++) {
                    // Only considered the very outer mirrored grids.
                    if (x != -index && x != index && y != -index && y != index) continue;
                    // Generate the base x & y coordinates of the mirrored grid.
                    int baseX = width * x, baseY = height * y;
                    // If the x or y index is odd, the grid must be flipped in relation to that axis.
                    int startX = ((x & 1) == 1) ? width - startPoint.x : startPoint.x;
                    int startY = ((y & 1) == 1) ? height - startPoint.y : startPoint.y;
                    int endX = ((x & 1) == 1) ? width - endPoint.x : endPoint.x;
                    int endY = ((y & 1) == 1) ? height - endPoint.y : endPoint.y;
                    positions.add(new Point[] {
                            new Point(baseX + startX, baseY + startY),
                            new Point(baseX + endX, baseY + endY)
                    });
                }
            }
            return positions;
        }

    }

}
