package maze.heuristics;
import core.Pos;
import maze.core.MazeExplorer;
import java.util.function.ToIntFunction;
public class ClosestTreasure implements ToIntFunction<MazeExplorer>{

    @Override
    public int applyAsInt(MazeExplorer value) {
        int currentClosest = Integer.MAX_VALUE;
        for(Pos treasure : value.getAllTreasureFromMaze()){
            if(value.getLocation().getManhattanDist(treasure) < currentClosest){
                currentClosest = value.getLocation().getManhattanDist(treasure);
            }
        }
        return currentClosest;
    }
}
