package com.airticket.common.util;

import java.util.Objects;

/**
 * Custom data structure: Generic Trie for efficient string searches
 * Useful for flight destination search, autocomplete features
 */
public class Trie<T> {
    private TrieNode<T> root;

    public Trie() {
        this.root = new TrieNode<>();
    }

    public void insert(String word, T value) {
        Objects.requireNonNull(word, "Word cannot be null");
        TrieNode<T> current = root;
        
        for (char ch : word.toLowerCase().toCharArray()) {
            current = current.children.computeIfAbsent(ch, k -> new TrieNode<>());
        }
        
        current.isEndOfWord = true;
        current.value = value;
    }

    public T search(String word) {
        if (word == null) return null;
        
        TrieNode<T> node = searchNode(word.toLowerCase());
        return (node != null && node.isEndOfWord) ? node.value : null;
    }

    public boolean startsWith(String prefix) {
        if (prefix == null) return false;
        return searchNode(prefix.toLowerCase()) != null;
    }

    private TrieNode<T> searchNode(String word) {
        TrieNode<T> current = root;
        
        for (char ch : word.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                return null;
            }
        }
        
        return current;
    }

    private static class TrieNode<T> {
        private java.util.Map<Character, TrieNode<T>> children;
        private boolean isEndOfWord;
        private T value;

        public TrieNode() {
            this.children = new java.util.HashMap<>();
            this.isEndOfWord = false;
        }
    }
}