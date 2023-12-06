package search.bestfirst;

import search.SearchNode;
import search.SearchQueue;

import java.util.Optional;
import java.util.function.ToIntFunction;

public class BestFirstQueue<T> implements SearchQueue<T> {
    // TODO: Implement this class
    // HINT: Use java.util.PriorityQueue. It will really help you.

    public BestFirstQueue(ToIntFunction<T> heuristic) {
        // TODO: Your code here
    }

    @Override
    public void enqueue(SearchNode<T> node) {
        // TODO: Your code here
    }

    @Override
    public Optional<SearchNode<T>> dequeue() {
        // TODO: Your code here
        return Optional.empty(); // TODO: Replace this line; it is here to let the code compile.
    }
}
