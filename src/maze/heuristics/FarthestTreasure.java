package maze.heuristics;
import core.Pos;
import maze.core.MazeExplorer;
import java.util.function.ToIntFunction;
public class FarthestTreasure implements ToIntFunction<MazeExplorer>{
    @Override
    public int applyAsInt(MazeExplorer value) {
        int currentFarthest = Integer.MIN_VALUE;
        for(Pos treasure : value.getAllTreasureFromMaze()){
            if(value.getLocation().getManhattanDist(treasure) > currentFarthest){
                currentFarthest = value.getLocation().getManhattanDist(treasure);
            }
        }
        return currentFarthest;
    }
}
