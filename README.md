# sokoban
AI to solve sokoban 
Algorithm:
To simplify this problem, we use an intelligent state presentation that including the
positions of boxes and move conditions of all the boxes. The position of box can be easily
described by coordinates of X and Y in the map. However, the move conditions which means
whether the box can be pushed in certain direction has to be divided into two parts. First,
whether there are any space in front of the box. Second, whether the person can move to the
position behind the box where he can push the box forward. We use A* search to such
judgement since it, at the same time, can help us find the shortest path that the person can
take to the behind position. We define a class to package these information and return it.
Before we actually use the A* framework to solve this problem, we also need to do some
optimization to reduce the search space. First, when we decide to put a state into the frontier,
we need to judge whether we have got this state before. To realize this, we rewrite the
equals() function and hashcode() function. We use the positions and move conditions of
boxes to do such comparison.

The heuristic function we use here is the sum of Manhattan distance that from each box to
the nearest goal position.
![image](http://github.com/zerowsw/sokoban/src/main/formula.png) <br>
m: number of boxes, G : one of the goal that is nearest to the box Bi
When calculating the distance of certain box, we ignore the the obstacle of other boxes which
simplify our calculation and make sure that the manhattan distance we get here is smaller
than the actual cost from the box to the goal. Thus the sum should be smaller than the actual
total cost from the present state to the final state. In conclusion, our heuristic function is
admissible.
