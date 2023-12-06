package maze.core;

import search.breadthfirst.BreadthFirstSearcher;

public class MazeTestSearcher extends BreadthFirstSearcher<MazeExplorer> {
    public MazeTestSearcher() {
        super(MazeExplorer::getSuccessors, MazeExplorer::achievesGoal);
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> a18b785 (project1)
