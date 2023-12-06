package maze.heuristics;

import maze.core.MazeExplorer;
import java.util.function.ToIntFunction;

public class Manhattan implements ToIntFunction<MazeExplorer>{
    @Override
    public int applyAsInt(MazeExplorer value) {
        return value.getLocation().getManhattanDist(value.getGoal().getLocation());
    }
}
