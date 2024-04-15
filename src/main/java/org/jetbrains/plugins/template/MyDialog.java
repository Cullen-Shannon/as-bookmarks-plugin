package org.jetbrains.plugins.template;

import com.intellij.icons.AllIcons;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.*;

/*
    Preliminary UI work
    Need to figure out how to load the file spec here -- hardcoding for now so I could work on the UI
    The UI needs some work -- particularly icons as buttons, but this is a decent start
    Need to implement callbacks for all the buttons
    Need to set focus on the title text when a given node is selected
    Must we use Java for this? I'm finding it tough to locate updated tutorials for this flow
    Probably more...
 */
public class MyDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTree tree1;
    private JButton addButton;
    private JButton removeButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JTextField textField1;
    private JTextField textField2;

    public MyDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // https://jetbrains.design/intellij/resources/icons_list/
        // these don't look great -- might be a better way
        moveDownButton.setIcon(AllIcons.Actions.MoveDown);
        moveUpButton.setIcon(AllIcons.Actions.MoveUp);
        removeButton.setIcon(AllIcons.Diff.Remove);
        addButton.setIcon(AllIcons.FileTypes.AddAny);

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
        tree1.setModel(tree.getModel());

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        MyDialog dialog = new MyDialog();
        dialog.pack();
        dialog.setLocationRelativeTo(null); // centers
        dialog.setVisible(true);
        //System.exit(0);
    }
}
