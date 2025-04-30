import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 主类 Lab1，实现文本有向图分析工具，包含以下功能：
 * 1. 从文件构建有向图
 * 2. 展示有向图结构
 * 3. 查询桥接词
 * 4. 生成新文本（插入桥接词）
 * 5. 计算最短路径（Dijkstra算法）
 * 6. 计算PageRank值
 * 7. 随机游走
 */
public class Lab1 {
    static Graph graph;
    /**
     * 内部类 Graph，表示有向图数据结构
     * - adjacencyList: 邻接表，存储每个节点的出边及权重（格式：源节点 -> {目标节点: 出现次数}）
     * - incomingEdges: 入边表，存储每个节点的入边来源（格式：目标节点 -> [源节点列表]）
     */
    static class Graph {
        Map<String, Map<String, Integer>> adjacencyList = new HashMap<>();
        Map<String, List<String>> incomingEdges = new HashMap<>();

        /**
         * 添加有向边（自动转为小写）
         * @param source 源节点
         * @param target 目标节点
         */
        void addEdge(String source, String target) {
            //全部转化为小写
            source = source.toLowerCase();
            target = target.toLowerCase();
            // 初始化邻接表和入边表,不存在则插入空
            adjacencyList.putIfAbsent(source, new HashMap<>());
            incomingEdges.putIfAbsent(target, new ArrayList<>());

            // 更新出边权重（出现次数+1）
            adjacencyList.get(source).put(target, adjacencyList.get(source).getOrDefault(target, 0) + 1);
            // 更新入边列表
            incomingEdges.get(target).add(source);
        }

        /**
         * 检查图中是否包含某个节点（不区分大小写）
         */
        boolean containsNode(String word) {
            String lowerWord = word.toLowerCase();
            return adjacencyList.containsKey(lowerWord) || incomingEdges.containsKey(lowerWord);
        }
    }

    // ---------------------- 功能1：读取文件并构建图 ----------------------

    /**
     * 从文件构建有向图
     * @param filePath 文件路径
     * @return 构建完成的有向图对象
     */
    public static Graph buildGraph(String filePath) throws IOException {
        // 读取文件全部内容
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        // 处理文本为单词列表
        List<String> words = processText(content);
        Graph graph = new Graph();

        // 遍历相邻单词，添加有向边
        for (int i = 0; i < words.size() - 1; i++) {
            String current = words.get(i);
            String next = words.get(i + 1);
            if (!current.isEmpty() && !next.isEmpty()) {
                graph.addEdge(current, next);
            }
        }
        return graph;
    }

    /**
     * 文本预处理：去除非字母字符，分割为单词列表（全小写）
     */
    private static List<String> processText(String text) {
        // 用空格替换所有非字母字符，并转为小写
        String processed = text.replaceAll("[^a-zA-Z]", " ").toLowerCase();
        // 按空格分割，过滤空字符串
        return Arrays.stream(processed.split("\\s+"))
                     .filter(word -> !word.isEmpty())
                     .toList();
    }

    // ---------------------- 功能2：展示有向图 ----------------------

    /**
     * 打印有向图结构（邻接表形式）
     */
    public static void showDirectedGraph() {
        for (String node : graph.adjacencyList.keySet()) {
            System.out.print(node + " -> ");
            // 格式：目标节点(出现次数)
            graph.adjacencyList.get(node).forEach(
                (target, weight) -> System.out.print(target + "(" + weight + ") ")
            );
            System.out.println();
        }
    }

    /**
     * 将有向图保存为图片文件（需系统安装Graphviz）
     * @param graph      图对象
     * @param outputPath 输出图片路径（如 "graph.png"）
     */
    //public static void saveGraphToImage(String outputPath) throws IOException, InterruptedException {
    public static void showDirectedGraph1() throws IOException, InterruptedException {
        // 生成DOT格式内容
        String outputPath = "C:\\Users\\hyh\\Desktop\\Software\\111.png";
        StringBuilder dot = new StringBuilder("digraph G {\n");
        for (String source : graph.adjacencyList.keySet()) {
            Map<String, Integer> edges = graph.adjacencyList.get(source);
            for (Map.Entry<String, Integer> entry : edges.entrySet()) {
                String target = entry.getKey();
                int weight = entry.getValue();
                // 添加边及权重标签
                dot.append(String.format("    \"%s\" -> \"%s\" [label=\"%d\"];\n", 
                        source, target, weight));
            }
        }
        dot.append("}");

        // 创建临时DOT文件
        Path dotFile = Files.createTempFile("graph", ".dot");
        Files.writeString(dotFile, dot.toString());

        // 调用系统命令生成图片
        ProcessBuilder processBuilder = new ProcessBuilder(
            "dot", "-Tpng", dotFile.toString(), "-o", outputPath
        );
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        // 清理临时文件
        Files.deleteIfExists(dotFile);
        
        if (exitCode != 0) {
            throw new IOException("Graphviz生成图片失败，请检查是否安装并配置PATH。");
        }else{
            System.out.println("Graph saved to: " + outputPath);
        }
    }




    // ---------------------- 功能3：查询桥接词 ----------------------

    /**
     * 查找从word1到word2的桥接词（即满足 word1 -> bridge -> word2 的单词）
     * @return 格式化结果字符串
     */
    public static String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        // 检查节点是否存在
        if (!graph.containsNode(word1) || !graph.containsNode(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        // 获取word1的所有出边目标，word2的所有入边来源
        Set<String> bridges = new HashSet<>();
        Map<String, Integer> outEdges = graph.adjacencyList.getOrDefault(word1, new HashMap<>());
        List<String> inEdges = graph.incomingEdges.getOrDefault(word2, new ArrayList<>());

        // 交集即为桥接词
        outEdges.keySet().stream()
                .filter(inEdges::contains)
                .forEach(bridges::add);

        // 结果格式化
        if (bridges.isEmpty()) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        } else {
            List<String> bridgeList = new ArrayList<>(bridges);
            //StringBuilder result=new StringBuilder();
            //StringBuilder result = new StringBuilder("The bridge words from " + word1 + " to " + word2 + " are: ");

            StringBuilder result = new StringBuilder();
            if (bridgeList.size() == 1) {
                // 单桥接词：is + "word"
                result.append("The bridge words from \"")
                        .append(word1)
                        .append("\" to \"")
                        .append(word2)
                        .append("\" is: \"")
                        .append(bridgeList.get(0))
                        .append("\".");
            } else {
                // 多桥接词：are + "word1", "word2", and "word3"
                result.append("The bridge words from \"")
                        .append(word1)
                        .append("\" to \"")
                        .append(word2)
                        .append("\" are: ");
                
                for (int i = 0; i < bridgeList.size(); i++) {
                    // 所有单词用引号包裹
                    result.append("\"").append(bridgeList.get(i)).append("\"");
                    
                    // 处理逗号和and
                    if (i == bridgeList.size() - 1) {
                        result.append(".");  // 最后以句号结尾
                    } else if (i == bridgeList.size() - 2) {
                        result.append(", and ");  // 倒数第二个单词后加", and"
                    } else {
                        result.append(", ");  // 其他情况加逗号
                    }
                }
            }
            return result.toString();     
        }
    }

    // ---------------------- 功能4：生成新文本 ----------------------

    /**
     * 在输入文本的相邻单词间插入随机桥接词
     * @return 生成的新文本
     */
    public static String generateNewText(String inputText) {
        List<String> words = processText(inputText);
        List<String> result = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < words.size() - 1; i++) {
            String current = words.get(i);
            String next = words.get(i + 1);
            result.add(current);

            // 查找桥接词并随机插入
            List<String> bridges = findBridgeWords(current, next);
            if (!bridges.isEmpty()) {
                result.add(bridges.get(rand.nextInt(bridges.size())));
            }
        }
        result.add(words.get(words.size() - 1)); // 添加最后一个词
        return String.join(" ", result);
    }

    /**
     * 辅助方法：查找两个单词间的桥接词列表
     */
    private static List<String> findBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        List<String> bridges = new ArrayList<>();

        if (graph.containsNode(word1) && graph.containsNode(word2)) {
            // 与queryBridgeWords相同逻辑，但返回列表而非格式化字符串
            Map<String, Integer> outEdges = graph.adjacencyList.getOrDefault(word1, new HashMap<>());
            List<String> inEdges = graph.incomingEdges.getOrDefault(word2, new ArrayList<>());
            outEdges.keySet().stream()
                    .filter(inEdges::contains)
                    .forEach(bridges::add);
        }
        return bridges;
    }

    // ---------------------- 功能5：计算最短路径（Dijkstra算法） ----------------------

    /**
     * 使用Dijkstra算法计算两节点间最短路径（权重为出现次数，越小优先级越高）
     * @return 路径描述字符串或错误信息
     */
    public static String calcShortestPath(String word1, String word2) {
        String source = word1.toLowerCase();
        String target = (word2 == null || word2.isEmpty()) ? null : word2.toLowerCase();
    
        // 检查源节点是否存在
        if (!graph.containsNode(source)) {
            return "Error: Source word '" + word1 + "' not found!";
        }
    
        // ---------------------- Dijkstra算法核心逻辑 ----------------------
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(node -> dist.getOrDefault(node, Integer.MAX_VALUE)));
    
        // 初始化所有节点距离为无穷大
        graph.adjacencyList.keySet().forEach(node -> dist.put(node, Integer.MAX_VALUE));
        dist.put(source, 0);
        queue.add(source);
    
        // 执行Dijkstra算法（计算到所有节点的最短路径）
        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDist = dist.get(current);
    
            // 遍历邻居
            for (Map.Entry<String, Integer> edge : graph.adjacencyList.getOrDefault(current, new HashMap<>()).entrySet()) {
                String neighbor = edge.getKey();
                int newDist = currentDist + edge.getValue();
    
                if (newDist < dist.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    if (queue.contains(neighbor)) queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    
        // ---------------------- 结果生成逻辑 ----------------------
        StringBuilder result = new StringBuilder();
    
        if (target != null) {  // 处理两个单词的路径查询
            if (!graph.containsNode(target)) {
                return "Error: Target word '" + word2 + "' not found!";
            }
    
            if (dist.get(target) == Integer.MAX_VALUE) {
                return "No path from '" + word1 + "' to '" + word2 + "'!";
            }
    
            LinkedList<String> path = new LinkedList<>();
            for (String node = target; node != null; node = prev.get(node)) {
                path.addFirst(node);
            }
            result.append(formatSinglePath(word1, word2, path, dist.get(target)));
        } else {  // 处理单个单词的全图路径查询
            result.append("Shortest paths from '").append(word1).append("':\n");
            for (String node : graph.adjacencyList.keySet()) {
                if (node.equals(source)) continue;  // 跳过自身
    
                if (dist.get(node) == Integer.MAX_VALUE) {
                    result.append("  To '").append(node).append("': No path!\n");
                    continue;
                }
    
                LinkedList<String> path = new LinkedList<>();
                for (String n = node; n != null; n = prev.get(n)) {
                    path.addFirst(n);
                }
                result.append("  To '").append(node)
                       .append("': ").append(String.join(" → ", path))
                       .append(" (Length: ").append(dist.get(node)).append(")\n");
            }
        }
    
        return result.toString().trim();
    }
    
    /**
     * 格式化单个路径输出
     */
    private static String formatSinglePath(String source, String target, List<String> path, int length) {
        return String.format("Shortest path from '%s' to '%s': %s (Length: %d)",
                source, target, String.join(" → ", path), length);
    }

    // ---------------------- 功能6：计算PageRank ----------------------

    /**
     * 计算指定单词的PageRank值（迭代100次）
     * @param word 目标单词
     * @return PageRank值，未找到时返回0.0
     */
    public static double calcPageRank(String word) {
        final double d = 0.85;
        int iterations = 100;
        Map<String, Double> pr = new HashMap<>();
        int N = graph.adjacencyList.size();

        // ---------------------- 初始化PR值 ----------------------
        // 高级模式：基于入度 + 1（拉普拉斯平滑）分配初始PR值
        double sumInDegrees = graph.adjacencyList.keySet().stream()
            .mapToDouble(node -> graph.incomingEdges.getOrDefault(node, Collections.emptyList()).size() + 1.0)
            .sum();
        
        for (String node : graph.adjacencyList.keySet()) {
            int inDegree = graph.incomingEdges.getOrDefault(node, Collections.emptyList()).size();
            pr.put(node, (inDegree + 1.0) / sumInDegrees);
        }
        // 默认模式：均匀初始化
        //for (String node : graph.adjacencyList.keySet()) {
        //    pr.put(node, 1.0 / N);
        //}

        // ---------------------- PageRank迭代 ----------------------
        for (int i = 0; i < iterations; i++) {
            final Map<String, Double> currentPr = new HashMap<>(pr);
            Map<String, Double> newPr = new HashMap<>();

            for (String node : graph.adjacencyList.keySet()) {
                double sum = graph.incomingEdges.getOrDefault(node, new ArrayList<>()).stream()
                        .mapToDouble(v -> currentPr.getOrDefault(v, 0.0) / graph.adjacencyList.get(v).size())
                        .sum();
                newPr.put(node, (1 - d) / N + d * sum);
            }
            pr = newPr;
        }

        return pr.getOrDefault(word.toLowerCase(), 0.0);
    }


    // ---------------------- 功能7：随机游走 ----------------------

    /**
     * 随机游走：从随机节点出发，每次随机选择出边，直到重复边或无法继续
     * @return 游走路径的字符串（空格分隔）
     */
    public static String randomWalk() {
        List<String> nodes = new ArrayList<>(graph.adjacencyList.keySet());
        if (nodes.isEmpty()) return "";

        Random rand = new Random();
        String current = nodes.get(rand.nextInt(nodes.size())); // 随机起点
        List<String> path = new ArrayList<>();
        Set<String> visitedEdges = new HashSet<>(); // 记录已访问边

        path.add(current);
        while (true) {
            Map<String, Integer> edges = graph.adjacencyList.get(current);
            if (edges == null || edges.isEmpty()) break; // 无出边时终止

            // 随机选择下一个节点
            List<String> targets = new ArrayList<>(edges.keySet());
            String next = targets.get(rand.nextInt(targets.size()));
            String edge = current + "->" + next;

            if (visitedEdges.contains(edge)) break; // 遇到重复边时终止
            visitedEdges.add(edge);

            path.add(next);
            current = next;
        }
        return String.join(" ", path);
    }

    // ---------------------- 主程序入口 ----------------------

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // 测试用固定路径（实际使用时取消下一行注释，并注释掉再下一行）
        // System.out.print("Enter file path: ");
        // String path = scanner.nextLine();
        String path = "C:\\Users\\hyh\\Desktop\\Software\\Easy Test.txt"; // 测试路径

        try {
            graph = buildGraph(path);
            showDirectedGraph(); // 初始展示图结构

            // 功能选择循环
            while (true) {
                System.out.println("\nChoose function:");
                System.out.println("1. show graph in image");  // 新增选项
                System.out.println("2. Query bridge words");
                System.out.println("3. Generate new text");
                System.out.println("4. Calculate shortest path");
                System.out.println("5. Calculate PageRank");
                System.out.println("6. Random walk");
                System.out.println("0. Exit");
                System.out.print("Input: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // 清除输入缓冲区的换行符

                switch (choice) {
                    case 1: // 保存图形到文件（新增功能）
                        //System.out.print("Enter output image path (e.g., graph.png): ");
                        try {
                            showDirectedGraph1();
                        } catch (IOException | InterruptedException e) {
                            System.err.println("保存失败: " + e.getMessage());
                        }
                        break;
                    case 2: // 查询桥接词
                        System.out.print("Enter two words (separated by space): ");
                        String[] words = scanner.nextLine().split(" ");
                        System.out.println(queryBridgeWords(words[0], words[1]));
                        break;
                    case 3: // 生成新文本
                        System.out.print("Enter text: ");
                        String input = scanner.nextLine();
                        System.out.println("New text: " + generateNewText(input));
                        break;
                    case 4: // 计算最短路径
                        System.out.print("Enter one or two words (e.g. 'to and' or 'to'): ");
                        String[] inputs = scanner.nextLine().split("\\s+", 2);  // 最多分割为两部分
                    
                        if (inputs.length == 0) {
                            System.out.println("Invalid input!");
                            break;
                        }               
                        String word2 = (inputs.length >= 2) ? inputs[1] : "";
                        System.out.println(calcShortestPath(inputs[0], word2));
                        break;
                    case 5: // 计算PageRank
                        System.out.print("Enter word: ");
                        String word = scanner.nextLine();
                        System.out.println("PageRank: " + calcPageRank(word));
                        break;
                    case 6: // 随机游走
                        System.out.println("Random walk: " + randomWalk());
                        break;
                    case 0: // 退出
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        }catch(IOException e){
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}