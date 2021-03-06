/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.vipaol.mg.editor;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author vipaol
 */
public class MobapGameEditor extends JFrame implements Runnable {

    JPanel btnPanel;
    JButton addLine;
    JList listOfPlaced;
    JPanel rightPanel;
    JPanel editorPanel = new JPanel();

    private static final int millis = 200;
    boolean stopped = false;
    EditorCanvas c = new EditorCanvas();
    JPanel rightBtnPanel;
    JPanel rightBtnPanel2nd;
    int w = 800;
    int h = 600;
    int rightPanelW = w / 5;
    int editorPanelW = w - rightPanelW;
    int topH = h * 10 / 158;
    int bottH = h - topH;

    public MobapGameEditor() {
        super("Mobap-game editor");

        setSize(w, h);
        setMinimumSize(new Dimension(300, 200));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        editorPanel.setLayout(new BorderLayout());
        btnPanel = new JPanel();

        JButton load = new JButton("Load");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(new JFrame(), "File name", c.mgStruct.name);
                if (name != null) {
                	c.mgStruct.name = name;
	                c.mgStruct.loadFile();
	                c.selected = c.mgStruct.length - 1;
                }
            }
        });

        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.mgStruct.saveToFile();
            }
        });
        
        JButton saveAs = new JButton("Save as");
        saveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String name = JOptionPane.showInputDialog(new JFrame(), "File name", c.mgStruct.name);
                if (name != null) {
                	c.mgStruct.name = name;
                	c.mgStruct.saveToFile();
                }
            }
        });

        addLine = new JButton("Add line");
        addLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println("line");
                c.elements.place(2);
            }
        });

        JButton addCircle = new JButton("Add circle");
        addCircle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.elements.place(3);
            }
        });
        
        JButton breakblLine = new JButton("Breakable line");
        breakblLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.elements.place(4);
            }
        });


        ///////////////////////// RIGHT
        rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        //rightBtnPanel2nd = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        JButton deleteShape = new JButton("Delete");
        deleteShape.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.elements.delete(c.selected);
            }
        });

        JButton editShape = new JButton("Edit");
        editShape.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                edit(c.selected);
            }
        });
        
        JButton moveToStart = new JButton("Move all to 0 0");
        moveToStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.elements.moveAllToStartPoint();
            }
        });
        JButton edit1 = new JButton("Move point 1");
        edit1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println("move");
            	c.elements.edit(c.selected, 1);
            }
        });
        JButton edit2 = new JButton("Move point 2");
        edit2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println("move");
                c.elements.edit(c.selected, 2);
            }
        });

        listOfPlaced = new JList();
        listOfPlaced.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                c.selected = list.locationToIndex(evt.getPoint());
                c.selectedInList = c.selected;
                if (evt.getClickCount() == 2) {
                    edit(c.selected);
                    // Double-click detected
                } else if (evt.getClickCount() == 3) {

                    // Triple-click detected
                    //c.selected = list.locationToIndex(evt.getPoint());
                }
                c.repaint();
            }
        });

        btnPanel.add(load);
        btnPanel.add(save);
        btnPanel.add(saveAs);
        btnPanel.add(addLine);
        btnPanel.add(addCircle);
        btnPanel.add(breakblLine);
        
        rightBtnPanel.add(deleteShape);
        rightBtnPanel.add(editShape);
        //rightBtnPanel2nd.add(edit1);
        //rightBtnPanel2nd.add(edit2);
        rightPanel.add(rightBtnPanel, BorderLayout.NORTH);
        //rightPanel.add(rightBtnPanel2nd, BorderLayout.NORTH);
        rightPanel.add(moveToStart, BorderLayout.NORTH);
        rightPanel.add(edit1, BorderLayout.NORTH);
        rightPanel.add(edit2, BorderLayout.NORTH);
        rightPanel.add(listOfPlaced, BorderLayout.NORTH);
        rightPanel.setMaximumSize(new Dimension(200, 1000));
        
        c.setBackground(Color.black);
        //c.setMinimumSize(new Dimension(500, 500));
        editorPanel.add(btnPanel, BorderLayout.NORTH);
        editorPanel.add(c, BorderLayout.WEST);
        
        panel.add(editorPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        //panel.add(editorPanel, BorderLayout.WEST);
        //GroupLayout layout = new GroupLayout(panel);
        //panel.setLayout(layout);

        /*layout.setHorizontalGroup(
                layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(btnPanel).addComponent(c))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(rightBtnPanel).addComponent(listOfPlaced))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(btnPanel).addComponent(rightBtnPanel))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(c, GroupLayout.Alignment.LEADING).addComponent(listOfPlaced))
        );/*

        GroupLayout.SequentialGroup leftToRight = layout.createSequentialGroup();
        GroupLayout.SequentialGroup columnLeft = layout.createSequentialGroup();
        columnLeft.addComponent(btnPanel);
        columnLeft.addComponent(c);
        leftToRight.addGroup(columnLeft);
        GroupLayout.SequentialGroup columnRight = layout.createSequentialGroup();
        columnRight.addComponent(rightBtnPanel);
        columnRight.addComponent(listOfPlaced);
        leftToRight.addGroup(columnRight);
        layout.setVerticalGroup(columnLeft);
        layout.setHorizontalGroup(leftToRight);*/
        //add(panel);
        add(panel);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                calculateSizes();
            }
        });

        (new Thread(this, "Main")).start();
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
        MobapGameEditor app = new MobapGameEditor();
        app.setVisible(true);
    }

    @Override
    public void run() {
        long sleep = 200;
        long start = 0;
        String[] listData;
        try {
            Thread.sleep(sleep * 8);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        c.init();
        c.repaint();
        while (!stopped) {
            start = System.currentTimeMillis();

            if (c.mgStruct.changed) {
                c.mgStruct.changed = false;
                listData = new String[c.mgStruct.getBufSize()];
                for (int i = 0; i < c.mgStruct.getBufSize(); i++) {
                    listData[i] = c.getShapeName(c.mgStruct.readBuf(i)[0]);
                }
                listOfPlaced.setListData(listData);
                //listOfPlaced.setFixedCellWidth(500);
                c.repaint();
            }
            
            calculateSizes();
            sleep = millis - (System.currentTimeMillis() - start);
            sleep = Math.max(sleep, 0);

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void calculateSizes() {
        w = getWidth();
        h = getHeight();
        editorPanelW = w - rightPanel.getWidth();
        bottH = h - btnPanel.getHeight();
        c.setSize(editorPanelW, bottH);
        listOfPlaced.setFixedCellWidth(rightPanel.getWidth());
    }

    public void edit(int i) {
        short[] shape = c.mgStruct.readBuf(i);/*new short[c.mgStruct.readBuf(i).length-1];
        for (int v = 0; v <  c.mgStruct.readBuf(i).length - 1; v++) {
            System.out.println(v);
            shape[v] = c.mgStruct.readBuf(i)[v+1];
        }*/
        short id = shape[0];
        JFrame jFrame = new JFrame();
        String str = Arrays.toString(shape);
        int idLength = String.valueOf(id).length() + 2; // [id] + "," + " "
        str = JOptionPane.showInputDialog(jFrame, c.mgStruct.argsDescriptions[shape[0]], str.substring(1+idLength, str.length() - 1));

        //JOptionPane.showMessageDialog(jFrame, "Your message: "+getMessage);
        System.out.println(str);
        String[] strippedStr = str.split(", ");

        // Copying character by character into array
        // using for each loop
        try {
            int it = 1;
            for (String s : strippedStr) {
                try {
                    int v = Integer.parseInt(s);
                    shape[it] = (short) v;
                    System.out.println("v:" + v);
                    it++;
                } catch (NumberFormatException ex) {

                }
            }
            shape[0] = id;
            c.mgStruct.buffer[i] = shape;
        } catch (ArrayIndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error: too many arguments");
        }

        repaint();
    }
    
    public static boolean showDialog(String title, String question) {
        JFrame jFrame = new JFrame();
        return JOptionPane.showConfirmDialog(jFrame, question, title, JOptionPane.OK_CANCEL_OPTION) == 0;
    }
    
    Color COLOR_LNDSCP = Color.decode("#4444ff");
    Color COLOR_SELECTED = Color.decode("#8822ff");
    int centrPointRadius = 2;



    short currPlacingID = 0;
    int selectedOption = -1;
    int selectedOptionInUpperRow = -1;
    int selectedInList = 0;
    int selectedIn3Row = -1;

    //Elements elements = new Elements();
    EditorCanvas editorCanvas;

    boolean inited = false;
    
    
    
    
}
