package ed.lab;

import java.util.*;

public class E02AutocompleteSystem {
    private StringBuilder thePrefix = new StringBuilder();
    Map<String, Integer> map = new HashMap<>();
    Trie trie = new Trie();

    public E02AutocompleteSystem(String[] sentences, int[] times) {
        for (int i = 0; i < sentences.length; i++) {
            map.put(sentences[i], map.getOrDefault(sentences[i], 0) + times[i]);
            trie.insert(sentences[i]);
        }
    }

    public List<String> input(char c) {
        if (c == '#') {
            String sentence = thePrefix.toString();
            map.put(sentence, map.getOrDefault(sentence, 0) + 1);
            trie.insert(sentence);
            thePrefix.setLength(0);
            return List.of();
        }

        thePrefix.append(c);
        String prefix = thePrefix.toString();
        List<String> candidates = trie.getWordsWithPrefix(prefix);

        PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> {
            int freqCompare = map.get(b) - map.get(a);
            return freqCompare != 0 ? freqCompare : a.compareTo(b);
        });

        for (String s : candidates) {
            pq.offer(s);
        }

        List<String> result = new ArrayList<>();
        for (int i = 0; i < 3 && !pq.isEmpty(); i++) {
            result.add(pq.poll());
        }

        return result;
    }

    public static class Node {
        public Map<Character, Node> children = new HashMap<>();
        public boolean isLast = false;
        public String word = null;
    }

    public static class Trie {
        private final Node root;

        public Trie() {
            root = new Node();
        }

        public void insert(String word) {
            Node current = root;
            for (char c : word.toCharArray()) {
                current.children.putIfAbsent(c, new Node());
                current = current.children.get(c);
            }
            current.isLast = true;
            current.word = word;
        }

        public boolean search(String word) {
            Node current = root;
            for (char c : word.toCharArray()) {
                if (!current.children.containsKey(c)) {
                    return false;
                }
                current = current.children.get(c);
            }
            return current.isLast;
        }

        public List<String> getWordsWithPrefix(String prefix) {
            List<String> result = new ArrayList<>();
            Node current = root;
            for (char c : prefix.toCharArray()) {
                if (!current.children.containsKey(c)) {
                    return result;
                }
                current = current.children.get(c);
            }
            dfs(current, result);
            return result;
        }

        private void dfs(Node node, List<String> result) {
            if (node.isLast && node.word != null) {
                result.add(node.word);
            }
            for (Node child : node.children.values()) {
                dfs(child, result);
            }
        }
    }
}

