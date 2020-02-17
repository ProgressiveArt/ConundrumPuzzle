import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class Graph {
    Map<Integer, GraphNode> nodes; // Словарик, где число - id`шник и GraphNode  - узел графа

    // Собственно установка Node: id, значения и двусторонние связи
    public void setNodes(int[] nodeValues, int[] nodeIds, Pair<Integer, Integer>[] connectionIds) {
        if (nodeValues.length != nodeIds.length) {
            throw new IllegalArgumentException("Кол-во значений не совпадает с кол-вом id узлов в графе");
        }

        nodes = new HashMap<>();
        for (int i = 0; i < nodeIds.length; i++) {
            nodes.put(nodeIds[i], new GraphNode(nodeValues[i], nodeIds[i]));
        }

        for (Pair<Integer, Integer> connectionId : connectionIds) {
            nodes.get(connectionId.getKey()).addNeighbor(nodes.get(connectionId.getValue()));
            nodes.get(connectionId.getValue()).addNeighbor(nodes.get(connectionId.getKey()));
        }
    }

    // Сложность O(n), т.к поиск по значению
    public GraphNode findNode(int value) {
        for (GraphNode node : nodes.values()) {
            if (node.getValue() == value) {
                return node;
            }
        }
        throw new RuntimeException("Не смог найти узел с заданным значением");
    }

    // Сложность O(1), т.к поиск по Id
    public GraphNode getNode(int id) {
        return nodes.get(id);
    }
}

class GraphNode {
    private final int thisHashCode;

    private int value;
    private HashSet<GraphNode> neighbors;

    public GraphNode(int value, int id) {
        this.value = value;
        neighbors = new HashSet<>();
        thisHashCode = id;
    }

    public void addNeighbor(GraphNode neighbor) {
        neighbors.add(neighbor);
    }

    public void removeNeighbor(GraphNode neighbor) {
        neighbors.remove(neighbor);
    }

    public int getValue() {
        return value;
    }

    public HashSet<GraphNode> getNeighbors() {
        return neighbors;
    }

    // Меняю местами ТОЛЬКО значения, не сами узлы
    public void swapWithNeighbors(GraphNode neighbor) {
        if (neighbors.contains(neighbor)) {
            int valueNeighbor = neighbor.value;
            neighbor.value = value;
            value = valueNeighbor;
        } else {
            throw new IllegalArgumentException("Данный узел не является соседом текущего узла");
        }
    }

    public GraphNode copy() {
        return new GraphNode(getValue(), getId());
    }

    public int getId() {
        return hashCode();
    }

    public boolean isPlaced() {
        return value == getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != getClass())
            return false;
        GraphNode node = (GraphNode) o;
        return node.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return thisHashCode;
    }
}
