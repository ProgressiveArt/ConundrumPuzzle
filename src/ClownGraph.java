import javafx.util.Pair;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

public class ClownGraph extends Graph {
    final static int[] nodeIds;
    final static Pair<Integer, Integer>[] connectionIds;

    static {
        nodeIds = getNodeIds();
        connectionIds = getConnectionIds();
    }

    static int[] getNodeIds() {
        return new int[]{1, 2, 3, 4, 0, 5, 6, 7};
    }

    //Создаем пары(по ID`шникам)
    static Pair<Integer, Integer>[] getConnectionIds() {
        return new Pair[]{
                new Pair<>(1, 2),
                new Pair<>(1, 3),
                new Pair<>(2, 3),
                new Pair<>(2, 4),
                new Pair<>(3, 5),
                new Pair<>(4, 0),
                new Pair<>(4, 6),
                new Pair<>(0, 5),
                new Pair<>(5, 7),
                new Pair<>(6, 7)
        };
    }

    //Массив значений в узлах
    private int[] getNodeValues() {
        int[] nodeValues = new int[nodeIds.length];

        for (int i = 0; i < nodeIds.length; i++) {
            nodeValues[i] = getNode(nodeIds[i]).getValue();
        }

        return nodeValues;
    }

    public ClownGraph(int[] nodeValues) {
        if (nodeValues.length != nodeIds.length) {
            throw new IllegalArgumentException("Кол-во узлов в данном графе должно быть 8");
        }

        setNodes(nodeValues, nodeIds, connectionIds); // При установке Node, пары преобразуем в двусторонние связи
    }

    public int[] resolve() {

        //Считаем кол-во не совпадений с конечным состоянием графа для будущих расчетов эвристической функции
        int h = 8;
        for (int i = 0; i < 8; i++) {
            GraphNode curNode = getNode(i);
            if (curNode.isPlaced()) {
                h--;
            }
        }

        GraphNodeTrack track = new GraphNodeTrack(findNode(0), h); //Создаем трек(путь)
        Queue<GraphTrack> queue = new PriorityQueue<>(1000000, fComparator);
        HashSet<Integer> queueSet = new HashSet<>(); // Сет для отсечения повторяющихся ситуаций

        queue.add(new GraphTrack(track, this));

        while (!queue.isEmpty()) {
            GraphTrack curTrack = queue.poll();
            queueSet.remove(curTrack.getClownGraph().hashCode());
            /* Если без этой строки(удаление повторяющихся ситуаций),
                то из 40320 заданий самое
                сложное решается за 39 ходов и алгоритм работает
                в 3 раза быстрее(21минуту у меня), а если с ней,
                то за 38 ходов и ~1 час. */

            if (curTrack.getGraphNodeTrack().getH() == 0) {
                nodes = curTrack.getClownGraph().nodes;
                return curTrack.getGraphNodeTrack().getNormalizedTrack();
            }

            //Заготовочка для алгоритма (A* "A star"\"Улучшенный алгоритм Дейкстры")
            GraphNode prevNode = curTrack.getGraphNodeTrack().getPrevNode();
            ClownGraph curClownGraph = curTrack.getClownGraph();

            //В самом начале у нас нет предыдущей ноды
            GraphNode newPrevNode = prevNode != null ? curClownGraph.getNode(prevNode.getId()) : null;

            GraphNode curNode = curTrack.getGraphNodeTrack().getGraphNode();


            //В ходе экспериментов выяснил, что на решение самого сложного варианта требуется значение F не более 40
            if (curTrack.getGraphNodeTrack().getF() > 40) {
                continue;
            }


            //Сам алгоритм A*
            for (GraphNode neighborNode : curClownGraph.getNode(curNode.getId()).getNeighbors()) {

                //Чтобы не "шагать" назад
                if (neighborNode != newPrevNode) {
                    int diffH = 0;

                    if (neighborNode.isPlaced())
                        diffH++;

                    if (curNode.isPlaced())
                        diffH++;

                    ClownGraph newClownGraph = new ClownGraph(curClownGraph.getNodeValues());
                    GraphNode newNeighbor = newClownGraph.getNode(neighborNode.getId());
                    GraphNode newCurNode = newClownGraph.getNode(curNode.getId());

                    newCurNode.swapWithNeighbors(newNeighbor);

                    if (newNeighbor.isPlaced())
                        diffH--;

                    if (newCurNode.isPlaced())
                        diffH--;

                    GraphNodeTrack newTrack = new GraphNodeTrack(curTrack.getGraphNodeTrack(), newNeighbor, diffH, newCurNode.getValue());

                    GraphTrack newGraphTrack = new GraphTrack(newTrack, newClownGraph);

                    int hashCode = newClownGraph.hashCode();

                    if (!queueSet.contains(hashCode)) {
                        queueSet.add(hashCode);
                        queue.add(newGraphTrack);
                    }
                }
            }
        }
        //Шутки шутками, но это и правда так... =|
        throw new IllegalArgumentException("В этот Exception мы никогда не попадем, потому что решаются абсолютно все варианты событий \"40320 мультивселенных\"");
    }

    public static Comparator<GraphTrack> fComparator = (gt750, gt750ti) -> (int) (gt750.getGraphNodeTrack().getF() - gt750ti.getGraphNodeTrack().getF());

    @Override
    public String toString() {
        return "  " + getNode(1).getValue() + "\n" +
                " " + getNode(2).getValue() + " " + getNode(3).getValue() + "\n" +
                "" + getNode(4).getValue() + " " + getNode(0).getValue() + " " + getNode(5).getValue() + "\n" +
                " " + getNode(6).getValue() + " " + getNode(7).getValue();
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(toString().replace(" ", "").replace("\n", ""));
    }
}


//Понадобилось сделать треки, содержащие сами графы... Нужна была связь трека и графа
class GraphTrack {
    private GraphNodeTrack graphNodeTrack;
    private ClownGraph clownGraph;

    public GraphTrack(GraphNodeTrack graphNodeTrack, ClownGraph clownGraph) {
        this.graphNodeTrack = graphNodeTrack;
        this.clownGraph = clownGraph;
    }

    public GraphNodeTrack getGraphNodeTrack() {
        return graphNodeTrack;
    }

    public ClownGraph getClownGraph() {
        return clownGraph;
    }
}

/* Track = путь
 Я сначала думал на LinkedList его сделать, но это занимало бы много памяти и
 мне бы приходилось его постоянно клонировать, а клонировать - плохо.
 */
class GraphNodeTrack {
    private GraphNodeTrack prevGraphNodeTrack; // предыдущий трек нода
    private GraphNode graphNode; // текущая нода
    private int countNodesInTrack; //требуется для нормализации, чтобы мы сразу могли определить глубину NodeTrack.
    private int toPrintResolve;

    private int h;

    public int getF() {
        return getG() + getH(); // Эвристическая функция
    }

    public int getH() {
        return h; //Число несовпадений текущего графа с тем, который в итоге должен получится
    }

    public int getG() {
        return countNodesInTrack; //Число шагов
    }

    public GraphNodeTrack(GraphNode graphNode, int h) {
        this.graphNode = graphNode;
        countNodesInTrack = 0;
        this.h = h;
    }

    public GraphNodeTrack(GraphNodeTrack nodeTrack, GraphNode graphNode, int diffH, int toPrintResolve) {
        prevGraphNodeTrack = nodeTrack;
        this.graphNode = graphNode;
        countNodesInTrack = nodeTrack.countNodesInTrack + 1;
        h = nodeTrack.h + diffH;
        this.toPrintResolve = toPrintResolve;
    }

    public GraphNode getPrevNode() {
        return prevGraphNodeTrack == null ? null : prevGraphNodeTrack.graphNode;
    }

    public GraphNode getGraphNode() {
        return graphNode;
    }

    // Просто преобразование чтобы указать путь, по которому мы должны пройти
    public int[] getNormalizedTrack() {
        int[] normalizedTrack = new int[countNodesInTrack];
        int inc = countNodesInTrack - 1;
        GraphNodeTrack curGraphNode = this;
        while (curGraphNode.prevGraphNodeTrack != null) {
            normalizedTrack[inc--] = curGraphNode.toPrintResolve;
            curGraphNode = curGraphNode.prevGraphNodeTrack;
        }
        return normalizedTrack;
    }
}
