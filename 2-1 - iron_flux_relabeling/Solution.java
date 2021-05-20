import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Solution {

    /**
     * Returns an int array of top converter labels.
     *
     * @param height  the height of the perfect binary tree to construct
     * @param converters  the converters to check in the tree
     * @return  an array of top converter labels
     */
    public static int[] solution(int height, int[] converters) {
        Map<Integer, Node> nodeTree = createNodeTree(height);
        return Arrays.stream(converters).map(i -> getTopConverter(i, nodeTree)).toArray();
    }

    /**
     * Returns a perfect binary tree, represented as a map of oll values in the tree and their respective nodes.
     *
     * @param height  the height of the perfect binary tree
     * @return  map of all values in the binary tree and their respective Nodes.
     */
    private static Map<Integer, Node> createNodeTree(int height) {
        Map<Integer, Node> nodeTree = new HashMap<>();
        /*
         * Initial value will be the maximum number of nodes in the tree, which is 2^h - 1.
         * Value will be held in an array of size 1 to allow global visibility of changes.
         */
        int[] startValue = { (int) Math.pow(2, height) - 1 };
        // Recursively add nodes to the tree.
        addNodes(startValue, 1, height, null, nodeTree);
        return nodeTree;
    }

    /**
     * Returns the top converter of the given converter.
     *
     * @param converter  the converter to check for in the tree
     * @param nodeTree  the tree to use to check
     * @return  the top converter's label if the given converter exists in the tree, else -1
     */
    private static int getTopConverter(int converter, Map<Integer, Node> nodeTree) {
        if (!nodeTree.containsKey(converter)) return -1;
        return nodeTree.get(converter).getParentValue();
    }

    /**
     * Constructs a perfect binary tree by recursively adding nodes to the node map.
     *
     * @param currentValue  the current value to add to the binary tree
     * @param currentHeight  the height of the tree when adding the current value
     * @param maxHeight  the maximum height of the tree
     * @param parent  the current value's parent node
     * @param nodeTree  the map to add nodes
     */
    private static void addNodes(int[] currentValue, int currentHeight, int maxHeight,
                                 Node parent, Map<Integer, Node> nodeTree) {
        Node currentNode = new Node(currentValue[0], parent);
        nodeTree.put(currentValue[0], currentNode);
        // Add the child nodes if we have not reached max height.
        if (currentHeight < maxHeight) {
            currentHeight++;
            // Add the right node.
            currentValue[0]--;
            addNodes(currentValue, currentHeight, maxHeight, currentNode, nodeTree);
            // Add the left node.
            currentValue[0]--;
            addNodes(currentValue, currentHeight, maxHeight, currentNode, nodeTree);
        }
    }

    /**
     * The Node class represents a value in a perfect binary tree, containing the
     * value at that node and a reference to it's parent node.
     */
    private static final class Node {

        private final int value;

        private final Node parent;

        public Node(int value, Node parent) {
            this.value = value;
            this.parent = parent;
        }

        /**
         * Return the value of the node's parent, or -1 if the node does not have a parent.
         */
        private int getParentValue() {
            return parent != null ? parent.value : -1;
        }

    }

}
