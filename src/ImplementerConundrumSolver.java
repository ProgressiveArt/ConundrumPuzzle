public class ImplementerConundrumSolver implements ConundrumSolver {
    ClownGraph clownGraph;

    @Override
    public int[] resolve(int[] initialState) {
        clownGraph = new ClownGraph(initialState); // Создаем наш граф

        return clownGraph.resolve();
    }

    @Override
    public String toString() {
        return clownGraph.toString();
    }
}
