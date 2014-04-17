package net.uaprom.jmorphy2.nlp;

import java.util.Set;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList;


public class Rule {
    public final String leftStr;
    public final String rightStr;
    public final ImmutableSet<String> left;
    public final ImmutableList<NodeMatcher> right;
    public final int rightSize;
    public final float weight;

    private static Splitter grammemeSplitter = Splitter.on(",").trimResults(CharMatcher.anyOf("@!"));
    private static Splitter rhsSplitter = Splitter.on(" ").trimResults();
    private static CharMatcher wordMatcher = CharMatcher.anyOf("'\"");

    public Rule(String left, String right, float weight) {
        this.leftStr = left;
        this.rightStr = right;
        this.left = parseLeft(left);
        this.right = parseRight(right);
        this.rightSize = this.right.size();
        this.weight = weight;
    }

    protected ImmutableSet<String> parseLeft(String left) {
        return ImmutableSet.copyOf(grammemeSplitter.split(left));
    }

    protected ImmutableList<NodeMatcher> parseRight(String right) {
        ImmutableList.Builder<NodeMatcher> listBuilder = ImmutableList.builder();
        for (String part : rhsSplitter.split(right)) {
            int flags = 0;
            if (part.startsWith("@")) {
                flags |= NodeMatcher.NO_COMMONS;
            }
            if ((part.startsWith("'") && part.endsWith("'")) ||
                (part.startsWith("\"") && part.endsWith("\""))) {
                listBuilder.add(new NodeMatcher(null, wordMatcher.trimFrom(part), flags));
            } else {
                listBuilder.add(new NodeMatcher(ImmutableSet.copyOf(grammemeSplitter.split(part)), null, flags));
            }
        }
        return listBuilder.build();
    }

    public boolean match(List<Node> nodes) {
        int n = right.size();
        if (nodes.size() != n) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            if (!right.get(i).match(nodes.get(i))) {
                return false;
            }
        }
        return true;
    }

    public ImmutableSet<String> commonGrammemeValues(List<Node> nodes, Set<String> allowedValues) {
        Set<String> values = null;
        int i = 0;
        for (Node node : nodes) {
            if ((right.get(i).flags & NodeMatcher.NO_COMMONS) != 0) {
                i++;
                continue;
            }
            if (values == null) {
                values = node.grammemeValues;
            } else {
                values = Sets.intersection(values, node.grammemeValues);
            }
            i++;
        }
        values = Sets.intersection(values, allowedValues);
        values = Sets.union(values, left);
        return ImmutableSet.copyOf(values);
    }

    public Node apply(ImmutableList<Node> nodes) {
        ImmutableList<Node> reducedNodes = nodes.subList(0, rightSize);
        return new Node(left, reducedNodes, Node.calcScore(reducedNodes));
    }

    @Override
    public String toString() {
        return String.format("%s -> %s [%s]", leftStr, rightStr, weight);
    }

    public static class NodeMatcher {
        private final ImmutableSet<String> grammemeValues;
        private final int flags;
        private final String word;

        private final static int NO_COMMONS = 1;

        public NodeMatcher(ImmutableSet<String> grammemeValues, String word, int flags) {
            this.grammemeValues = grammemeValues;
            this.word = word;
            this.flags = flags;
        }

        public boolean match(Node node) {
            return node.match(grammemeValues, word);
        }
    };
}
