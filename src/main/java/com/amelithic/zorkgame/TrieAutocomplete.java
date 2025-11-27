package com.amelithic.zorkgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
}

public class TrieAutocomplete {
    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            node = node.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        node.isEndOfWord = true;
    }

    public void delete(String word) {
        deleteHelper(root, word, 0);
    }

    private boolean deleteHelper(TrieNode node, String word, int index) {
        if (index == word.length()) {
            if (!node.isEndOfWord) return false;
            node.isEndOfWord = false;
            return node.children.isEmpty();
        }

        char ch = word.charAt(index);
        TrieNode child = node.children.get(ch);
        if (child == null) return false;

        boolean shouldDeleteCurrentNode = deleteHelper(child, word, index + 1);

        if (shouldDeleteCurrentNode) {
            node.children.remove(ch);
            return node.children.isEmpty() && !node.isEndOfWord;
        }

        return false;
    }

    //TODO: add case insensitivity

    public List<String> search(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = root;

        for (char ch : prefix.toCharArray()) {
            node = node.children.get(ch);
            if (node == null) return results;
        }

        collectWords(node, prefix, results);
        return results;
    }

    private void collectWords(TrieNode node, String prefix, List<String> results) {
        if (node.isEndOfWord) results.add(prefix);
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            collectWords(entry.getValue(), prefix + entry.getKey(), results);
        }
    }
}