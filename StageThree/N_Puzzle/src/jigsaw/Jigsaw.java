package jigsaw;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * 在此类中填充算法，完成重拼图游戏（N-数码问题）
 *
 * @author yzp
 */
public class Jigsaw {
  JigsawNode beginJNode; // 拼图的起始状态节点
  JigsawNode endJNode; // 拼图的目标状态节点
  JigsawNode currentJNode; // 拼图的当前状态节点
  private Vector<JigsawNode> openList; // open表 ：用以保存已发现但未访问的节点
  private Vector<JigsawNode> closeList; // close表：用以保存已访问的节点
  private Vector<JigsawNode> solutionPath;// 解路径 ：用以保存从起始状态到达目标状态的移动路径中的每一个状态节点
  private boolean isCompleted; // 完成标记：初始为false;当求解成功时，将该标记至为true
  private int searchedNodesNum; // 已访问节点数： 用以记录所有访问过的节点的数量

  /**
   * 拼图构造函数
   *
   * @param bNode - 初始状态节点
   * @param eNode - 目标状态节点
   */
  public Jigsaw(JigsawNode bNode, JigsawNode eNode) {
    this.beginJNode = new JigsawNode(bNode);
    this.endJNode = new JigsawNode(eNode);
    this.currentJNode = new JigsawNode(bNode);
    this.openList = new Vector<JigsawNode>();
    this.closeList = new Vector<JigsawNode>();
    this.solutionPath = null;
    this.isCompleted = false;
    this.searchedNodesNum = 0;
  }

  /**
   * 此函数用于打散拼图：将输入的初始状态节点jNode随机移动len步，返回其打散后的状态节点
   *
   * @param jNode - 初始状态节点
   * @param len   - 随机移动的步数
   * @return 打散后的状态节点
   */
  public static JigsawNode scatter(JigsawNode jNode, int len) {
    int randomDirection;
    len += (int) (Math.random() * 2);
    JigsawNode jigsawNode = new JigsawNode(jNode);
    for (int t = 0; t < len; t++) {
      int[] movable = jigsawNode.canMove();
      do {
        randomDirection = (int) (Math.random() * 4);
      } while (0 == movable[randomDirection]);
      jigsawNode.move(randomDirection);
    }
    jigsawNode.setInitial();
    return jigsawNode;
  }

  /**
   * 获取拼图的当前状态节点
   *
   * @return currentJNode - 拼图的当前状态节点
   */
  public JigsawNode getCurrentJNode() {
    return currentJNode;
  }

  /**
   * 设置拼图的初始状态节点
   *
   * @param jNode - 拼图的初始状态节点
   */
  public void setBeginJNode(JigsawNode jNode) {
    beginJNode = jNode;
  }

  /**
   * 获取拼图的初始状态节点
   *
   * @return beginJNode - 拼图的初始状态节点
   */
  public JigsawNode getBeginJNode() {
    return beginJNode;
  }

  /**
   * 设置拼图的目标状态节点
   *
   * @param jNode - 拼图的目标状态节点
   */
  public void setEndJNode(JigsawNode jNode) {
    this.endJNode = jNode;
  }

  /**
   * 获取拼图的目标状态节点
   *
   * @return endJNode - 拼图的目标状态节点
   */
  public JigsawNode getEndJNode() {
    return endJNode;
  }

  /**
   * 获取拼图的求解状态
   *
   * @return isCompleted - 拼图已解为true；拼图未解为false
   */
  public boolean isCompleted() {
    return isCompleted;
  }

  /**
   * 计算解的路径
   *
   * @return 若有解，则将结果保存在solutionPath中，返回true; 若无解，则返回false
   */
  private boolean calSolutionPath() {
    if (!this.isCompleted()) {
      return false;
    } else {
      JigsawNode jNode = this.currentJNode;
      solutionPath = new Vector<JigsawNode>();
      while (jNode != null) {
        solutionPath.addElement(jNode);
        jNode = jNode.getParent();
      }
      return true;
    }
  }

  /**
   * 获取解路径文本
   *
   * @return 解路径solutionPath的字符串，若有解，则分行记录从初始状态到达目标状态的移动路径中的每一个状态节点；
   * 若未解或无解，则返回提示信息。
   */
  public String getSolutionPath() {
    String Str = new String();
    int flag = 0;
    Str += "Begin::->\r\n";
    if (this.isCompleted) {
    	//vector是倒序压进去的
      for (int i = solutionPath.size() - 1; i >= 0; i--) {
        Str += solutionPath.elementAt(i).toString() + "->";
        flag++;
        if(flag == 4) {
        	Str += "\r\n";
        	flag = 0;
        }
      }
      Str += "\r\n->::End";
    } else
      Str = "Jigsaw is not completed,please cheak.";
    return Str;
  }

  /**
   * 获取访问过的节点数searchedNodesNum
   *
   * @return 返回所有已访问过的节点总数
   */
  public int getSearchedNodesNum() {
    return searchedNodesNum;
  }

  /**
   * 将搜索结果写入文件中，同时显示在控制台 若搜索失败，则提示问题无解，输出已访问节点数；
   * 若搜索成功，则输出初始状态beginJnode，目标状态endJNode
   * ，已访问节点数searchedNodesNum，路径深度nodeDepth和解路径solutionPath。
   *
   * @param pw - 文件输出PrintWriter类对象，如果pw为null，则写入到Result.txt
   * @throws IOException
   */
  public void printResult(PrintWriter pw) throws IOException {
    boolean flag = false;
    if (pw == null) {
      pw = new PrintWriter(new FileWriter("F:\\eclipse-Worksapce\\N_Puzzle\\text\\Result.txt"));
      // 将搜索过程写入:Result.txt
      flag = true;
    }
    if (this.isCompleted == true) {
      // 写入文件
      pw.println("Jigsaw completed");
      pw.println("Begin state:" + this.getBeginJNode().toString());
      pw.println("End state:" + this.getEndJNode().toString());
      pw.println("Solution Path is: ");
      pw.println(this.getSolutionPath());
      pw.println("The number of searched nodes are:" + this.getSearchedNodesNum());
      pw.println("The size of the solution path is:"
              + this.getCurrentJNode().getNodeDepth());

      // 输出到控制台
      System.out.println("Jigsaw completed");
      System.out.println("Begin state:" + this.getBeginJNode().toString());
      System.out.println("End state:" + this.getEndJNode().toString());
      System.out.println("Solution Path is: ");
      System.out.println(this.getSolutionPath());
      System.out.println("The all number of searched nodes are:"
              + this.getSearchedNodesNum());
      System.out.println("The size of the solution path is:"
              + this.getCurrentJNode().getNodeDepth());

    } else {
      // 写入文件
      pw.println("No solution. Jigsaw is not completed,please cheak.");
      pw.println("Begin state:" + this.getBeginJNode().toString());
      pw.println("End state:" + this.getEndJNode().toString());
      pw.println("The all number of searched nodes are:" + this.getSearchedNodesNum());

      // 输出到控制台
      System.out.println("No solution. Jigsaw is not completed,pleased cheak.");
      System.out.println("Begin state:" + this.getBeginJNode().toString());
      System.out.println("End state:" + this.getEndJNode().toString());
      System.out.println("The number of searched nodes is:"
              + this.getSearchedNodesNum());
    }
    if (flag)
      pw.close();
  }

  /**
   * 探索所有与jNode邻接(上、下、左、右)且未曾被访问的节点
   *
   * @param jNode - 要探索的节点
   * @return 包含所有与jNode邻接且未曾被访问的节点的Vector<JigsawNode>对象
   */
  private Vector<JigsawNode> findFollowJNodes(JigsawNode jNode) {
    Vector<JigsawNode> followJNodes = new Vector<JigsawNode>();
    JigsawNode tempJNode;
    for (int i = 0; i < 4; i++) {
      tempJNode = new JigsawNode(jNode);
      if (tempJNode.move(i) && !this.closeList.contains(tempJNode)
              && !this.openList.contains(tempJNode))
        followJNodes.addElement(tempJNode);
    }
    return followJNodes;
  }

  /**
   * 排序插入openList：按照节点的代价估值（estimatedValue）将节点插入openList中，估值小的靠前。
   *
   * @param jNode - 要插入的状态节点
   */
  private void sortedInsertOpenList(JigsawNode jNode) {
    this.estimateValue(jNode);
    for (int i = 0; i < this.openList.size(); i++) {
      if (jNode.getEstimatedValue() < this.openList.elementAt(i)
              .getEstimatedValue()) {
        this.openList.insertElementAt(jNode, i);
        return;
      }
    }
    this.openList.addElement(jNode);
  }

  // ****************************************************************
  // *************************实验任务************************
  /**
   * 实验任务一：广度优先搜索算法，求指定3*3拼图（8-数码问题）的最优解
   * 要求：填充广度优先搜索算法BFSearch()，执行测试脚本RunnerPart1 主要涉及函数：BFSearch()
   */
  /**
   * 实验任务二：启发式搜索算法，求解随机5*5拼图（24-数码问题）
   * 要求：1.修改启发式搜索算法ASearch()和代价估计函数estimateValue()，执行测试脚本RunnerPart2
   * 2.访问节点总数不超过29000个，主要涉及函数：ASearch()，estimateValue()
   */
  // ****************************************************************

  /**
   * （实验一）广度优先搜索算法，求解指定3*3拼图（8-数码问题）的最优解。 要求函数结束后： 1,isCompleted记录了求解完成状态；
   * 2,closeList记录了所有访问过的节点； 3,searchedNodesNum记录了访问过的节点数； 4,solutionPath记录了解路径。
   * 将起始节点放入一个open列表中。</br>
	如果open列表为空，则搜索失败，问题无解；否则重复以下步骤：
	a. 访问open列表中的第一个节点v，若v为目标节点，则搜索成功，退出。
	b. 从open列表中删除节点v，放入close列表中。
	c. 将所有与v邻接且未曾被访问的节点放入open列表中。
   *
   * @return isCompleted, 搜索成功时为true,失败为false
   * @throws IOException
   * 
   */
  public boolean BFSearch() throws IOException {
    // 将搜索过程写入:BFSearch.txt
    String filePath = "F:\\eclipse-Worksapce\\N_Puzzle\\text\\BFSearch.txt";
    PrintWriter pw = new PrintWriter(new FileWriter(filePath));
    // *************************************

    // must update the currentNode
    currentJNode = beginJNode;
    openList.addElement(currentJNode);
    while (!openList.isEmpty()) {
      // can't use equal operator '=' for it compares pointer rather than the
      // object
      if (currentJNode.equals(endJNode)) {
        isCompleted = true;
        calSolutionPath();
        break;
      } else {
        Vector<JigsawNode> child = findFollowJNodes(currentJNode);
        for (JigsawNode ch : child) {
          openList.addElement(ch);
        }
        openList.removeElement(currentJNode);
        closeList.addElement(currentJNode);
        currentJNode = openList.firstElement();
        searchedNodesNum++;
      }
    }

    // *************************************
    this.printResult(pw);
    pw.close();
    System.out.println("Result writting into " + filePath);
    return isCompleted;
  }

  /**
   * （Demo+实验二）启发式搜索。
   * 启发式搜索（如A*算法）:</br>
   * 利用问题拥有的启发信息来引导搜索，动态地确定搜索节点的排序，以达到减少搜索范围，降低问题复杂度的目的。
   * 在N-数码问题中，每搜索到每一个节点时，通过”估价函数”对该节点进行"评估”，然后优先访问”最优良”节点的邻接节点，能够大大减少求解的时间。</br>
   * 访问节点数大于30000个则认为搜索失败。 函数结束后：isCompleted记录了求解完成状态；
   * closeList记录了所有访问过的节点； searchedNodesNum记录了访问过的节点数； solutionPath记录了解路径。
   * 搜索过程和结果会记录在:ASearch.txt中。
   *
   * @return 搜索成功返回true, 失败返回false
   * @throws IOException
   */
  public boolean ASearch(String dimension) throws IOException {
    // 将搜索过程写入ASearch.txt
    String filePath = "F:\\eclipse-Worksapce\\N_Puzzle\\text\\ASearch_"+dimension+"x"+dimension+".txt";
    PrintWriter pw = new PrintWriter(new FileWriter(filePath));

    // 访问节点数大于29000个则认为搜索失败
    int maxNodesNum = 29000;

    // 用以存放某一节点的邻接节点
    Vector<JigsawNode> followJNodes = new Vector<JigsawNode>();

    // 重置求解完成标记为false
    isCompleted = false;

    // (1)将起始节点放入openList中
    this.sortedInsertOpenList(this.beginJNode);

    // (2) 如果openList为空，或者访问节点数大于maxNodesNum个，则搜索失败，问题无解;否则循环直到求解成功
    while (!this.openList.isEmpty() && searchedNodesNum <= maxNodesNum) {

      // (2-1)访问openList的第一个节点N，置为当前节点currentJNode
      // 若currentJNode为目标节点，则搜索成功，设置完成标记isCompleted为true，计算解路径，退出。
      this.currentJNode = this.openList.elementAt(0);
      if (this.currentJNode.equals(this.endJNode)) {
        isCompleted = true;
        this.calSolutionPath();
        break;
      }

      // (2-2)从openList中删除节点N,并将其放入closeList中，表示以访问节点
      this.openList.removeElementAt(0);
      this.closeList.addElement(this.currentJNode);
      searchedNodesNum++;

      // 记录并显示搜索过程
      pw.println("Searching the "+this.closeList.size()+"th times nodes:"
              +"   Current state:"
              + this.currentJNode.toString());
      System.out.println("Searching the "+this.closeList.size()+"th times nodes:"
              + "   Current state:"
              + this.currentJNode.toString());

      // (2-3)寻找所有与currentJNode邻接且未曾被访问的节点，将它们按代价估值从小到大排序插入openList中
      followJNodes = this.findFollowJNodes(this.currentJNode);
      while (!followJNodes.isEmpty()) {
        this.sortedInsertOpenList(followJNodes.elementAt(0));
        followJNodes.removeElementAt(0);
      }
    }

    this.printResult(pw); // 记录搜索结果
    pw.close(); // 关闭输出文件
    System.out.println("Result writting into " + filePath);
    return isCompleted;
  }

  /**
   * （Demo+实验二）计算并修改状态节点jNode的代价估计值:</br>
   * 使用方法：
   * 1.后续节点不正确的数码个数:uncorrect(n)</br>
   * 2.所有放错位的数码与其正确位置的距离之和:depth(n)</br>
   * 3.当前位置节点到末位位置节点的曼哈顿距离manhattan(n)</br>
   * f(n)=uncorrect(n)+depth(n)+Manhattan(n).
   *
   * @param jNode - 要计算代价估计值的节点；此函数会改变该节点的estimatedValue属性值。
   */
  
  //预估距离的函数
  private void estimateValue(JigsawNode jNode) {
    // 后续节点不正确的数码个数
    int uncorrectAfter = 0;
    int dimension = JigsawNode.getDimension();
    for (int index = 1; index < dimension * dimension; index++) {
      if (jNode.getNodesState()[index] + 1 != jNode.getNodesState()[index + 1])//当前位置的和后序的排序不正确
        uncorrectAfter++;
    }

    //当前的位置节点出错
    int uncorrectPiece = 0;
    for (int index = 1; index < dimension * dimension; index++) {
      if (jNode.getNodesState()[index] != endJNode.getNodesState()[index])
        uncorrectPiece++;
    }
    // 找到曼哈顿距离，注意是每个位置的曼哈顿距离的和
    int manhattanDistance = 0;
    int distance = 0;
    for (int currentIndex = 1; currentIndex < dimension * dimension; currentIndex++) {
      for (int targetIndex = 1; targetIndex < dimension * dimension; targetIndex++) {
        // 两重循环，找到每一个位置的距离，从(0,0)到最后一个节点
        int currentPiece = jNode.getNodesState()[currentIndex];
        int targetPiece = endJNode.getNodesState()[targetIndex];
        
        /*
         * 从上面的题目意思来看可以肯定的是，最终0值得节点一定会在拼图的末尾的位置，也就是在endNode的list的末尾的位置，
         * 所以第一个目标断然不会是0，开始检索计算：</br>
         */
        if (currentPiece != 0 && currentPiece == targetPiece) {
          int currentX = (currentIndex) / dimension;//将一维的坐标表达经过计算得到二维向量的坐标表达
          int currentY = (currentIndex) % dimension;//简单的线性转化成非线性坐标的方法
          int targetX = (targetIndex) / dimension;
          int targetY = (targetIndex) % dimension;
          int dx = Math.abs(currentX - targetX);
          int dy = Math.abs(currentY - targetY);
          manhattanDistance += (dx + dy);
          distance += Math.sqrt(dx * dx + dy * dy);
          break;
        }
      }
    }
    
    /*
     * 增加权重的时候，选择各个权重的互质，另外考虑到曼哈顿距离在不符合位置节点之间的权重几乎在榜首，所以圈子中稍微大一点 </br>
     * 后面的权重随意赋值，但是都要求按照权重的排序来完成，这个影响到具体的算法效率
     */
    int estimate = (int) (uncorrectAfter * 383 + manhattanDistance * 617 + distance * 201 + uncorrectPiece * 23);
    jNode.setEstimatedValue(estimate);
  }

}
