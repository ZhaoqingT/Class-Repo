package pacman.entries.pacman;


import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.controllers.examples.StarterGhosts;
import static pacman.game.Constants.*;


import java.util.*;


/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;

	private int Evaluation(Game game, Game lastgame){
		int currentpac = game.getPacmanCurrentNodeIndex();
		int distanceToGhost = 0;
		int distanceToEdibleGhost = Integer.MAX_VALUE;
		int score = 0;

		if (game.getCurrentLevel() > lastgame.getCurrentLevel()){
			return Integer.MAX_VALUE;
		}

		for (GHOST ghost : GHOST.values()){
			if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost)==0){
				int currentDistance = game.getShortestPathDistance(currentpac, game.getGhostCurrentNodeIndex(ghost));
				if (currentDistance < distanceToGhost) {
					distanceToGhost = currentDistance;
				}
			}
			if (game.getGhostEdibleTime(ghost) > 0){
				int currentDistance = game.getShortestPathDistance(currentpac, game.getGhostCurrentNodeIndex(ghost));
				if (currentDistance < distanceToEdibleGhost) {
					distanceToEdibleGhost = currentDistance;
				}
			}
		}

		score = game.getScore();
		score += (game.getNumberOfPills() - game.getNumberOfActivePills()) * 100;

		int distanceToPill = Integer.MAX_VALUE;
		for (int i : game.getActivePillsIndices()){
			int distance = game.getShortestPathDistance(currentpac, i);
			if (distance < distanceToPill){
				distanceToPill = distance;
			}
		}

		score -= distanceToPill * 100;
		score -= distanceToGhost * 10;
		score += distanceToEdibleGhost * 10;

//		score = game.getNumberOfPills() - game.getNumberOfActivePills();
		return score;
	}

	private int Evaluation(Game game){
		int[] index = game.getActivePillsIndices();
		int nearest = game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), index, DM.PATH);
		int distance = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), nearest);
		return game.getScore() / 2  + game.getNumberOfPills() - game.getNumberOfActivePowerPills();
	}


	private MOVE DepthFirstSearch(Game game){
		int current = game.getPacmanCurrentNodeIndex();
		int best = 0;
		MOVE bestMove = MOVE.NEUTRAL;
		MOVE pMove[] = game.getPossibleMoves(current,game.getPacmanLastMoveMade());
		for (MOVE move : pMove) {
			Game copy = game.copy();
			copy.advanceGame(move, new StarterGhosts().getMove(copy.copy(), System.currentTimeMillis()+DELAY));
			int value = dfsRecursive (copy, 200, 1);
			if (value > best) {
				best = value;
				bestMove = move;
			}
		}
		return bestMove;
	}

	private MOVE DepthIteratingSearch(Game game){
		int current = game.getPacmanCurrentNodeIndex();
		int best = 0;
		MOVE bestMove = MOVE.NEUTRAL;
		MOVE pMove[] = game.getPossibleMoves(current,game.getPacmanLastMoveMade());
		for (MOVE move : pMove) {
			Game copy = game.copy();
			copy.advanceGame(move, new StarterGhosts().getMove(copy.copy(), System.currentTimeMillis()+DELAY));
			Random random = new Random();
			int value = dfsRecursive (copy, random.nextInt(200), 1);
			if (value > best) {
				best = value;
				bestMove = move;
			}
		}
		return bestMove;
	}

	int dfsRecursive (Game game, int levellimit, int curlevel) {
		int current = game.getPacmanCurrentNodeIndex();
		int best = 0;
		MOVE pMove[] = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
		for (MOVE move : pMove) {
			Game copy = game.copy();
			copy.advanceGame(move, new StarterGhosts().getMove(copy.copy(), System.currentTimeMillis() + DELAY));
			if (copy.wasPacManEaten())
				return Integer.MIN_VALUE;
			else if (curlevel == levellimit){
				int score = Evaluation(copy);
				if (score > 20000) return score;
				if (score > best) best = score;
			}
			else{
				return dfsRecursive(copy, levellimit, curlevel + 1);
			}
		}
		return best;
	}

	private MOVE BreadthFirstSearch(Game game){
		int current = game.getPacmanCurrentNodeIndex();
		int bestscore = 0;
		MOVE bestmove = MOVE.NEUTRAL;
		Queue<Game> qgame = new LinkedList<Game>();
		Queue<MOVE> qmove = new LinkedList<MOVE>();
		Map<Game, Game> map = new HashMap<Game, Game>();

		MOVE pMove[] = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
		for (MOVE move : pMove){
			Game copy = game.copy();
			copy.advanceGame(move,new StarterGhosts().getMove(copy.copy(), System.currentTimeMillis()+DELAY));
			if (copy.wasPacManEaten()){
				continue;
			}else {
				qmove.add(move);
				qgame.add(copy);
			}

		}
		Game bestgame = bfsRecursive(qgame, qmove, map, 100, 1);
		while (map.containsKey(bestgame)){
			bestgame = map.get(bestgame);
		}
		if(bestgame != null)
		{
			bestmove = bestgame.getPacmanLastMoveMade();
		}
		return bestmove;
	}


	private Game bfsRecursive(Queue<Game> qgame, Queue<MOVE> qmove, Map<Game,Game> map, int level_limit, int curlevel){
		if (curlevel == level_limit){
			int size = qgame.size();
			int best_score = 0;
			Game best_game = qgame.peek();
			for (int i = 0; i < size; i++){
				Game curgame = qgame.poll();
				MOVE lastmove = qmove.poll();
				if (Evaluation(curgame, map.get(curgame)) > best_score){
					best_score = Evaluation(curgame, map.get(curgame));
					best_game = curgame;
				}
			}
			return best_game;
		}

		int size = qgame.size();
		int bestscore = 0;
		Game bestgame = qgame.peek();
		Controller<EnumMap<GHOST, MOVE>> ghost = new StarterGhosts();

		for (int i = 0; i < size; i++){
			Game curgame = qgame.poll();
			MOVE lastmove = qmove.poll();
			MOVE pMove[] = curgame.getPossibleMoves(curgame.getPacmanCurrentNodeIndex(), lastmove);
			if (pMove.length == 0)
				continue;
			for (MOVE move : pMove){
				Game copy = curgame.copy();
				ghost.update(copy,-1);
				copy.advanceGame(move,ghost.getMove());
				if (copy.wasPacManEaten()){
					continue;
				}else {
					qgame.add(copy);
					qmove.add(move);
					map.put(copy, curgame);
				}
			}
		}

		if (qgame.size() > 0) {
			Game next_level_best = bfsRecursive(qgame, qmove, map, level_limit, curlevel + 1);
			if (next_level_best.getScore() > bestscore) {
				bestgame = next_level_best;
			}
		}
		return bestgame;
	}


	private static class Node {
		MOVE move;
		Game game;
		int layer;

		public Node(MOVE m, Game g, int i) {
			move = m;
			game = g;
			layer = i;

		}
		public double getGoodness(){
			return game.getScore() / (game.getCurrentLevel() + 1) + game.getNumberOfPills() - game.getNumberOfActivePills();
		}
	}

	private class HComparator<T> implements Comparator<T>{

		@Override
		public int compare(T t, T t1) {
			Node a = (Node) t;
			Node b = (Node) t1;
			if(a.game.getCurrentLevel() > b.game.getCurrentLevel()){
				return -1;
			}
			else if(a.game.getCurrentLevel() < b.game.getCurrentLevel()){
				return 1;
			}
			if(a.getGoodness() > b.getGoodness()){
				return -1;
			}
			else{
				return 1;
			}
		}
	}

	private MOVE AStar(Game game){
		PriorityQueue<Node> q = new PriorityQueue<>(new HComparator());
		MOVE[] pMove = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
		Controller<EnumMap<GHOST,MOVE>> ghost = new StarterGhosts();
		for(MOVE m : pMove){
			Game s = game.copy();
			s.advanceGame(m, ghost.getMove(s.copy(), System.currentTimeMillis()+DELAY));
			q.offer(new Node(m, s, 1));
		}
		while(q.size() > 1){
			Node current = q.poll();
			MOVE[] pMove_current = current.game.getPossibleMoves(current.game.getPacmanCurrentNodeIndex(), current.game.getPacmanLastMoveMade());
			for(MOVE m:pMove_current){
				Game cstate = current.game.copy();
				cstate.advanceGame(m, ghost.getMove(cstate.copy(), System.currentTimeMillis()+DELAY));
				if(!cstate.wasPacManEaten() && (cstate.getNumberOfActivePills() == 0  || cstate.getCurrentLevel() != game.getCurrentLevel())){
					return current.move;
				}
				else if(!cstate.wasPacManEaten() && cstate.getCurrentLevel() == game.getCurrentLevel()){
					Node next_level = new Node(current.move, cstate, current.layer + 1);
					q.add(next_level);
				}
			}
		}
		return q.poll().move;
	}


	private MOVE HillClimbing(Game game){
		int C = 150;
		Random random=new Random();
		ArrayList<Integer> choices = new ArrayList<Integer>();
		Controller<EnumMap<GHOST,MOVE>> ghost = new StarterGhosts();
		int bestScore = Integer.MIN_VALUE;
		MOVE bestMove = MOVE.NEUTRAL;
		int level1 = 0;
		int level2 = 0;

		for(int i = 0; i < C; i++){
			choices.add(random.nextInt(150));
		}

		while (level1 ++ < 100){
			Game copy = game.copy();
			int index = random.nextInt(C);
			int last_val = choices.get(index);
			int new_val = random.nextInt(150);
			choices.set(index,new_val);
			MOVE firstMove = MOVE.NEUTRAL;
			boolean first = true;
			while(level2++ < 100){
				int pnode = copy.getPacmanCurrentNodeIndex();
				MOVE next_move = MOVE.NEUTRAL;
				MOVE pMove[] = copy.getPossibleMoves(copy.getPacmanCurrentNodeIndex(), copy.getPacmanLastMoveMade());
				if (pMove.length > 1) {
					next_move = pMove[choices.get(level2) % pMove.length];
				}else if (pMove.length == 1){
					next_move = pMove[0];
				}
				copy.advanceGame(next_move, ghost.getMove(copy, System.currentTimeMillis()+DELAY));
				if (copy.wasPacManEaten())
					break;
				if (first) {
					firstMove = next_move;
					first = false;
				}
			}
			int s = Evaluation(copy);
			if (s > bestScore) {
				bestMove = firstMove;
				bestScore = s;
			}
			else {
				choices.set(index,last_val);
			}
		}

		return bestMove;
	}


	private double cooldown(int t){ return 1.0 / t; }

	private MOVE SimulatedAnnealing(Game game){

		Controller<EnumMap<GHOST, MOVE>> ghost = new StarterGhosts();
		int C = 100;
		Random random = new Random();
		ArrayList<Integer> choices = new ArrayList<Integer>();
		int bestScore = 0;
		MOVE bestMove = MOVE.NEUTRAL;
		int level1 = 0;
		int level2 = 0;

		for(int i = 0; i < C; i++){
			choices.add(random.nextInt(150));
		}
		int t = 1;

		while (level1 ++ < 100){
			Game copy = game.copy();
			int index = random.nextInt(C);
			int last_val = choices.get(index);
			int new_val = random.nextInt(150);
			choices.set(index,new_val);
			int c = 0;
			MOVE firstMove = MOVE.NEUTRAL;
			boolean first = true;
			while(level2++ < C){
				if (copy.wasPacManEaten()){ break; }
				MOVE[] pMove = copy.getPossibleMoves(copy.getPacmanCurrentNodeIndex(), copy.getPacmanLastMoveMade());
				MOVE m = pMove[0];
				if (pMove.length > 1) {
					m = pMove[choices.get(c++) % pMove.length];
				}
				copy.advanceGame(m, ghost.getMove(copy, System.currentTimeMillis()+DELAY));
				if (first) {
					firstMove = m;
					first = false;
				}
			}

			int s = Evaluation(copy);
			double temp = cooldown(t++);
			if (s > bestScore) {
				bestMove = firstMove;
				bestScore = s;
			}
			else {
				int delta = s - bestScore;
				double prob = Math.exp(delta / temp);
				double reject = prob * 100000;
				int r = random.nextInt(100000);
				if (r > reject) {
					choices.set(index,last_val);
				}
			}
		}
		return bestMove;
	}


	private MOVE ExecuteGame(ArrayList<Integer> choices, Game game){
		int c = 0;
		MOVE firstMove = MOVE.NEUTRAL;
		Controller<EnumMap<GHOST,MOVE>> ghost = new StarterGhosts();
		boolean first = true;
		Game copy = game.copy();
		int level = 0;
		while(level < 100){
			if (copy.wasPacManEaten())
				break;

			MOVE[] pMove = copy.getPossibleMoves(copy.getPacmanCurrentNodeIndex(), copy.getPacmanLastMoveMade());
			MOVE m = pMove[0];

			if (pMove.length > 1) {
				if (c == choices.size())
					break;
				m = pMove[choices.get(c++) % pMove.length];
			}
			copy.advanceGame(m, ghost.getMove(copy, System.currentTimeMillis()+DELAY));
			if (first) {
				firstMove = m;
				first = false;
			}
		}
		return firstMove;
	}


	private class Node2{
		public MOVE firstMove;
		public int score;
		public ArrayList<Integer> choices;
		Node2(MOVE m, int i, ArrayList<Integer> arr){
			firstMove = m;
			score = i;
			choices = arr;
		}
	}

	class NComparator implements Comparator<Node2>{
		public int compare(Node2 a, Node2 b){
			return b.score - a.score;
		}
	}

	private MOVE Evolution_first(Game game){
		int C = 20;
		int MU = 50;
		int LAMBDA = 50;
		Random random=new Random();
		ArrayList<Node2> population = new ArrayList<Node2>();

		for (int i = 0; i < MU+LAMBDA; i++){
			Game copy = game.copy();
			ArrayList<Integer> choices = new ArrayList<Integer>();
			for(int j = 0; j < C; j++){
				choices.add(random.nextInt(150));
			}
			MOVE firstMove = ExecuteGame(choices, copy);
			int s = copy.getScore();
			population.add(new Node2(firstMove,s,choices));
		}

		int LEVEL = 0;
		while (LEVEL < 50){
			Collections.sort(population,new NComparator());

			for(int i = 0; i < LAMBDA; i++){
				ArrayList<Integer> choices = population.get(i%MU).choices;
				int index = random.nextInt(C);
				int val = random.nextInt(150);
				choices.set(index,val);
				Game copy = game.copy();
				MOVE firstMove = ExecuteGame(choices, copy);
				int s = copy.getScore();
				population.set(MU + i,new Node2(firstMove,s,choices));
			}
			LEVEL++;
		}

		return population.get(0).firstMove;
	}


	private MOVE Evolution_second(Game game) {
		int C = 10;
		int MU = 50;
		int LAMBDA = 50;
		Random random=new Random();
		ArrayList<Node2> population = new ArrayList<Node2>();

		for (int i = 0; i < MU+LAMBDA; i++){
			Game copy = game.copy();
			ArrayList<Integer> choices = new ArrayList<Integer>();
			for(int j = 0; j < C; j++){
				choices.add(random.nextInt(6));
			}
			MOVE firstMove = ExecuteGame(choices, copy);
			int s = Evaluation(copy);
			population.add(new Node2(firstMove,s,choices));
		}

		int LEVEL = 0;
		while (LEVEL < 50){
			Collections.sort(population,new NComparator());
			for(int i = 0; i < MU; i++){
				ArrayList<Integer> choices1 = population.get(2 * i).choices;
				ArrayList<Integer> choices2 = population.get(2 * i + 1).choices;
				for (int j = 0; j < C / 2; i++){
					choices1.set(j, choices2.get(j));
					choices2.set(j + C / 2, choices1.get(j + C / 2));
				}
				Game copy1 = game.copy();
				MOVE firstMove1 = ExecuteGame(choices1, copy1);
				int s1 = Evaluation(copy1);
				population.set(2 * i, new Node2(firstMove1,s1,choices1));
			}
			LEVEL ++;
		}
		return population.get(0).firstMove;
	}


	public MOVE getMove(Game game, long timeDue) 
	{
		//Place your game logic here to play the game as Ms Pac-Man

//		myMove = DepthFirstSearch(game);
//		myMove = BreadthFirstSearch(game);
//		myMove = DepthIteratingSearch(game);
//		myMove = AStar(game);
//		myMove = HillClimbing(game);
		myMove = SimulatedAnnealing(game);
//		myMove = Evolution_first(game);
//		myMove = Evolution_second(game);

		return myMove;
	}
}


