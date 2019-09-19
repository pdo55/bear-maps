import java.util.*;

public class Trie {
    protected class Node {
        protected Map<Character, Node> m;
        protected boolean exists;

        protected Node(boolean exists) {
            m = new HashMap<>();
            this.exists = exists;
        }
    }

    Node n;
    public Trie() {
        n = new Node(false);
    }

    public void insert(String s) {
        boolean exists = false;
        Node temp = n;
        for(int i = 0; i < s.length(); ++i) {
            if(i == s.length() - 1) {
                exists = true;
            }
            if(!temp.m.containsKey(s.charAt(i))) {
                temp.m.put(s.charAt(i), new Node(exists));
            }
            temp = temp.m.get(s.charAt(i));
        }
    }


    public List<String> getPrefix(String prefix) {
        Set<String> results = new HashSet<>();
        String s = prefix;
        Node temp = n;
        for(int i = 0; i < prefix.length(); ++i) {
            temp = temp.m.get(prefix.charAt(i));
//            System.out.println(prefix.charAt(i));
            if(temp == null) return new LinkedList<>();
        }
        helper(results, temp, s);
        return new LinkedList<>(results);
    }

    private void helper(Set<String> results, Node n, String sofar) {
        if(n == null) return;
        if(n.exists) results.add(sofar);

        for(Map.Entry<Character, Node> entry: n.m.entrySet()) {
            Node temp = entry.getValue();
            String s = sofar;
            helper(results, temp, s + entry.getKey());
        }
    }
}
