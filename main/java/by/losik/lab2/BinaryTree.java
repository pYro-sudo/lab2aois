package by.losik.lab2;

import java.util.HashMap;

public class BinaryTree {
    private Boolean value;
    private String literal;
    private BinaryTree leftNode;
    private BinaryTree rightNode;

    public BinaryTree getLeftNode() {
        return leftNode;
    }

    public BinaryTree getRightNode() {
        return rightNode;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLeftNode(BinaryTree leftNode) {
        this.leftNode = leftNode;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public void setRightNode(BinaryTree rightNode) {
        this.rightNode = rightNode;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
