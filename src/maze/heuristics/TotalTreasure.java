package maze.heuristics;

import core.Pos;
import maze.core.MazeExplorer;
import java.util.function.ToIntFunction;
public class TotalTreasure implements ToIntFunction<MazeExplorer>{

    @Override
    public int applyAsInt(MazeExplorer value) {
        int totaldistance = 0;
        for(Pos treasure : value.getAllTreasureFromMaze()){
            totaldistance += value.getLocation().getManhattanDist(treasure);
        }
        return totaldistance;
    }
}



