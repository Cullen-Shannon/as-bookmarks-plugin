package org.jetbrains.plugins.template;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField myUserNameText = new JBTextField();
    private final JBCheckBox myIdeaUserStatus = new JBCheckBox("Do you use IntelliJ IDEA? ");

    private final JTree myTree;

    public AppSettingsComponent() {

        // https://www.codejava.net/java-se/swing/jtree-basic-tutorial-and-examples#Adding_a_Scrollpane:
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("NFCU");
        DefaultMutableTreeNode adoNode = new DefaultMutableTreeNode("ADO");
        DefaultMutableTreeNode riseNode = new DefaultMutableTreeNode("Rise Modules");
        riseNode.add(new DefaultMutableTreeNode("Environmental Setup"));
        riseNode.add(new DefaultMutableTreeNode("Emulator Setup"));
        root.add(adoNode);
        root.add(riseNode);
        Tree tree = new Tree(root);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(true);
        myTree = new com.intellij.ui.treeStructure.Tree();
        myTree.setDragEnabled(true);
        myTree.setModel(tree.getModel());

        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Enter user name: "), myUserNameText, 1, false)
                .addComponent(myIdeaUserStatus, 1)
                .addComponent(myTree)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return myTree;
    }

    @NotNull
    public String getUserNameText() {
        return myUserNameText.getText();
    }

    public void setUserNameText(@NotNull String newText) {
        myUserNameText.setText(newText);
    }

    public boolean getIdeaUserStatus() {
        return myIdeaUserStatus.isSelected();
    }

    public void setIdeaUserStatus(boolean newStatus) {
        myIdeaUserStatus.setSelected(newStatus);
    }

}