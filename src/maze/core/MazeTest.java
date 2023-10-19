package maze.core;

import static org.junit.Assert.*;

import core.Pos;
import org.junit.Test;

public class MazeTest {
	final static int NUM_TESTS = 100;
	final static int WIDTH = 10, HEIGHT = 15;

	@Test
	public void testNoTreasure() {
		for (int i = 0; i < NUM_TESTS; ++i) {
			Maze m = new Maze(WIDTH, HEIGHT);
			m.makeMaze(new Pos(0, 0), new Pos(WIDTH - 1, HEIGHT - 1), 0, 1);
			MazeTestSearcher searcher = new MazeTestSearcher();
			searcher.solve(new MazeExplorer(m, m.getStart()));
			assertTrue(searcher.success());
			assertTrue(searcher.getResult().isPresent());
			MazePath path = new MazePath(searcher.getResult().get(), m);
			assertTrue(path.solvesMaze(m));
		}
	}

	@Test
	public void testMany() {
		for (int i = 0; i < NUM_TESTS; ++i) {
			Maze m = new Maze(WIDTH, HEIGHT);
			m.makeMaze(new Pos(0, 0), new Pos(WIDTH - 1, HEIGHT - 1), 2, 1);
			MazeTestSearcher searcher = new MazeTestSearcher();
			MazeExplorer endNode = new MazeExplorer(m, m.getEnd());
			endNode.addTreasures(m.getTreasures());
			searcher.solve(new MazeExplorer(m, m.getStart()));
			assertTrue(searcher.success());
			assertTrue(searcher.getResult().isPresent());
			MazePath path = new MazePath(searcher.getResult().get(), m);
			assertTrue(path.solvesMaze(m));
		}
	}
	
	@Test
	public void testBestFirst() {
		int totalBest = 0, totalBreadth = 0;
		for (int i = 0; i < NUM_TESTS; ++i) {
			Maze m = new Maze(WIDTH, HEIGHT);
			m.makeMaze(new Pos(0, 0), new Pos(WIDTH - 1, HEIGHT - 1), 0, 1);
			MazeSearcher breadthFirst = new MazeSearcher(new maze.heuristics.BreadthFirst());
			MazeSearcher bestFirst = new MazeSearcher(n -> m.getGoal().getLocation().getX() - n.getLocation().getX());
			MazeExplorer startNode = new MazeExplorer(m, m.getStart());
			breadthFirst.solve(startNode);
			bestFirst.solve(startNode);
			assertTrue(breadthFirst.success());
			assertTrue(bestFirst.success());
			totalBest += bestFirst.getNumNodes();
			totalBreadth += breadthFirst.getNumNodes();
		}
		assertTrue(totalBest < totalBreadth);
	}
}
