/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vipaol.mg.editor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author vipaol
 */
public class MGStruct {
    int[] clicks = {0, 1, 2, 2};
    int[] args = {0, 2, 4, 7};
    String[] shapeNames = {"terminate", "End point", "Line", "Circle"};
    String[] argsDescriptions = {"0", "x,y", "x1,y1,x2,y2", "x,y,radius,arc-angle,arc-offset,x-coefficient,y-coefficient"};
    public MGStruct() {
    }
    
    short supportedFileVer = 1;
    int length = 0;
    short fVervion = supportedFileVer;
    String name = "test" + ".mgstruct";
    short[][] buffer;
    int bufSizeInShort = 0;
    boolean changed = true;
    
    public boolean loadFile() {
        length = 0;
        try {
            File f = new File("./" + name);
            if (f.exists()) {
                InputStream is = new FileInputStream(f);
                DataInputStream dis = new DataInputStream(is);
                int available = dis.available();
                available /= 2;
                System.out.println(available + f.getAbsolutePath());
                
                fVervion = dis.readShort();
                System.out.println("file version: " + fVervion);
                boolean cancelled = false;
                
                if (fVervion != supportedFileVer) {
                    if (fVervion == 0) {
                        cancelled = !showDialog("Older file version", "Older file ver: " + fVervion + ". Actual is: " + supportedFileVer + ". This file can be converted and will be signed with version nubmer " + supportedFileVer + " on the next save. Open?");
                    } else {
                        cancelled = !showDialog("Unsupported file version", "Unsupported file ver: " + fVervion + ". Supported is: " + supportedFileVer + ". Try to open anyway?");
                    }
                }
                if (!cancelled) {
                    if (fVervion == 0) {
                        length = 16;
                    } else {
                        length = dis.readShort();
                    }
                    
                    buffer = new short[length][];
                    
                    length = 0;
                    
                    while (true) {
                        short id = dis.readShort();
                        System.out.println("read: id=" + id);
                        if (id == 0) {
                            break;
                        } else {
                            short[] data = new short[args[id] + 1];
                            data[0] = id;
                            for (int i = 1; i < args[id] + 1; i++) {
                                data[i] = dis.readShort();
                            }
                            saveToBuffer(data);
                            for (int i : data) {
                                System.out.print(i + " ");
                            }
                            System.out.println(". ");
                        }
                    }
                }
                dis.close();
                is.close();
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean saveToFile() {
        try {
            File f = new File("./" + name);
            if (!f.exists()) {
                f.createNewFile();
            } else {
                if (!showDialog("File already exists", "Replace " + name + "?")) {
                    return false;
                }
            }
            if (f.exists()) {
                OutputStream os = new FileOutputStream(f);
                DataOutputStream dis = new DataOutputStream(os);
                dis.writeShort(supportedFileVer);
                dis.writeShort(length);
                for (int j = 0; j < length; j++) {
                    short[] data = buffer[j];
                    for (int i : data) {
                        dis.writeShort(i);
                    }
                }
                dis.writeShort(0);
                dis.close();
                os.close();
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    void saveToBuffer(short[] data) {
        buffer[length] = data;
        length++;
        bufSizeInShort += data.length;
        changed = true;
    }
    short[] readBuf(int i) {
        return buffer[i];
    }
    int getBufSize() {
        return length;
    }
    
    public boolean showDialog(String title, String question) {
        JFrame jFrame = new JFrame();
        if(JOptionPane.showConfirmDialog(jFrame, question, title, JOptionPane.OK_CANCEL_OPTION) == 0) {
            return true;
        } else {
            return false;
        }
    }
}
